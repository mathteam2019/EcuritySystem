package com.nuctech.securitycheck.backgroundservice.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisUtil redisUtil;

    private Object getSendMqObject(ResultMessageVO resultMessageVO) {
        //return CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        return resultMessageVO;
    }

    /**
     * sendDevRegisterMessage
     *
     * @param resultMessageVO Message
     */
    public void sendDevRegisterMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.register"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendDevUnregisterMessage
     *
     * @param resultMessageVO Message
     */
    public void sendDevUnregisterMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.unregister"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendDevLoginMessage
     *
     * @param resultMessageVO Message
     */
    public void sendDevLoginMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.login"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendDevLogoutMessage
     *
     * @param resultMessageVO Message
     */
    public void sendDevLogoutMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.logout"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendRemRegisterMessage
     *
     * @param resultMessageVO Message
     */
    public void sendRemRegisterMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.register"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendSysRemMessage
     *
     * @param resultMessageVO encryptMessage
     * @param routingKey routingKey
     */
    public void sendDevSysMessage(ResultMessageVO resultMessageVO, String routingKey) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                routingKey,
                getSendMqObject(resultMessageVO)
        );
    }

    /**
     * sendRemUnregisterMessage
     *
     * @param resultMessageVO Message
     */
    public void sendRemUnregisterMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.unregister"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendRemLoginMessage
     *
     * @param resultMessageVO Message
     */
    public void sendRemLoginMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.login"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendRemLogoutMessage
     *
     * @param resultMessageVO Message
     */
    public void sendRemLogoutMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.logout"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendManRegisterMessage
     *
     * @param resultMessageVO Message
     */
    public void sendManRegisterMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.register"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendManUnregisterMessage
     *
     * @param resultMessageVO Message
     */
    public void sendManUnregisterMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.unregister"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendManLoginMessage
     *
     * @param resultMessageVO Message
     */
    public void sendManLoginMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.login"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendManLogoutMessage
     *
     * @param resultMessageVO Message
     */
    public void sendManLogoutMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.logout"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendSysDeviceVersionReplyMessage
     *
     * @param resultMessageVO Message
     */
    public void sendSysDeviceVersionReplyMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.updateversion"),
                getSendMqObject(resultMessageVO)
        );
    }

    /**
     * sendSerDeviceStatusReplyMessage
     *
     * @param resultMessageVO Message
     */
    public void sendSerDeviceStatusReplyMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.dev.sys.status"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.currentstatus"),
                getSendMqObject(resultMessageVO)
        );
    }

    /**
     * sendSerDevLogReplyMessage
     *
     * @param resultMessageVO
     * @param exchangeName
     * @param routingKey
     */
    public void sendSerDevLogReplyMessage(ResultMessageVO resultMessageVO, String exchangeName, String routingKey) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, getSendMqObject(resultMessageVO));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * sendSysRemMessage
     *
     * @param resultMessageVO encryptMessage
     * @param routingKey routingKey
     */
    public void sendSysRemMessage(ResultMessageVO resultMessageVO, String routingKey) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                routingKey,
                getSendMqObject(resultMessageVO)
        );
    }

    /**
     * sendSysRemReplyMessage
     *
     * @param resultMessageVO
     * @param routingKey
     */
    public void sendSysRemReplyMessage(ResultMessageVO resultMessageVO, String routingKey) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                routingKey,
                getSendMqObject(resultMessageVO)
        );
    }


    /**
     * sendSysManReplyMessage
     *
     * @param resultMessageVO
     * @param routingKey
     */
    public void sendSysManReplyMessage(ResultMessageVO resultMessageVO, String routingKey) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                routingKey,
                getSendMqObject(resultMessageVO)
        );
    }

    /**
     * sendHardwareStatusReplyMessage
     *
     * @param resultMessageVO Message
     */
    public void sendHardwareStatusReplyMessage(ResultMessageVO resultMessageVO) {
        redisUtil.set(BackgroundServiceUtil.getConfig("routingKey.sys.hardwarestatus"),
                CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO)), CommonConstant.EXPIRE_TIME.getValue());
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys.status"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.hardwarestatus"), getSendMqObject(resultMessageVO));
    }

    /**
     * cronJobSecurityOvertime
     *
     * @param resultMessageVO encryptedMessage
     */
    public void cronJobSecurityOvertime(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.sys.dev"),
                BackgroundServiceUtil.getConfig("routingKey.dev.overtime"), getSendMqObject(resultMessageVO));
    }

    /**
     * sendSysRemMessage
     *
     * @param resultMessageVO encryptMessage
     * @param routingKey routingKey
     */
    public void sendSysDevMessage(ResultMessageVO resultMessageVO, String routingKey) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.sys.dev"),
                routingKey,
                getSendMqObject(resultMessageVO)
        );
    }

    /**
     * cronJobHandOvertime
     *
     * @param resultMessageVO encryptedMessage
     */
    public void cronJobHandOvertime(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.sys.man"),
                BackgroundServiceUtil.getConfig("routingKey.man.overtime"), getSendMqObject(resultMessageVO));
    }

    /**
     * cronJobJudgeOvertime
     *
     * @param resultMessageVO encryptedMessage
     */
    public void cronJobJudgeOvertime(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.sys.rem"),
                BackgroundServiceUtil.getConfig("routingKey.rem.overtime"), getSendMqObject(resultMessageVO));
    }

    /**
     * cronJobLowDiskSpace
     *
     * @param resultMessageVO encryptedMessage
     */
    public void cronJobLowDiskSpace(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.zabbix"),
                BackgroundServiceUtil.getConfig("routingKey.zabbix"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendDispatchManual
     *
     * @param resultMessageVO encryptedMessage
     */
    public void sendDispatchManual(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.sys.dev"),
                BackgroundServiceUtil.getConfig("routingKey.dev.dispatch.manual"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendSysDeviceVersionMessage
     *
     * @param topicExchange topicExchange
     * @param routingKey    routingKey
     * @param resultMessageVO           ResultMessageVO
     */
    public void sendSysDeviceVersionMessage(String topicExchange, String routingKey, ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(topicExchange, routingKey, getSendMqObject(resultMessageVO));
    }

    /**
     * sendDeviceConfigMessage
     *
     * @param resultMessageVO ResultMessage
     * @param exchangeName  exchangeName
     * @param routingKey    routingKey
     */
    public void sendDeviceConfigMessage(ResultMessageVO resultMessageVO, String exchangeName, String routingKey) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, getSendMqObject(resultMessageVO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * sendDevUserList
     *
     * @param resultMessageVO ResultMessage
     * @param exchangeName  exchangeName
     * @param routingKey    routingKey
     */
    public void sendDevUserList(ResultMessageVO resultMessageVO, String exchangeName, String routingKey) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, getSendMqObject(resultMessageVO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void allSysRegister(ResultMessageVO resultMessageVO) {
        try {
            rabbitTemplate.convertAndSend(
                    BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                    resultMessageVO.getKey(), getSendMqObject(resultMessageVO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * sendDevDictionary
     *
     * @param resultMessageVO ResultMessage
     * @param exchangeName  exchangeName
     * @param routingKey    routingKey
     */
    public void sendDevDictionary(ResultMessageVO resultMessageVO, String exchangeName, String routingKey) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, getSendMqObject(resultMessageVO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * sendHeartBeatReplyMessage
     *
     * @param resultMessageVO ResultMessage
     * @param exchangeName  exchangeName
     * @param routingKey    routingKey
     */
    public void sendHeartBeatReplyMessage(ResultMessageVO resultMessageVO, String exchangeName, String routingKey) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, getSendMqObject(resultMessageVO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * sendSysDeviceSecurityInfoSaveReplyMessage
     *
     * @param resultMessageVO Message
     */
    public void sendSysDeviceSecurityInfoSaveReplyMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.imageinfo"), getSendMqObject(resultMessageVO));
    }

    /**
     * sendSysDeviceSecurityInfoSynchronizeReplyMessage
     *
     * @param resultMessageVO Message
     */
    public void sendSysDeviceSecurityInfoSynchronizeReplyMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.dev.sys.data"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.synchronization"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendJudgeGraphResultReplyMessage
     *
     * @param resultMessageVO Message
     */
    public void sendJudgeGraphResultReplyMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.result"),
                getSendMqObject(resultMessageVO));
    }

    /**
     * sendSaveHandResultReplyMessage
     *
     * @param resultMessageVO Message
     */
    public void sendSaveHandResultReplyMessage(ResultMessageVO resultMessageVO) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.result"), getSendMqObject(resultMessageVO));
    }

    /**
     * sendImageInfoToJudge
     *
     * @param resultMessageVO ResultMessage
     */
    public void sendImageInfoToJudge(ResultMessageVO resultMessageVO) {
        try {
            rabbitTemplate.convertAndSend(
                    BackgroundServiceUtil.getConfig("topic.inter.sys.rem"),
                    resultMessageVO.getKey(),
                    getSendMqObject(resultMessageVO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * sendJudgeInfoToSecurity
     *
     * @param resultMessageVO ResultMessage
     */
    public void sendJudgeInfoToSecurity(ResultMessageVO resultMessageVO) {
        try {
            rabbitTemplate.convertAndSend(
                    BackgroundServiceUtil.getConfig("topic.inter.sys.dev"),
                    resultMessageVO.getKey(), getSendMqObject(resultMessageVO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * sendJudgeInfoToHandDevice
     *
     * @param resultMessageVO ResultMessage
     */
    public void sendJudgeInfoToHandDevice(ResultMessageVO resultMessageVO) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(
                    BackgroundServiceUtil.getConfig("topic.inter.sys.man"),
                    resultMessageVO.getKey(),
                    getSendMqObject(resultMessageVO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
