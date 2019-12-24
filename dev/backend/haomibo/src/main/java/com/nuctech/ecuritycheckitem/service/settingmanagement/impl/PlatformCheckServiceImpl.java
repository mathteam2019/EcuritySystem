package com.nuctech.ecuritycheckitem.service.settingmanagement.impl;

import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformCheckParamRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformCheckServiceImpl implements PlatformCheckService {
    @Autowired
    SerPlatformCheckParamRepository serPlatformCheckParamRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Override
    public List<SerPlatformCheckParams> findAll() {
        List<SerPlatformCheckParams> result = serPlatformCheckParamRepository.findAll();
        for(int i = 0; i < result.size(); i ++) {
            SerPlatformCheckParams params = result.get(i);
            if(params.getHistoryDataExport() != null) {
                params.setHistoryDataExportList(params.getHistoryDataExport().split(","));
            }
            if(params.getHistoryDataStorage() != null) {
                params.setHistoryDataStorageList(params.getHistoryDataStorage().split(","));
            }

        }
        return result;
    }

    @Override
    public void modifyPlatform(SerPlatformCheckParams serPlatformCheckParams, boolean isCreate) {
        if(isCreate == true) {
            serPlatformCheckParams.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        } else {
            serPlatformCheckParams.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        }
        serPlatformCheckParamRepository.save(serPlatformCheckParams);
    }
}
