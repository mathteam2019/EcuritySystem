package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDeviceRegister;
import com.nuctech.securitycheck.backgroundservice.repositories.SerDeviceRegisterRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISerDeviceRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SerDeviceRegisterServiceImpl
 *
 * @author benja
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SerDeviceRegisterServiceImpl implements ISerDeviceRegisterService {

    @Autowired
    private SerDeviceRegisterRepository serDeviceRegisterRepository;

    /**
     * 设备注册信息 查询
     *
     * @param serDeviceRegister
     * @return SerDeviceRegister
     */
    @Override
    public SerDeviceRegister find(SerDeviceRegister serDeviceRegister) {
        Example<SerDeviceRegister> example = Example.of(serDeviceRegister);
        return serDeviceRegisterRepository.findOne(example);
    }

    /**
     * 设备注册信息 保存
     *
     * @param serDeviceRegister
     */
    @Override
    public void save(SerDeviceRegister serDeviceRegister) {
        serDeviceRegisterRepository.save(serDeviceRegister);
    }

}
