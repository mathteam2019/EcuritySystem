package com.nuctech.securitycheck.backgroundservice.message;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.DispatchManualDeviceInfoVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import com.nuctech.securitycheck.backgroundservice.controller.*;
import com.nuctech.securitycheck.backgroundservice.service.ISerMqMessageService;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
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

    @Autowired
    private ISerMqMessageService serMqMessageService;



    /**
     * listenDevSysMessage
     *
     * @param receiver message
     */
    @RabbitListener(queues = "${dev.sys.queue}")
    public void listenDevSysMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {

            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.register"))) {
                // 4.3.1.1 安检仪向后台服务发送注册信息
                SysRegisterModel sysRegisterModel = objectMapper.convertValue(result.getContent(), SysRegisterModel.class);//(SysRegisterModel) result.getContent();
                securitySysController.sendRegister(sysRegisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.login"))) {
                // 4.3.1.2 安检仪向后台服务发送登录信息
                SysLoginModel sysLoginModel = objectMapper.convertValue(result.getContent(), SysLoginModel.class);
                securitySysController.sendLogin(sysLoginModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.logout"))) {
                // 4.3.1.3 安检仪向后台服务发送登出信息
                SysLogoutModel sysLogoutModel = objectMapper.convertValue(result.getContent(), SysLogoutModel.class);
                securitySysController.sendLogout(sysLogoutModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.unregister"))) {
                // 4.3.1.4 安检仪向后台服务发送注销信息
                SysUnregisterModel sysUnregisterModel = objectMapper.convertValue(result.getContent(), SysUnregisterModel.class);
                securitySysController.sendUnregister(sysUnregisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.updateversion"))) {
                // 4.3.1.9 安检仪向后台服务更新版本号信息（算法切换后更新）-返回
                SysDeviceVersionModel sysDeviceVersionModel = objectMapper.convertValue(result.getContent(), SysDeviceVersionModel.class);
                result = securitySysController.sendDeviceVersion(sysDeviceVersionModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.imageinfo"))) {
                // 4.3.1.13 安检仪向后台服务发送扫描图像信息-返回
                DevSerImageInfoModel devSerImageInfoModel = objectMapper.convertValue(result.getContent(), DevSerImageInfoModel.class);
                result = securitySysController.saveScanResult(devSerImageInfoModel);

            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.manual.task"))) {
                // 4.3.1.21 安检仪向后台服务请求本机手检-返回
                SendSimpleMessageModel sendSimpleMessageModel = objectMapper.convertValue(result.getContent(), SendSimpleMessageModel.class);
                result = securitySysController.availableCheckHand(sendSimpleMessageModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.manual.conclusion"))) {
                // 4.3.1.22 安检仪本地手检站后台服务请求提交手检结论-返回
                HandSerResultModel handSerResultModel = objectMapper.convertValue(result.getContent(), HandSerResultModel.class);
                result = securitySysController.saveHandResultFromSecurity(handSerResultModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenSysDevMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${sys.dev.queue}")
    public void listenSysDevMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.config"))) {
                // 4.3.1.5 后台服务向安检仪下发配置信息
//                String guid = (String) result.getContent();
//                sysSecurityController.sendDeviceConfig(guid);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.userlist"))) {
                // 4.3.1.6 后台服务向安检仪下发用户列表
//                String guid = (String) result.getContent();
//                sysSecurityController.sendUserList(guid);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.dictionary"))) {
                // 4.3.1.7 后台服务向安检仪下发字典
//                String guid = (String) result.getContent();
//                sysSecurityController.sendDictData(guid);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.result"))) {
                // 4.3.1.15 后台服务向安检仪推送判图结论
                //SerDevJudgeGraphResultModel serDevJudgeGraphResultModel = (SerDevJudgeGraphResultModel) result.getContent();
                //sysSecurityController.sendJudgeResultToSecurityDevice(serDevJudgeGraphResultModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.dispatch.manual"))) {
                // 4.3.1.16 后台服务向安检仪推送调度的手检站信息
                //DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO = (DispatchManualDeviceInfoVO) result.getContent();
                //sysSecurityController.dispatchManual(dispatchManualDeviceInfoVO);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.overtime"))) {
                // 4.3.1.17 后台服务向安检仪推送工作超时提醒
                //DeviceOvertimeModel deviceOvertimeModel = (DeviceOvertimeModel) result.getContent();
                //sysSecurityController.monitorSecurityOvertime(deviceOvertimeModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.dev.command"))) {
                // 4.3.1.18 后台服务向安检仪推送控制信息(预留接口)
                // SecurityRemoteModel securityRemoteModel = (SecurityRemoteModel) result.getContent();
                // sysSecurityController.monitorSecurityRemote(securityRemoteModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    /**
     * listenRemSysMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${rem.sys.queue}")
    public void listenRemSysMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.register"))) {
                // 4.3.2.1 判图站向后台服务发送注册信息
                SysRegisterModel sysRegisterModel = objectMapper.convertValue(result.getContent(), SysRegisterModel.class);
                judgeSysController.sendRegister(sysRegisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.login"))) {
                // 4.3.2.2 判图站向后台服务发送登录信息
                SysLoginModel sysLoginModel = objectMapper.convertValue(result.getContent(), SysLoginModel.class);
                judgeSysController.sendLogin(sysLoginModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.logout"))) {
                // 4.3.2.3 判图站向后台服务发送登出信息
                SysLogoutModel sysLogoutModel = objectMapper.convertValue(result.getContent(), SysLogoutModel.class);
                judgeSysController.sendLogout(sysLogoutModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.unregister"))) {
                // 4.3.2.4 判图站向后台服务发送注销信息
                SysUnregisterModel sysUnregisterModel = objectMapper.convertValue(result.getContent(), SysUnregisterModel.class);
                judgeSysController.sendUnregister(sysUnregisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.start"))) {
                // 4.3.2.7 判图站向后台服务发送开始工作通知
                SysDeviceStatusModel sysDeviceStatusModel = objectMapper.convertValue(result.getContent(), SysDeviceStatusModel.class);
                judgeSysController.deviceStart(sysDeviceStatusModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.stop"))) {
                // 4.3.2.8 判图站向后台服务发送暂停工作通知
                SysDeviceStatusModel sysDeviceStatusModel = objectMapper.convertValue(result.getContent(), SysDeviceStatusModel.class);
                judgeSysController.deviceStop(sysDeviceStatusModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.result"))) {
                // 4.3.2.9 判图站向后台服务提交判图结论
                JudgeSerResultModel judgeSerResultModel = objectMapper.convertValue(result.getContent(), JudgeSerResultModel.class);
                judgeSysController.saveJudgeGraphResult(judgeSerResultModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenSysRemMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${sys.rem.queue}")
    public void listenSysRemMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());

        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenManSysMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${man.sys.queue}")
    public void listenManSysMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.register"))) {
                // 4.3.3.1 手检站向后台服务发送注册信息
                SysRegisterModel sysRegisterModel = objectMapper.convertValue(result.getContent(), SysRegisterModel.class);
                manualSysController.sendRegister(sysRegisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.login"))) {
                // 4.3.3.2 手检站向后台服务发送登录信息
                SysLoginModel sysLoginModel = objectMapper.convertValue(result.getContent(), SysLoginModel.class);
                manualSysController.sendLogin(sysLoginModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.logout"))) {
                // 4.3.3.3 手检站向后台服务发送登出信息
                SysLogoutModel sysLogoutModel = objectMapper.convertValue(result.getContent(), SysLogoutModel.class);
                manualSysController.sendLogout(sysLogoutModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.start"))) {
                // 4.3.3.4 手检站向后台服务发送开始工作通知
                SysDeviceStatusModel sysDeviceStatusModel = objectMapper.convertValue(result.getContent(), SysDeviceStatusModel.class);
                manualSysController.deviceStart(sysDeviceStatusModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.stop"))) {
                // 4.3.3.5 手检站向后台服务发送暂停工作通知
                SysDeviceStatusModel sysDeviceStatusModel = objectMapper.convertValue(result.getContent(), SysDeviceStatusModel.class);
                manualSysController.deviceStop(sysDeviceStatusModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.unregister"))) {
                // 4.3.3.6 手检战向后台服务发送注销信息
                SysUnregisterModel sysUnregisterModel = objectMapper.convertValue(result.getContent(), SysUnregisterModel.class);
                manualSysController.sendUnregister(sysUnregisterModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.result"))) {
                // 4.3.3.9 手检站后台服务请求提交手检结论
                HandSerResultModel handSerResultModel = objectMapper.convertValue(result.getContent(), HandSerResultModel.class);
                manualSysController.saveHandResult(handSerResultModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenSysManMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${sys.man.queue}")
    public void listenSysManMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());

        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.man.config"))) {
                // 4.3.3.10 后台服务向远程短下发配置信息
                // String guid = (String) result.getContent();
                // sysManualController.sendDeviceConfig(guid);
            }else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.man.dictionary"))) {
                // 4.3.3.11 后台服务向手检站下发字典
                // String guid = (String) result.getContent();
                // sysManualController.sendDictData(guid);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.man.image"))) {
                // 4.3.3.12 后台服务向手检站推送业务数据
                // SerManImageInfoModel serManImageInfoModel = (SerManImageInfoModel) result.getContent();
                // sysManualController.sendJudgeResultToHandDevice(serManImageInfoModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.man.overtime"))) {
                // 4.3.3.13 后台服务向手检端推送工作超时提醒
                // DeviceOvertimeModel deviceOvertimeModel = (DeviceOvertimeModel) result.getContent();
                // sysManualController.monitorManualOvertime(deviceOvertimeModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * listenDevSysDataMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${dev.sys.data.queue}")
    public void listenDevSysDataMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.log"))) {
                // 4.3.1.12 安检仪向后台服务发送日志信息
                SerDevLogModel serDevLogModel = objectMapper.convertValue(result.getContent(), SerDevLogModel.class);
                securitySysController.saveSerDevLog(serDevLogModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.synchronization"))) {
                // 4.3.1.14 安检仪向后台服务同步数据
                DevSerDataSyncModel devSerDataSyncModel = objectMapper.convertValue(result.getContent(), DevSerDataSyncModel.class);
                securitySysController.synchronizeScanResult(devSerDataSyncModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.rem.log"))) {
                // 4.3.2.5 判图站向后台服务发送日志信息
                SerDevLogModel serDevLogModel = objectMapper.convertValue(result.getContent(), SerDevLogModel.class);
                judgeSysController.saveSerDevLog(serDevLogModel);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.man.log"))) {
                // 4.3.3.7 手检站向后台服务发送日志信息
                SerDevLogModel serDevLogModel = objectMapper.convertValue(result.getContent(), SerDevLogModel.class);
                manualSysController.saveSerDevLog(serDevLogModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenDevSysStatusMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${dev.sys.status.queue}")
    public void listenDevSysStatusMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            String key = result.getKey();
            if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.heartbeat"))) {
                //4.3.1.8 安检仪向后台服务发送心跳信息
                HeartBeatModel heartBeatModel = objectMapper.convertValue(result.getContent(), HeartBeatModel.class);
                securitySysController.saveHeartBeatTime(heartBeatModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenDevSysStatusMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${rem.sys.status.queue}")
    public void listenRemSysStatusMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            String key = result.getKey();
            if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.rem.heartbeat"))) {
                //4.3.2.6 判图站向后台服务发送心跳信息
                HeartBeatModel heartBeatModel = objectMapper.convertValue(result.getContent(), HeartBeatModel.class);
                judgeSysController.saveHeartBeatTime(heartBeatModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenDevSysStatusMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${man.sys.status.queue}")
    public void listenManSysStatusMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            String key = result.getKey();
            if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.man.heartbeat"))) {
                //4.3.3.8 手检端向后台服务发送心跳信息
                HeartBeatModel heartBeatModel = objectMapper.convertValue(result.getContent(), HeartBeatModel.class);
                manualSysController.saveHeartBeatTime(heartBeatModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenDevSysStatusMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${dev.sys.currentstatus.queue}")
    public void listenDevSysCurrentStatusMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            String key = result.getKey();
            if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.currentstatus"))) {
                // 4.3.1.10 安检仪向后台服务发送 flow 信息（即时状态）-返回
                SerDeviceStatusModel serDeviceStatusModel = objectMapper.convertValue(result.getContent(), SerDeviceStatusModel.class);
                securitySysController.sendSerDeviceStatus(serDeviceStatusModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenDevSysStatusMessage
     *
     * @param receiver Message
     */
    @RabbitListener(queues = "${dev.sys.hardwarestatus.queue}")
    public void listenDevSysHardwareStatusMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            String key = result.getKey();
            if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.hardwarestatus"))) {
                HardwareStatusModel hardwareStatusModel = objectMapper.convertValue(result.getContent(), HardwareStatusModel.class);
                //4.3.1.11 安检仪向后台服务发送硬件状态（即时状态）
                securitySysController.sendHardwareStatus(hardwareStatusModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    @RabbitListener(queues = "${sys.dev.reply.queue}")
    public void listenSysDevReplyMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            serMqMessageService.save(result, 0, null, null,
                    CommonConstant.RESULT_SUCCESS.getValue().toString());
            log.info(msg);
        } catch (Exception e) {

        }
    }



    @RabbitListener(queues = "${dev.sys.reply.queue}")
    public void listenDevSysReplyMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
//            serMqMessageService.save(result, 0, null, null,
//                    CommonConstant.RESULT_SUCCESS.getValue().toString());
//            log.info(msg);
        } catch (Exception e) {

        }
    }

    @RabbitListener(queues = "${sys.rem.reply.queue}")
    public void listenSysRemReplyMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            serMqMessageService.save(result, 0, null, null,
                    CommonConstant.RESULT_SUCCESS.getValue().toString());
            log.info(msg);
        } catch (Exception e) {

        }
    }

    @RabbitListener(queues = "${rem.sys.reply.queue}")
    public void listenRemSysReplyMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
//            serMqMessageService.save(result, 0, null, null,
//                    CommonConstant.RESULT_SUCCESS.getValue().toString());
            log.info(msg);
        } catch (Exception e) {

        }
    }

    @RabbitListener(queues = "${sys.man.reply.queue}")
    public void listenSysManReplyMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            serMqMessageService.save(result, 0, null, null,
                    CommonConstant.RESULT_SUCCESS.getValue().toString());
            log.info(msg);
        } catch (Exception e) {

        }
    }

    @RabbitListener(queues = "${man.sys.reply.queue}")
    public void listenManSysReplyMessage(Message receiver) {

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = new String(receiver.getBody());
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
//            serMqMessageService.save(result, 0, null, null,
//                    CommonConstant.RESULT_SUCCESS.getValue().toString());
            log.info(msg);
        } catch (Exception e) {

        }
    }



    /**
     * listenZabbixQueue
     *
     * @param receiver Message
     */
    //@RabbitListener(queues = "${zabbix.queue}")
    public void listenZabbixQueue(Message receiver) {
        
        String msg = new String(receiver.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }



}
