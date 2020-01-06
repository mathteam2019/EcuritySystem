package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDeviceConfig;
import com.nuctech.securitycheck.backgroundservice.repositories.SysDeviceConfigRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISysDeviceConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SysDeviceConfigServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SysDeviceConfigServiceImpl implements ISysDeviceConfigService {

    @Autowired
    private SysDeviceConfigRepository sysDeviceConfigRepository;

    /**
     * 运行配置 查询
     *
     * @param sysDeviceConfig
     * @return SysDeviceConfig
     */
    @Override
    public SysDeviceConfig find(SysDeviceConfig sysDeviceConfig) {
        Example<SysDeviceConfig> exConfig = Example.of(sysDeviceConfig);
        return sysDeviceConfigRepository.findOne(exConfig);
    }

    @Override
    public SysDeviceConfig findLastConfig(Long deviceId) {
        return sysDeviceConfigRepository.findLatestConfig(deviceId);
    }

    @Override
    public SysDeviceConfig findById(Long configId) {
        return sysDeviceConfigRepository.findOne(configId);
    }
}
