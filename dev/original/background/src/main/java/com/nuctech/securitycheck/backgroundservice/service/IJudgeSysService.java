package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.models.SysDeviceStatusModel;

/**
 * IJudgeSysService
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-11-29
 */
public interface IJudgeSysService {

    boolean updateSysDeviceStatus(SysDeviceStatusModel sysDeviceStatusModel, String deviceStatus);
    
}
