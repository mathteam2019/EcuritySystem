package com.nuctech.ecuritycheckitem.service.settingmanagement.impl;

import com.nuctech.ecuritycheckitem.models.db.SerPlatformOtherParams;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformOtherParamRepository;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformOtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformOtherServiceImpl implements PlatformOtherService {
    @Autowired
    SerPlatformOtherParamRepository serPlatformOtherParamRepository;


    @Override
    public List<SerPlatformOtherParams> findAll() {
        return serPlatformOtherParamRepository.findAll();
    }

    @Override
    public void modifyPlatform(SerPlatformOtherParams serPlatformOtherParams) {
        serPlatformOtherParamRepository.save(serPlatformOtherParams);
    }
}
