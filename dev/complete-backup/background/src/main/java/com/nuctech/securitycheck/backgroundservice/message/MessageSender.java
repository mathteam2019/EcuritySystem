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

    /**
     * sendDevRegisterMessage
     *
     * @param encryptMsg Message
     */
    public void sendDevRegisterMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.register"),
                encryptMsg);
    }

    /**
     * sendDevUnregisterMessage
     *
     * @param encryptMsg Message
     */
    public void sendDevUnregisterMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.unregister"),
                encryptMsg);
    }

    /**
     * sendDevLoginMessage
     *
     * @param encryptMsg Message
     */
    public void sendDevLoginMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.login"),
                encryptMsg);
    }

    /**
     * sendDevLogoutMessage
     *
     * @param encryptMsg Message
     */
    public void sendDevLogoutMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.logout"),
                encryptMsg);
    }

    /**
     * sendRemRegisterMessage
     *
     * @param encryptMsg Message
     */
    public void sendRemRegisterMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.register"),
                encryptMsg);
    }

    /**
     * sendRemUnregisterMessage
     *
     * @param encryptMsg Message
     */
    public void sendRemUnregisterMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.unregister"),
                encryptMsg);
    }

    /**
     * sendRemLoginMessage
     *
     * @param encryptMsg Message
     */
    public void sendRemLoginMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.login"),
                encryptMsg);
    }

    /**
     * sendRemLogoutMessage
     *
     * @param encryptMsg Message
     */
    public void sendRemLogoutMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.logout"),
                encryptMsg);
    }

    /**
     * sendManRegisterMessage
     *
     * @param encryptMsg Message
     */
    public void sendManRegisterMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.register"),
                encryptMsg);
    }

    /**
     * sendManUnregisterMessage
     *
     * @param encryptMsg Message
     */
    public void sendManUnregisterMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.unregister"),
                encryptMsg);
    }

    /**
     * sendManLoginMessage
     *
     * @param encryptMsg Message
     */
    public void sendManLoginMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.login"),
                encryptMsg);
    }

    /**
     * sendManLogoutMessage
     *
     * @param encryptMsg Message
     */
    public void sendManLogoutMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.logout"),
                encryptMsg);
    }

    /**
     * sendSysDeviceVersionReplyMessage
     *
     * @param encryptMsg Message
     */
    public void sendSysDeviceVersionReplyMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.updateversion"),
                encryptMsg
        );
    }

    /**
     * sendSerDeviceStatusReplyMessage
     *
     * @param encryptMsg Message
     */
    public void sendSerDeviceStatusReplyMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.dev.sys.status"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.currentstatus"),
                encryptMsg
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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * sendSysRemMessage
     *
     * @param encryptMsg encryptMessage
     * @param routingKey routingKey
     */
    public void sendSysRemMessage(String encryptMsg, String routingKey) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig(routingKey),
                encryptMsg
        );
    }

    /**
     * sendSysRemReplyMessage
     *
     * @param encryptMsg
     * @param routingKey
     */
    public void sendSysRemReplyMessage(String encryptMsg, String routingKey) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                routingKey,
                encryptMsg
        );
    }


    /**
     * sendSysManReplyMessage
     *
     * @param encryptMsg
     * @param routingKey
     */
    public void sendSysManReplyMessage(String encryptMsg, String routingKey) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                routingKey,
                encryptMsg
        );
    }

    /**
     * sendHardwareStatusReplyMessage
     *
     * @param encryptedContent Message
     */
    public void sendHardwareStatusReplyMessage(String encryptedContent) {
        ObjectMapper objMapper = new ObjectMapper();
        redisUtil.set(BackgroundServiceUtil.getConfig("routingKey.sys.hardwarestatus"),
                encryptedContent, CommonConstant.EXPIRE_TIME.getValue());
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.hardwarestatus"), encryptedContent);
    }

    /**
     * cronJobSecurityOvertime
     *
     * @param encryptedContent encryptedMessage
     */
    public void cronJobSecurityOvertime(String encryptedContent) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.sys.dev"),
                BackgroundServiceUtil.getConfig("routingKey.reply.dev.overtime"), encryptedContent);
    }

    /**
     * cronJobHandOvertime
     *
     * @param encryptedContent encryptedMessage
     */
    public void cronJobHandOvertime(String encryptedContent) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.sys.man"),
                BackgroundServiceUtil.getConfig("routingKey.reply.man.overtime"), encryptedContent);
    }

    /**
     * cronJobJudgeOvertime
     *
     * @param encryptedContent encryptedMessage
     */
    public void cronJobJudgeOvertime(String encryptedContent) {
        rabbitTemplate.convertAndSend(BackgroundServiceUtil.getConfig("topic.inter.sys.rem"),
                BackgroundServiceUtil.getConfig("routingKey.reply.rem.overtime"), encryptedContent);
    }

    /**
     * cronJobLowDiskSpace
     *
     * @param encryptedContent encryptedMessage
     */
    public void cronJobLowDiskSpace(String encryptedContent) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.zabbix"),
                BackgroundServiceUtil.getConfig("routingKey.zabbix"),
                encryptedContent);
    }

    /**
     * sendDispatchManual
     *
     * @param encryptedContent encryptedMessage
     */
    public void sendDispatchManual(String encryptedContent) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.sys.dev"),
                BackgroundServiceUtil.getConfig("routingKey.reply.dev.dispatch.manual"),
                encryptedContent);
    }

    /**
     * sendSysDeviceVersionMessage
     *
     * @param topicExchange topicExchange
     * @param routingKey    routingKey
     * @param obj           Object
     */
    public void sendSysDeviceVersionMessage(String topicExchange, String routingKey, Object obj) {
        rabbitTemplate.convertAndSend(topicExchange, routingKey, obj);
    }

    /**
     * sendDeviceConfigMessage
     *
     * @param resultMessageVO ResultMessage
     * @param exchangeName  exchangeName
     * @param routingKey    routingKey
     */
    public void sendDeviceConfigMessage(ResultMessageVO resultMessageVO, String exchangeName, String routingKey) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
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
            rabbitTemplate.convertAndSend(exchangeName, routingKey, CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * sendSysDeviceSecurityInfoSaveReplyMessage
     *
     * @param encryptMsg Message
     */
    public void sendSysDeviceSecurityInfoSaveReplyMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.imageinfo"), encryptMsg);
    }

    /**
     * sendSysDeviceSecurityInfoSynchronizeReplyMessage
     *
     * @param encryptMsg Message
     */
    public void sendSysDeviceSecurityInfoSynchronizeReplyMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.dev.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.synchronization"),
                encryptMsg);
    }

    /**
     * sendJudgeGraphResultReplyMessage
     *
     * @param encryptMsg Message
     */
    public void sendJudgeGraphResultReplyMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.rem.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.result"),
                encryptMsg);
    }

    /**
     * sendSaveHandResultReplyMessage
     *
     * @param encryptMsg Message
     */
    public void sendSaveHandResultReplyMessage(String encryptMsg) {
        rabbitTemplate.convertAndSend(
                BackgroundServiceUtil.getConfig("topic.inter.man.sys"),
                BackgroundServiceUtil.getConfig("routingKey.reply.sys.result"), encryptMsg);
    }

    /**
     * sendImageInfoToJudge
     *
     * @param resultMessageVO ResultMessage
     */
    public void sendImageInfoToJudge(ResultMessageVO resultMessageVO) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(
                    BackgroundServiceUtil.getConfig("topic.inter.sys.rem"),
                    resultMessageVO.getKey(),
                    CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(
                    BackgroundServiceUtil.getConfig("topic.inter.sys.dev"),
                    resultMessageVO.getKey(), CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
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
                    CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
