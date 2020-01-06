package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDeviceRegister;

/**
 * ISerDeviceRegisterService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
public interface ISerDeviceRegisterService {

    SerDeviceRegister find(SerDeviceRegister serDeviceRegister);

    void save(SerDeviceRegister serDeviceRegister);

}
