package com.nuctech.ecuritycheckitem.service.settingmanagement.impl;

import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformCheckParamRepository;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformCheckServiceImpl implements PlatformCheckService {
    @Autowired
    SerPlatformCheckParamRepository serPlatformCheckParamRepository;

    @Override
    public List<SerPlatformCheckParams> findAll() {
        return serPlatformCheckParamRepository.findAll();
    }

    @Override
    public void modifyPlatform(SerPlatformCheckParams serPlatformCheckParams) {
        serPlatformCheckParamRepository.save(serPlatformCheckParams);
    }
}
