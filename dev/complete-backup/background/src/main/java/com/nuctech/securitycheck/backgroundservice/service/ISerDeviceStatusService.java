package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDeviceStatus;

/**
 * ISerDeviceStatusService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
public interface ISerDeviceStatusService {

    SerDeviceStatus save(SerDeviceStatus serDeviceStatus);

    SerDeviceStatus find(SerDeviceStatus serDeviceStatus);

}
