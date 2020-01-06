package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerHeartBeat;

/**
 * ISerHeartBeatService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISerHeartBeatService {

    SerHeartBeat find(SerHeartBeat serHeartBeat);

    void save(SerHeartBeat serHeartBeat);
    
}
