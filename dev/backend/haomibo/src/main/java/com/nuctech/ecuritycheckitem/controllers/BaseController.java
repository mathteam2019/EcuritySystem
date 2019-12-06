/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/14
 * @CreatedBy Sandy.
 * @FileName BaseController.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.controllers;

import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * The base controller for all controllers. This class defines common fields and methods.
 */
public class BaseController {

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

    @Value("classpath:font/NotoSansCJKsc-Regular.otf")
    public Resource res;

    @Getter
    @Setter
    public static class FilterDataByCategory<T> {
        List<T> data;
        long total;
    }

    public FilterDataByCategory<SysDevice> getFilterDeviceByCategory(List<SysDevice> preDeviceList, Long categoryId, int startIndex, int endIndex) {
        long total = 0;
        List<SysDevice> data = new ArrayList<>();
        if(categoryId != null) {

            for(int i = 0; i < preDeviceList.size(); i ++) {
                SysDevice deviceData = preDeviceList.get(i);
                try {
                    if(deviceData.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryId() == categoryId) {
                        if(total >= startIndex && total < endIndex) {
                            data.add(deviceData);
                        }
                        total ++;

                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            for(int i = 0; i < preDeviceList.size(); i ++) {
                SysDevice deviceData = preDeviceList.get(i);
                if(i >= startIndex && i < endIndex) {
                    data.add(deviceData);
                }
            }
            total = preDeviceList.size();
        }
        FilterDataByCategory<SysDevice> result = new FilterDataByCategory<>();
        result.setTotal(total);
        result.setData(data);
        return result;
    }


}
