package com.nuctech.securitycheck.backgroundservice.message;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceStatusType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import com.nuctech.securitycheck.backgroundservice.controller.JudgeSysController;
import com.nuctech.securitycheck.backgroundservice.controller.ManualSysController;
import com.nuctech.securitycheck.backgroundservice.controller.SecuritySysController;
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
    private JudgeSysController judgeSysController;

    @Autowired
    private ManualSysController manualSysController;

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
     * listenSysDevReplyMessage
     *
     * @param msg Message
     */
    //@RabbitListener(queues = "${sys.dev.reply.queue}")
    public void listenSysDevReplyMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenDevSysReplyMessage
     *
     * @param msg message
     */
    @RabbitListener(queues = "${dev.sys.reply.queue}")
    public void listenDevSysReplyMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenSysDevMessage
     *
     * @param msg Message
     */
    //@RabbitListener(queues = "${sys.dev.queue}")
    public void listenSysDevMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
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
                judgeSysController.deviceStartAndStop(sysDeviceStatusModel, DeviceStatusType.START.getValue(), result);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.stop"))) {
                // 4.3.2.8 判图站向后台服务发送暂停工作通知
                SysDeviceStatusModel sysDeviceStatusModel = objectMapper.readValue(JSONObject.toJSONString(result.getContent()), SysDeviceStatusModel.class);
                judgeSysController.deviceStartAndStop(sysDeviceStatusModel, DeviceStatusType.STOP.getValue(), result);
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
     * listenRemSysReplyMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${rem.sys.reply.queue}")
    public void listenRemSysReplyMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenSysRemMessage
     *
     * @param msg Message
     */
    //@RabbitListener(queues = "${sys.rem.queue}")
    public void listenSysRemMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenSysRemReplyMessage
     *
     * @param msg Message
     */
    //@RabbitListener(queues = "${sys.rem.reply.queue}")
    public void listenSysRemReplyMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
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
                result = judgeSysController.deviceStartAndStop(sysDeviceStatusModel, DeviceStatusType.START.getValue(), result);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.stop"))) {
                // 4.3.3.5 手检站向后台服务发送暂停工作通知
                SysDeviceStatusModel sysDeviceStatusModel = (SysDeviceStatusModel) result.getContent();
                result = judgeSysController.deviceStartAndStop(sysDeviceStatusModel, DeviceStatusType.STOP.getValue(), result);
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
     * listenManSysReplyMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${man.sys.reply.queue}")
    public void listenManSysReplyMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenSysManMessage
     *
     * @param msg Message
     */
    //@RabbitListener(queues = "${sys.man.queue}")
    public void listenSysManMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenSysManReplyMessage
     *
     * @param msg Message
     */
    //@RabbitListener(queues = "${sys.man.reply.queue}")
    public void listenSysManReplyMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
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
                DeviceType deviceType = DeviceType.SECURITY;
                securitySysController.saveSerDevLog(serDevLogModel, deviceType);
            } else if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.synchronization"))) {
                // 4.3.1.14 安检仪向后台服务同步数据
                DevSerDataSyncModel devSerDataSyncModel = (DevSerDataSyncModel) result.getContent();
                securitySysController.synchronizeScanResult(devSerDataSyncModel);
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
                DeviceType deviceType = DeviceType.SECURITY;
                HeartBeatModel heartBeatModel = (HeartBeatModel) result.getContent();
                securitySysController.saveHeartBeatTime(heartBeatModel, deviceType);
            } else if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.hardwarestatus"))) {
                HardwareStatusModel hardwareStatusModel = (HardwareStatusModel) result.getContent();
                //4.3.1.11 安检仪向后台服务发送硬件状态（即时状态）
                securitySysController.sendHardwareStatus(hardwareStatusModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenManSysDataMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${man.sys.data.queue}")
    public void listenManSysDataMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.log"))) {
                // 4.3.3.7 手检站向后台服务发送日志信息
                SerDevLogModel serDevLogModel = (SerDevLogModel) result.getContent();
                DeviceType deviceType = DeviceType.MANUAL;
                securitySysController.saveSerDevLog(serDevLogModel, deviceType);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenManSysStatusMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${man.sys.status.queue}")
    public void listenManSysStatusMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            String key = result.getKey();
            if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.heartbeat"))) {
                // 4.3.3.8 手检端向后台服务发送心跳信息
                HeartBeatModel heartBeatModel = (HeartBeatModel) result.getContent();
                DeviceType deviceType = DeviceType.MANUAL;
                securitySysController.saveHeartBeatTime(heartBeatModel, deviceType);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenRemSysDataMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${rem.sys.data.queue}")
    public void listenRemSysDataMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            if (result.getKey().equals(BackgroundServiceUtil.getConfig("routingKey.sys.log"))) {
                // 4.3.2.5 判图站向后台服务发送日志信息
                SerDevLogModel serDevLogModel = (SerDevLogModel) result.getContent();
                DeviceType deviceType = DeviceType.JUDGE;
                securitySysController.saveSerDevLog(serDevLogModel, deviceType);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * listenRemSysStatusMessage
     *
     * @param msg Message
     */
    @RabbitListener(queues = "${rem.sys.status.queue}")
    public void listenRemSysStatusMessage(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = CryptUtil.decrypt(msg);
            ResultMessageVO result = objectMapper.readValue(msg, ResultMessageVO.class);
            String key = result.getKey();
            if (key.equals(BackgroundServiceUtil.getConfig("routingKey.sys.heartbeat"))) {
                // 4.3.2.6 判图站向后台服务发送心跳信息
                HeartBeatModel heartBeatModel = (HeartBeatModel) result.getContent();
                DeviceType deviceType = DeviceType.JUDGE;
                securitySysController.saveHeartBeatTime(heartBeatModel, deviceType);
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
