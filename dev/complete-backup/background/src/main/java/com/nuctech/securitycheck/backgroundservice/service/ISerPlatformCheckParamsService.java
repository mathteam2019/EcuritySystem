package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerPlatformCheckParams;

/**
 * ISerPlatformCheckParamsService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISerPlatformCheckParamsService {

    SerPlatformCheckParams getLastCheckParams();

}
