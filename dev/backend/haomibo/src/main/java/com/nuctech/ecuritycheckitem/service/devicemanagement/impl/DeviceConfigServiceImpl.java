package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;

import com.nuctech.ecuritycheckitem.controllers.devicemanagement.DeviceConfigManagementController;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceConfigService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DeviceConfigServiceImpl implements DeviceConfigService {

    @Autowired
    SysDeviceConfigRepository sysDeviceConfigRepository;

    @Autowired
    SysDeviceRepository sysDeviceRepository;

    @Autowired
    SysWorkModeRepository sysWorkModeRepository;

    @Autowired
    SysManualDeviceRepository sysManualDeviceRepository;

    @Autowired
    SysJudgeDeviceRepository sysJudgeDeviceRepository;

    @Autowired
    SysManualGroupRepository sysManualGroupRepository;

    @Autowired
    SysJudgeGroupRepository sysJudgeGroupRepository;

    @Autowired
    FromConfigIdRepository fromConfigIdRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Override
    public SysDeviceConfig findConfigById(Long configId) {
        return sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig
                .configId.eq(configId)).orElse(null);
    }

    @Override
    public PageResult<SysDeviceConfig> findConfigByFilter(String deviceName, Long fieldId, Long categoryId, int currentPage, int perPage) {
        QSysDeviceConfig builder = QSysDeviceConfig.sysDeviceConfig;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(deviceName)) {
            predicate.and(builder.device.deviceName.contains(deviceName));
        }
        if (fieldId != null) {
            predicate.and(builder.device.field.fieldId.eq(fieldId));
        }
        /*
        * Todo
        *  strange category
        *
        * if (filter.getCategoryId() != null) {
            predicate.and(builder.device.archive.archiveTemplate.category.categoryId.eq(filter.getCategoryId()));
        }
        * */

        int startIndex = perPage * currentPage;
        int endIndex = perPage * (currentPage + 1);

        long total = 0;
        List<SysDeviceConfig> allData = StreamSupport
                .stream(sysDeviceConfigRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        List<SysDeviceConfig> data = new ArrayList<>();

        for(int i = 0; i < allData.size(); i ++) {
            SysDeviceConfig config = allData.get(i);
            if(config.getFromConfigIdList() != null && config.getFromConfigIdList().size() > 0) {
                Long fromDeviceId = config.getFromConfigIdList().get(0).getFromDeviceId();
                SysDevice device = sysDeviceRepository.findOne(QSysDevice.sysDevice
                        .deviceId.eq(fromDeviceId)).orElse(null);
                if(device != null) {
                    config.setFromConfigDeviceName(device.getDeviceName());
                }
            }
        }

        if(categoryId != null) {
            for(int i = 0; i < allData.size(); i ++) {
                SysDeviceConfig deviceConfigData = allData.get(i);
                try {
                    if(deviceConfigData.getDevice().getArchive().getArchiveTemplate().getDeviceCategory().getCategoryId() == categoryId) {
                        if(total >= startIndex && total < endIndex) {
                            data.add(deviceConfigData);
                        }
                        total ++;
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            for(int i = 0; i < allData.size(); i ++) {
                SysDeviceConfig deviceConfigData = allData.get(i);
                if(i >= startIndex && i < endIndex) {
                    data.add(deviceConfigData);
                }
            }
            total = allData.size();
        }
        return new PageResult<SysDeviceConfig>(total, data);
    }

    @Override
    @Transactional
    public void modifyDeviceConfig(SysDeviceConfig sysDeviceConfig, Long manualDeviceId, Long judgeDeviceId, Long configDeviceId) {

        SysManualGroup manualGroup = (sysDeviceConfig.getManualGroupList() != null &&  sysDeviceConfig.getManualGroupList().size() > 0)?
                sysDeviceConfig.getManualGroupList().get(0): null;
        //check manual Group exist or not
        if(manualGroup != null) {
            if(manualDeviceId != null) {
                manualGroup.setManualDeviceId(manualDeviceId);
                manualGroup.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                sysManualGroupRepository.save(manualGroup);
            } else {
                sysManualGroupRepository.delete(manualGroup);
            }

        } else if(manualDeviceId != null) {//create manual group
            manualGroup = SysManualGroup.
                    builder()
                    .manualDeviceId(manualDeviceId)
                    .configId(sysDeviceConfig.getConfigId())
                    .build();
            manualGroup.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysManualGroupRepository.save(manualGroup);
        }

        SysJudgeGroup judgeGroup = (sysDeviceConfig.getJudgeGroupList() != null &&  sysDeviceConfig.getJudgeGroupList().size() > 0)?
                sysDeviceConfig.getJudgeGroupList().get(0): null;
        //check judge Group exist or not
        if(judgeGroup != null) {
            if(judgeDeviceId != null) {
                judgeGroup.setJudgeDeviceId(judgeDeviceId);
                judgeGroup.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                sysJudgeGroupRepository.save(judgeGroup);
            } else {
                sysJudgeGroupRepository.delete(judgeGroup);
            }

        } else if(judgeDeviceId != null) {//create judge group
            judgeGroup = SysJudgeGroup.
                    builder()
                    .judgeDeviceId(judgeDeviceId)
                    .configId(sysDeviceConfig.getConfigId())
                    .build();
            judgeGroup.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysJudgeGroupRepository.save(judgeGroup);
        }

        FromConfigId fromConfigId = (sysDeviceConfig.getFromConfigIdList() != null &&  sysDeviceConfig.getFromConfigIdList().size() > 0)?
                sysDeviceConfig.getFromConfigIdList().get(0): null;
        //check from config exist or not
        if(fromConfigId != null) {
            if(configDeviceId != null) {
                fromConfigId.setFromDeviceId(configDeviceId);
                fromConfigId.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                fromConfigIdRepository.save(fromConfigId);
            } else {
                fromConfigIdRepository.delete(fromConfigId);
            }

        } else if(configDeviceId != null) {//create judge group
            fromConfigId = FromConfigId.
                    builder()
                    .fromDeviceId(configDeviceId)
                    .deviceId(sysDeviceConfig.getDeviceId())
                    .configId(sysDeviceConfig.getConfigId())
                    .build();
            fromConfigId.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            fromConfigIdRepository.save(fromConfigId);
        }

        // Add edited info.
        sysDeviceConfig.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceConfigRepository.save(sysDeviceConfig);
    }

    @Override
    @Transactional
    public void removeDeviceConfig(SysDeviceConfig sysDeviceConfig) {
        SysManualGroup manualGroup = (sysDeviceConfig.getManualGroupList() != null &&  sysDeviceConfig.getManualGroupList().size() > 0)?
                sysDeviceConfig.getManualGroupList().get(0): null;
        if(manualGroup != null) {
            sysManualGroupRepository.delete(manualGroup);
        }
        //remove correspond judge group
        SysJudgeGroup judgeGroup = (sysDeviceConfig.getJudgeGroupList() != null &&  sysDeviceConfig.getJudgeGroupList().size() > 0)?
                sysDeviceConfig.getJudgeGroupList().get(0): null;
        if(judgeGroup != null) {
            sysJudgeGroupRepository.delete(judgeGroup);
        }
        //remove correspond from config.
        FromConfigId fromConfigId = (sysDeviceConfig.getFromConfigIdList() != null &&  sysDeviceConfig.getFromConfigIdList().size() > 0)?
                sysDeviceConfig.getFromConfigIdList().get(0): null;
        if(fromConfigId != null) {
            fromConfigIdRepository.delete(fromConfigId);
        }
        sysDeviceConfigRepository.delete(sysDeviceConfig);
    }

    @Override
    public List<SysDeviceConfig> findAllDeviceConfigExceptId(Long deviceId) {
        List<SysDeviceConfig> preSysDeviceConfigList = sysDeviceConfigRepository.findAll();
        List<SysDeviceConfig> sysDeviceConfigList = new ArrayList<>();

        for(int i = 0; i < preSysDeviceConfigList.size(); i ++) {
            if(preSysDeviceConfigList.get(i).getDeviceId() != deviceId) {
                sysDeviceConfigList.add(preSysDeviceConfigList.get(i));
            }
        }
        return sysDeviceConfigList;
    }

    @Override
    public List<SysWorkMode> findAllWorkMode() {
        return sysWorkModeRepository.findAll();
    }

    @Override
    public List<SysManualDevice> findAllManualDevice() {
        return sysManualDeviceRepository.findAll();
    }

    @Override
    public List<SysJudgeDevice> findAllJudgeDevice() {
        return sysJudgeDeviceRepository.findAll();
    }
}
