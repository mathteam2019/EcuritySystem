package com.nuctech.securitycheck.backgroundservice.message;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.DispatchManualDeviceInfoVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import com.nuctech.securitycheck.backgroundservice.controller.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MessageListener
 *
 * @author LovelyXing
 * @version v1.0
 * @since 2019-11-29
 */
@Component
@Slf4j
public class MessageListener {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private SecuritySysController securitySysController;

    @Autowired
    private SysSecurityController sysSecurityController;

    @Autowired
    private JudgeSysController judgeSysController;

    @Autowired
    private SysJudgeController sysJudgeController;

    @Autowired
    private ManualSysController manualSysController;

    @Autowired
    private SysManualController sysManualController;

    /**
     * listenDevSysMessage
     *
     * @param msg message
     */
    @RabbitListener(queues = "${dev.sys.queue}")
    public void listenDevSysMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.register"))) {
                // 4.3.1.1 安检仪向后台服务发送注册信息
                SysRegisterModel sysRegisterModel = (SysRegisterModel) result.getContent();
                securitySysController.sendRegister(sysRegisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.login"))) {
                // 4.3.1.2 安检仪向后台服务发送登录信息
                SysLoginModel sysLoginModel = (SysLoginModel) result.getContent();
                securitySysController.sendLogin(sysLoginModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.logout"))) {
                // 4.3.1.3 安检仪向后台服务发送登出信息
                SysLogoutModel sysLogoutModel = (SysLogoutModel) result.getContent();
                securitySysController.sendLogout(sysLogoutModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.unregister"))) {
                // 4.3.1.4 安检仪向后台服务发送注销信息
                SysUnregisterModel sysUnregisterModel = (SysUnregisterModel) result.getContent();
                securitySysController.sendUnregister(sysUnregisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.updateversion"))) {
                // 4.3.1.9 安检仪向后台服务更新版本号信息（算法切换后更新）-返回
                SysDeviceVersionModel sysDeviceVersionModel = (SysDeviceVersionModel) result.getContent();
                result = securitySysController.sendDeviceVersion(sysDeviceVersionModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.imageinfo"))) {
                // 4.3.1.13 安检仪向后台服务发送扫描图像信息-返回
                DevSerImageInfoModel devSerImageInfoModel = (DevSerImageInfoModel) result.getContent();
                result = securitySysController.saveScanResult(devSerImageInfoModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenSysDevMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${sys.dev.queue}")
    public void listenSysDevMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("dev.config"))) {
                // 4.3.1.5 后台服务向安检仪下发配置信息
                String guid = (String) result.getContent();
                sysSecurityController.sendDeviceConfig(guid);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.userlist"))) {
                // 4.3.1.6 后台服务向安检仪下发用户列表
                String guid = (String) result.getContent();
                sysSecurityController.sendUserList(guid);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.dictionary"))) {
                // 4.3.1.7 后台服务向安检仪下发字典
                String guid = (String) result.getContent();
                sysSecurityController.sendDictData(guid);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.result"))) {
                // 4.3.1.15 后台服务向安检仪推送判图结论
                SerDevJudgeGraphResultModel serDevJudgeGraphResultModel = (SerDevJudgeGraphResultModel) result.getContent();
                sysSecurityController.sendJudgeResultToSecurityDevice(serDevJudgeGraphResultModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.dispatch.manual"))) {
                // 4.3.1.16 后台服务向安检仪推送调度的手检站信息
                DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO = (DispatchManualDeviceInfoVO) result.getContent();
                //sysSecurityController.dispatchManual(dispatchManualDeviceInfoVO);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.overtime"))) {
                // 4.3.1.17 后台服务向安检仪推送工作超时提醒
                DeviceOvertimeModel deviceOvertimeModel = (DeviceOvertimeModel) result.getContent();
                sysSecurityController.monitorSecurityOvertime(deviceOvertimeModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.command"))) {
                // 4.3.1.18 后台服务向安检仪推送控制信息(预留接口)
                SecurityRemoteModel securityRemoteModel = (SecurityRemoteModel) result.getContent();
                sysSecurityController.monitorSecurityRemote(securityRemoteModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    /**
     * listenRemSysMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${rem.sys.queue}")
    public void listenRemSysMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.register"))) {
                // 4.3.2.1 判图站向后台服务发送注册信息
                SysRegisterModel sysRegisterModel = (SysRegisterModel) result.getContent();
                judgeSysController.sendRegister(sysRegisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.login"))) {
                // 4.3.2.2 判图站向后台服务发送登录信息
                SysLoginModel sysLoginModel = (SysLoginModel) result.getContent();
                judgeSysController.sendLogin(sysLoginModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.logout"))) {
                // 4.3.2.3 判图站向后台服务发送登出信息
                SysLogoutModel sysLogoutModel = (SysLogoutModel) result.getContent();
                judgeSysController.sendLogout(sysLogoutModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.unregister"))) {
                // 4.3.2.4 判图站向后台服务发送注销信息
                SysUnregisterModel sysUnregisterModel = (SysUnregisterModel) result.getContent();
                judgeSysController.sendUnregister(sysUnregisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.start"))) {
                // 4.3.2.7 判图站向后台服务发送开始工作通知
                SysDeviceStatusModel sysDeviceStatusModel = objectMapper.readValue(JSONObject.toJSONString(result.getContent()), SysDeviceStatusModel.class);
                judgeSysController.deviceStart(sysDeviceStatusModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.stop"))) {
                // 4.3.2.8 判图站向后台服务发送暂停工作通知
                SysDeviceStatusModel sysDeviceStatusModel = objectMapper.readValue(JSONObject.toJSONString(result.getContent()), SysDeviceStatusModel.class);
                judgeSysController.deviceStop(sysDeviceStatusModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.result"))) {
                // 4.3.2.9 判图站向后台服务提交判图结论
                JudgeSerResultModel judgeSerResultModel = objectMapper.readValue(JSONObject.toJSONString(result.getContent()), JudgeSerResultModel.class);
                judgeSysController.saveJudgeGraphResult(judgeSerResultModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenSysRemMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${sys.rem.queue}")
    public void listenSysRemMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        /**
         * todo
         * 4.3.2.10
         */
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.rem.config"))) {
                // 4.3.2.10 后台服务向远程短下发配置信息
                String guid = (String) result.getContent();
                sysJudgeController.sendDeviceConfig(guid);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.rem.image"))) {
                // 4.3.2.11 后台服务向判图站推送待判图图像信息
                SerJudgeImageInfoModel serJudgeImageInfoModel = (SerJudgeImageInfoModel) result.getContent();
                //sysJudgeController.sendJudgeImageInfo(serJudgeImageInfoModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.rem.overtime"))) {
                // 4.3.2.12 后台服务向判图站推送工作超时提醒
                DeviceOvertimeModel deviceOvertimeModel = (DeviceOvertimeModel) result.getContent();
                sysJudgeController.monitorJudgeOvertime(deviceOvertimeModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenManSysMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${man.sys.queue}")
    public void listenManSysMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.register"))) {
                // 4.3.3.1 手检站向后台服务发送注册信息
                SysRegisterModel sysRegisterModel = (SysRegisterModel) result.getContent();
                manualSysController.sendRegister(sysRegisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.login"))) {
                // 4.3.3.2 手检站向后台服务发送登录信息
                SysLoginModel sysLoginModel = (SysLoginModel) result.getContent();
                manualSysController.sendLogin(sysLoginModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.logout"))) {
                // 4.3.3.3 手检站向后台服务发送登出信息
                SysLogoutModel sysLogoutModel = (SysLogoutModel) result.getContent();
                manualSysController.sendLogout(sysLogoutModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.start"))) {
                // 4.3.3.4 手检站向后台服务发送开始工作通知
                SysDeviceStatusModel sysDeviceStatusModel = (SysDeviceStatusModel) result.getContent();
                manualSysController.deviceStart(sysDeviceStatusModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.stop"))) {
                // 4.3.3.5 手检站向后台服务发送暂停工作通知
                SysDeviceStatusModel sysDeviceStatusModel = (SysDeviceStatusModel) result.getContent();
                manualSysController.deviceStop(sysDeviceStatusModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.unregister"))) {
                // 4.3.3.6 手检战向后台服务发送注销信息
                SysUnregisterModel sysUnregisterModel = (SysUnregisterModel) result.getContent();
                manualSysController.sendUnregister(sysUnregisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.result"))) {
                // 4.3.3.9 手检站后台服务请求提交手检结论
                HandSerResultModel handSerResultModel = (HandSerResultModel) result.getContent();
                manualSysController.saveHandResult(handSerResultModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenSysManMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${sys.man.queue}")
    public void listenSysManMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.man.config"))) {
                // 4.3.3.10 后台服务向远程短下发配置信息
                String guid = (String) result.getContent();
                sysManualController.sendDeviceConfig(guid);
            }else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.man.dictionary"))) {
                // 4.3.3.11 后台服务向手检站下发字典
                String guid = (String) result.getContent();
                sysManualController.sendDictData(guid);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.man.image"))) {
                // 4.3.3.12 后台服务向手检站推送业务数据
                SerManImageInfoModel serManImageInfoModel = (SerManImageInfoModel) result.getContent();
                sysManualController.sendJudgeResultToHandDevice(serManImageInfoModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.man.overtime"))) {
                // 4.3.3.13 后台服务向手检端推送工作超时提醒
                DeviceOvertimeModel deviceOvertimeModel = (DeviceOvertimeModel) result.getContent();
                sysManualController.monitorManualOvertime(deviceOvertimeModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenDevSysDataMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${dev.sys.data.queue}")
    public void listenDevSysDataMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.log"))) {
                // 4.3.1.12 安检仪向后台服务发送日志信息
                SerDevLogModel serDevLogModel = (SerDevLogModel) result.getContent();
                securitySysController.saveSerDevLog(serDevLogModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.synchronization"))) {
                // 4.3.1.14 安检仪向后台服务同步数据
                DevSerDataSyncModel devSerDataSyncModel = (DevSerDataSyncModel) result.getContent();
                securitySysController.synchronizeScanResult(devSerDataSyncModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.rem.log"))) {
                // 4.3.2.5 判图站向后台服务发送日志信息
                SerDevLogModel serDevLogModel = (SerDevLogModel) result.getContent();
                judgeSysController.saveSerDevLog(serDevLogModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.man.log"))) {
                // 4.3.3.7 手检站向后台服务发送日志信息
                SerDevLogModel serDevLogModel = (SerDevLogModel) result.getContent();
                manualSysController.saveSerDevLog(serDevLogModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenDevSysStatusMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${dev.sys.status.queue}")
    public void listenDevSysStatusMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            String key = result.getKey();
            if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.currentstatus"))) {
                // 4.3.1.10 安检仪向后台服务发送 flow 信息（即时状态）-返回
                SerDeviceStatusModel serDeviceStatusModel = (SerDeviceStatusModel) result.getContent();
                securitySysController.sendSerDeviceStatus(serDeviceStatusModel);
            } else if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.heartbeat"))) {
                //4.3.1.8 安检仪向后台服务发送心跳信息
                HeartBeatModel heartBeatModel = (HeartBeatModel) result.getContent();
                securitySysController.saveHeartBeatTime(heartBeatModel);
            } else if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.hardwarestatus"))) {
                HardwareStatusModel hardwareStatusModel = (HardwareStatusModel) result.getContent();
                //4.3.1.11 安检仪向后台服务发送硬件状态（即时状态）
                securitySysController.sendHardwareStatus(hardwareStatusModel);
            } else if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.rem.heartbeat"))) {
                //4.3.2.6 判图站向后台服务发送心跳信息
                HeartBeatModel heartBeatModel = (HeartBeatModel) result.getContent();
                judgeSysController.saveHeartBeatTime(heartBeatModel);
            } else if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.man.heartbeat"))) {
                //4.3.3.8 手检端向后台服务发送心跳信息
                HeartBeatModel heartBeatModel = (HeartBeatModel) result.getContent();
                manualSysController.saveHeartBeatTime(heartBeatModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenZabbixQueue
     *
     * @param msg Message
     */
    //@RabbitListener(queues = "${zabbix.queue}")
    public void listenZabbixQueue(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
