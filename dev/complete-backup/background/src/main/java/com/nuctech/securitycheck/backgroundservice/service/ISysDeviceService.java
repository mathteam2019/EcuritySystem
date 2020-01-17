package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDevice;
import com.nuctech.securitycheck.backgroundservice.common.models.SysLoginModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SysLogoutModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SysRegisterModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SysUnregisterModel;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;

import java.util.List;

/**
 * ISysDeviceService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISysDeviceService {

    SysDevice save(SysDevice sysDevice);

    SysDevice find(SysDevice sysDevice);

    List<SysDevice> findAll();

    DispatchManualDeviceInfoVO dispatchManual(String guid, TaskInfoVO taskInfoVO) throws Exception;

    DispatchManualDeviceInfoVO isManualDeviceDispatched(String taskNumber);

    boolean checkSecurityHandDevice(String guid);

    boolean register(SysDevice sysDevice, SysRegisterModel sysRegisterModel);

    boolean unRegister(SysDevice sysDevice, SysUnregisterModel sysUnregisterModel);

    boolean login(SysDevice sysDevice, SysLoginModel sysLoginModel);

    boolean logout(SysDevice sysDevice, SysLogoutModel sysLogoutModel);

    List<SysSecurityInfoVO> findSecurityInfoList();

    List<SysMonitoringDeviceStatusInfoVO> findMonitoringInfoList();

    boolean checkGuid(String guid);

    int checkDeviceLogin(SysDevice sysDevice);
}
