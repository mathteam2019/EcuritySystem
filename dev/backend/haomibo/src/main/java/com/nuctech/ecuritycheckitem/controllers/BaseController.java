/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（BaseController 1.0）
 * 文件名：	BaseController.java
 * 描述：	The base controller for all controllers. This class defines common fields and methods.
 * 作者名：	Sandy
 * 日期：	2019/10/15
 *
 */

package com.nuctech.ecuritycheckitem.controllers;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysDictionaryData;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.statistics.*;
import com.nuctech.ecuritycheckitem.service.taskmanagement.HistoryService;
import com.nuctech.ecuritycheckitem.service.taskmanagement.ProcessService;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class BaseController {

    public static Locale currentLocale = Locale.CHINESE;

    @Autowired
    public EntityManager entityManager;

    @Autowired
    public SysOrgRepository sysOrgRepository;

    @Autowired
    public ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    public Utils utils;

    @Autowired
    public AuthenticationFacade authenticationFacade;

    @Autowired
    public SysUserRepository sysUserRepository;

    @Autowired
    public SysRoleRepository sysRoleRepository;

    @Autowired
    public SysDataGroupRepository sysDataGroupRepository;

    @Autowired
    public SysDataGroupUserRepository sysDataGroupUserRepository;

    @Autowired
    public SysUserGroupRepository sysUserGroupRepository;

    @Autowired
    public SysResourceRepository sysResourceRepository;

    @Autowired
    public SysUserGroupUserRepository sysUserGroupUserRepository;

    @Autowired
    public SysRoleResourceRepository sysRoleResourceRepository;

    @Autowired
    public SysRoleUserRepository sysRoleUserRepository;

    @Autowired
    public SysUserGroupRoleRepository sysUserGroupRoleRepository;

    @Autowired
    public SysUserLookupRepository sysUserLookupRepository;

    @Autowired
    public SysUserGroupLookupRepository sysUserGroupLookupRepository;

    @Autowired
    public SysFieldRepository sysFieldRepository;

    @Autowired
    public SysDeviceCategoryRepository sysDeviceCategoryRepository;

    @Autowired
    public SerArchiveTemplateRepository serArchiveTemplateRepository;

    @Autowired
    public SerArchiveIndicatorsRepository serArchiveIndicatorsRepository;

    @Autowired
    public SerArchiveRepository serArchiveRepository;

    @Autowired
    public SerArchiveValueRepository serArchiveValueRepository;

    @Autowired
    public SysDeviceRepository sysDeviceRepository;

    @Autowired
    public SysDeviceConfigRepository sysDeviceConfigRepository;

    @Autowired
    public SysWorkModeRepository sysWorkModeRepository;

    @Autowired
    public SysManualDeviceRepository sysManualDeviceRepository;

    @Autowired
    public SysManualGroupRepository sysManualGroupRepository;

    @Autowired
    public SysJudgeDeviceRepository sysJudgeDeviceRepository;

    @Autowired
    public SysJudgeGroupRepository sysJudgeGroupRepository;

    @Autowired
    public FromConfigIdRepository fromConfigIdRepository;

    @Autowired
    public SerDevLogRepository serDevLogRepository;

    @Autowired
    public SysAccessLogRepository sysAccessLogRepository;

    @Autowired
    public SysAuditLogRepository sysAuditLogRepository;

    @Autowired
    public SerDeviceStatusRepository serDeviceStatusRepository;

    @Autowired
    public SerScanParamRepository serScanParamRepository;

    @Autowired
    public SerScanParamsFromRepository serScanParamsFromRepository;

    @Autowired
    public SerPlatformCheckParamRepository serPlatformCheckParamRepository;

    @Autowired
    public SerPlatformOtherParamRepository serPlatformOtherParamRepository;

    @Autowired
    public SerTaskRepository serTaskRespository;

    @Autowired
    public HistoryRepository historyRespository;

    @Autowired
    public SerScanRepository serScanRepository;

    @Autowired
    public SerJudgeGraphRepository serJudgeGraphRepository;

    @Autowired
    public SerHandExaminationRepository serHandExaminationRepository;
    
    @Autowired
    public SerKnowledgeCaseDealRepository serKnowledgeCaseDealRepository;

    @Autowired
    public SerKnowledgeCaseRepository serKnowledgeCaseRepository;

    @Autowired
    public SerDeviceRegisterRepository serDeviceRegisterRepository;

    @Autowired
    public ProcessService taskService;

    @Autowired
    public StatisticsByUserService userStatisticsService;

    @Autowired
    public StatisticsByDeviceService deviceStatisticsService;

    @Autowired
    public PreviewStatisticsService previewStatisticsService;

    @Autowired
    public HistoryService historyService;

    @Autowired
    public EvaluateJudgeStatisticsService evaluateJudgeStatisticsService;

    @Autowired
    public HandExaminationStatisticsService handExaminationStatisticsService;

    @Autowired
    public JudgeStatisticsService judgeStatisticsService;

    @Autowired
    public ScanStatisticsService scanStatisticsService;

    @Autowired
    public SuspictionHandgoodsStatisticsService suspictionHandgoodsStatisticsService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public void setDictionary(String locale) {
        List<SysDictionaryData> dictionaryDataList = authService.findAllDictionary();
        ConstantDictionary.Dictionary[] originalDictionaryList;
        if(Constants.CHINESE_LOCALE.equals(locale)) {
            originalDictionaryList = ConstantDictionary.originalChineseDictionaryList;
        } else {
            originalDictionaryList = ConstantDictionary.originalEnglishDictionaryList;
        }
        ConstantDictionary.Dictionary[] dictionaryList = new ConstantDictionary.Dictionary[dictionaryDataList.size() + originalDictionaryList.length];
        int index = 0;
        for(int i = 0; i < originalDictionaryList.length; i ++) {
            dictionaryList[index ++] = originalDictionaryList[i];
        }
        for(int i = 0; i < dictionaryDataList.size(); i ++) {
            ConstantDictionary.Dictionary dictionary = new ConstantDictionary.Dictionary(dictionaryDataList.get(i).getDataCode(),
                    dictionaryDataList.get(i).getDataValue());
            dictionaryList[index ++] = dictionary;
        }

        ConstantDictionary.setDictionaryList(dictionaryList);
    }

    @Value(Constants.PDF_HEADER_FONT_RESOURCE_PATH)
    public Resource resourceFile;

    public URL getFontResource() {
//        try {
//            return resourceFile.getURL();
//        } catch (Exception ex) {}
//        return null;
        return this.getClass().getClassLoader().getResource(Constants.PDF_HEADER_FONT_RESOURCE_PATH);
    }



}
