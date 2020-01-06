package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDevLog;

/**
 * ISerDevLogService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
public interface ISerDevLogService {

    SerDevLog save(SerDevLog serDevLog);
    
}
