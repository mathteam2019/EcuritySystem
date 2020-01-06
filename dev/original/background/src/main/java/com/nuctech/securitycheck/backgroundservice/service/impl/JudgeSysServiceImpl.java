package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDevice;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysUser;
import com.nuctech.securitycheck.backgroundservice.common.models.SysDeviceStatusModel;
import com.nuctech.securitycheck.backgroundservice.repositories.SysDeviceRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SysUserRepository;
import com.nuctech.securitycheck.backgroundservice.service.IJudgeSysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JudgeSysServiceImpl
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class JudgeSysServiceImpl implements IJudgeSysService {

    @Autowired
    private SysDeviceRepository sysDeviceRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    /**
     * 更新设备工作状态
     *
     * @param sysDeviceStatusModel
     * @param deviceStatus
     * @return boolean
     */
    @Override
    public boolean updateSysDeviceStatus(SysDeviceStatusModel sysDeviceStatusModel, String deviceStatus) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(sysDeviceStatusModel.getLoginName());
        Example<SysUser> ex = Example.of(sysUser);
        sysUser = sysUserRepository.findOne(ex);
        if (sysUser == null) {
            return false;
        }

        SysDevice sysDevice = new SysDevice();
        sysDevice.setGuid(sysDeviceStatusModel.getGuid());
        Example<SysDevice> devEx = Example.of(sysDevice);
        sysDevice = sysDeviceRepository.findOne(devEx);
        if (sysDevice == null) {
            return false;
        }

        sysDevice.setCurrentStatus(deviceStatus);
        sysDeviceRepository.save(sysDevice);

        return true;
    }

}
