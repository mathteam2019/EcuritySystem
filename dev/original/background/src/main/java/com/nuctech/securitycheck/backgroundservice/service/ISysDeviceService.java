package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDevice;
import com.nuctech.securitycheck.backgroundservice.common.models.SysLoginModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SysLogoutModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SysRegisterModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SysUnregisterModel;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.SysMonitoringDeviceStatusInfoVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.SysSecurityInfoVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.TaskInfoVO;

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

    ResultMessageVO dispatchManual(String guid, TaskInfoVO taskInfoVO) throws Exception;

    boolean register(SysDevice sysDevice, SysRegisterModel sysRegisterModel);

    boolean unRegister(SysDevice sysDevice, SysUnregisterModel sysUnregisterModel);

    boolean login(SysDevice sysDevice, SysLoginModel sysLoginModel);

    boolean logout(SysDevice sysDevice, SysLogoutModel sysLogoutModel);

    List<SysSecurityInfoVO> findSecurityInfoList();

    List<SysMonitoringDeviceStatusInfoVO> findMonitoringInfoList();

    boolean checkGuid(String guid);
}
