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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.db.SysDictionary;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformCheckParamRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class PlatformCheckServiceImpl implements PlatformCheckService {
    @Autowired
    SerPlatformCheckParamRepository serPlatformCheckParamRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.CHINESE;

    public String getJsonFromPlatform(SerPlatformCheckParams params) {

        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(params);
        } catch(Exception ex) {
        }
        return answer;
    }

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
        String valueBefore = "";
        if(isCreate == true) {
            serPlatformCheckParams.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        } else {
            valueBefore = getJsonFromPlatform(findAll().get(0));
            serPlatformCheckParams.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        }
        serPlatformCheckParamRepository.save(serPlatformCheckParams);
        String valueAfter = getJsonFromPlatform(serPlatformCheckParams);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("PlatformCheck", null, currentLocale), "", "", null, true, valueBefore, valueAfter);
    }
}
