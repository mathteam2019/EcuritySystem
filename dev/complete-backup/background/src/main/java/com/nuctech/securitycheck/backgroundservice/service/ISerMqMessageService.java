package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDevLog;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerMqMessage;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;

import java.util.Date;

/**
 * ISerMqMessageService
 *
 * @author Choe
 * @version v1.0
 * @since 2020-01-09
 */
public interface ISerMqMessageService {

    SerMqMessage save(ResultMessageVO resultMessageVO, int type, String guid, String imageGuid, String resultCode);

    void removeAllMessage(Date limitDate);
    
}
