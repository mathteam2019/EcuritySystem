/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PlatformOtherServiceImpl）
 * 文件名：	PlatformOtherServiceImpl.java
 * 描述：	PlatformOtherService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.settingmanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformOtherParams;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformOtherParamRepository;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformOtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class PlatformOtherServiceImpl implements PlatformOtherService {
    @Autowired
    SerPlatformOtherParamRepository serPlatformOtherParamRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.ENGLISH;

    public String getJsonFromPlatform(SerPlatformOtherParams params) {

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
     * find all platform other params
     * @return
     */
    @Override
    public List<SerPlatformOtherParams> findAll() {
        return serPlatformOtherParamRepository.findAll();
    }

    /**
     * edit platform other param
     * @param serPlatformOtherParams
     */
    @Override
    public void modifyPlatform(SerPlatformOtherParams serPlatformOtherParams) {
        String valueBefore = "";
        List<SerPlatformOtherParams> paramsList = findAll();
        if(paramsList.size() > 0) {
            valueBefore = getJsonFromPlatform(paramsList.get(0));
        }
        serPlatformOtherParamRepository.save(serPlatformOtherParams);
        String valueAfter = getJsonFromPlatform(serPlatformOtherParams);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("PlatformOther", null, currentLocale), "", "", null, true, valueBefore, valueAfter);
    }
}
