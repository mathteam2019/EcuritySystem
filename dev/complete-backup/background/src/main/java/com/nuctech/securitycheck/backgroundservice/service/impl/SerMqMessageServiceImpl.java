package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerDevLog;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerMqMessage;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import com.nuctech.securitycheck.backgroundservice.repositories.SerDevLogRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SerMqMessageRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISerDevLogService;
import com.nuctech.securitycheck.backgroundservice.service.ISerMqMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SerMqMessageServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2020-01-09
 */
@Service
@Transactional
public class SerMqMessageServiceImpl implements ISerMqMessageService {

    @Autowired
    private SerMqMessageRepository serMqMessageRepository;

    /**
     * 更新 或者 保存
     *
     * @param resultMessageVO
     * @param type
     * @return SerMqMessage
     */
    @Override
    public SerMqMessage save(ResultMessageVO resultMessageVO, int type, String guid, String imageGuid, String resultCode){
        SerMqMessage serMqMessage = SerMqMessage.builder()
                .guid(guid)
                .imageGuid(imageGuid)
                .resultCode(resultCode)
                .type(type)
                .mqKey(resultMessageVO.getKey())
                .mqContent(JSONObject.toJSONString(resultMessageVO))
                .build();
        return serMqMessageRepository.save(serMqMessage);
    }

}
