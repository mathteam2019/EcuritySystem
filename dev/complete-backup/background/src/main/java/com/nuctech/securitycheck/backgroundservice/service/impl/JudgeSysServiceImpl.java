package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDevice;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysUser;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceStatusType;
import com.nuctech.securitycheck.backgroundservice.common.models.SysDeviceStatusModel;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.repositories.SysDeviceRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SysUserRepository;
import com.nuctech.securitycheck.backgroundservice.service.IJudgeSysService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class JudgeSysServiceImpl implements IJudgeSysService {

    @Autowired
    private SysDeviceRepository sysDeviceRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 更新设备工作状态
     *
     * @param sysDeviceStatusModel
     * @param deviceStatus
     * @return boolean
     */
    @Override
    public boolean updateSysDeviceStatus(SysDeviceStatusModel sysDeviceStatusModel, String deviceStatus) {

        // 从数据库获取用户信息
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(sysDeviceStatusModel.getLoginName());
        Example<SysUser> ex = Example.of(sysUser);
        sysUser = sysUserRepository.findOne(ex);
        if (sysUser == null) {
            return false;
        }

        // 从数据库获取设备(get device data from database)
        SysDevice sysDevice = new SysDevice();
        sysDevice.setGuid(sysDeviceStatusModel.getGuid());
        Example<SysDevice> devEx = Example.of(sysDevice);
        sysDevice = sysDeviceRepository.findOne(devEx);
        if (sysDevice == null) {
            return false;
        }


        // 状态必须为停止或登录才能开始
        if(DeviceStatusType.START.getValue().equals(deviceStatus)) {
            if(!DeviceStatusType.LOGIN.getValue().equals(sysDevice.getCurrentStatus()) && !DeviceStatusType.STOP.getValue().equals(sysDevice.getCurrentStatus())) {
                return false;
            }
        } else if(DeviceStatusType.STOP.getValue().equals(deviceStatus)) { //必须先启动停止状态
            if(!DeviceStatusType.START.getValue().equals(sysDevice.getCurrentStatus()) && !DeviceStatusType.LOGIN.getValue().equals(sysDevice.getCurrentStatus())) {
                return false;
            }
        }

        /**
         * todo check current user is login user
         */
        sysDevice.setCurrentStatus(deviceStatus);
        sysDeviceRepository.save(sysDevice);
        redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.device.current.status") + sysDevice.getGuid());

        return true;
    }

}
