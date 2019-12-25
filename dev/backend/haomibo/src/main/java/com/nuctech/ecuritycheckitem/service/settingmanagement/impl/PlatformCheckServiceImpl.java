/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PlatformCheckServiceImpl）
 * 文件名：	PlatformCheckServiceImpl.java
 * 描述：	PlatformCheckService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

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

    /**
     * find all platformcheck params
     * @return
     */
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

    /**
     * modify platform param
     * @param serPlatformCheckParams
     * @param isCreate
     */
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
