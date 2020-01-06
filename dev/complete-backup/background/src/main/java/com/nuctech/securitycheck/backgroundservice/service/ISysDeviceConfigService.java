package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDeviceConfig;

/**
 * ISysDeviceConfigService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISysDeviceConfigService {

    SysDeviceConfig find(SysDeviceConfig sysDeviceConfig);

    SysDeviceConfig findLastConfig(Long deviceId);

    SysDeviceConfig findById(Long configId);

}
