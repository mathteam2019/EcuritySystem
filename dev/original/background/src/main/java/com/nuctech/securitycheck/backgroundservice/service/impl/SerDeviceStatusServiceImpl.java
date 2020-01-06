package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDeviceStatus;
import com.nuctech.securitycheck.backgroundservice.repositories.SerDeviceStatusRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISerDeviceStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SerDeviceStatusServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SerDeviceStatusServiceImpl implements ISerDeviceStatusService {

    @Autowired
    private SerDeviceStatusRepository repository;

    /**
     * 设备信息 保存
     *
     * @param serDeviceStatus
     * @return SerDeviceStatus
     */
    @Override
    public SerDeviceStatus save(SerDeviceStatus serDeviceStatus) {
        return repository.save(serDeviceStatus);
    }

    /**
     * 设备信息 查询
     *
     * @param serDeviceStatus
     * @return SerDeviceStatus
     */
    @Override
    public SerDeviceStatus find(SerDeviceStatus serDeviceStatus) {
        Example<SerDeviceStatus> example = Example.of(serDeviceStatus);
        return repository.findOne(example);
    }
    
}
