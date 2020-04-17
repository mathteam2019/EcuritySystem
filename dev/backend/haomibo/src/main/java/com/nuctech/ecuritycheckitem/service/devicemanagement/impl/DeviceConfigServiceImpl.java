/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceConfigServiceImpl）
 * 文件名：	DeviceConfigServiceImpl.java
 * 描述：	DeviceConfigService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.models.redis.*;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.*;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceConfigService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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

    @Autowired
    SerDeviceStatusRepository serDeviceStatusRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.CHINESE;

    public static String defaultConfigSort = "device.deviceSerial";

    public String getJsonFromDeviceConfig(SysDeviceConfig deviceConfig) {
        SysDeviceConfig newConfig = SysDeviceConfig.builder()
                .configId(deviceConfig.getConfigId())
                .modeId(deviceConfig.getModeId())
                .deviceId(deviceConfig.getDeviceId())
                .manualSwitch(deviceConfig.getManualSwitch())
                .atrSwitch(deviceConfig.getAtrSwitch())
                .manRemoteGender(deviceConfig.getManRemoteGender())
                .womanRemoteGender(deviceConfig.getWomanRemoteGender())
                .manManualGender(deviceConfig.getManManualGender())
                .womanManualGender(deviceConfig.getWomanManualGender())
                .manDeviceGender(deviceConfig.getManDeviceGender())
                .womanDeviceGender(deviceConfig.getWomanDeviceGender())
                .status(deviceConfig.getStatus())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newConfig);
        } catch(Exception ex) {
        }
        return answer;
    }

    /**
     * find config with id
     * @param configId
     * @return
     */
    @Override
    public SysDeviceConfig findConfigById(Long configId) {
        return sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig
                .configId.eq(configId)).orElse(null);
    }

    /**
     * Check device is offline
     * @param deviceId
     * @return
     */
    @Override
    public boolean checkDeviceOnline(Long deviceId) {
        return serDeviceStatusRepository.exists(QSerDeviceStatus.serDeviceStatus
                .deviceId.eq(deviceId).and(QSerDeviceStatus.serDeviceStatus.deviceOnline.eq(Constants.DEVICE_ONLINE)));
    }

    /**
     * Check device have field or not
     * @param deviceId
     * @return
     */
    public boolean checkDeviceHaveField(Long deviceId) {
        return sysDeviceRepository.exists(QSysDevice.sysDevice
                .deviceId.eq(deviceId).and(QSysDevice.sysDevice.fieldId.isNotNull()));
    }

    /**
     * Change device config status
     * @param configId
     * @return
     */
    @Transactional
    public void updateStatusDeviceConfig(Long configId, String status) {
        Optional<SysDeviceConfig> sysDeviceConfigData = sysDeviceConfigRepository.findOne(
                QSysDeviceConfig.sysDeviceConfig.configId.eq(configId));
        SysDeviceConfig sysDeviceConfig = sysDeviceConfigData.get();
        String valueBefore = getJsonFromDeviceConfig(sysDeviceConfig);
        sysDeviceConfig.setStatus(status);
        sysDeviceConfig.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysDeviceConfigRepository.save(sysDeviceConfig);
        String valueAfter = getJsonFromDeviceConfig(sysDeviceConfig);
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("DeviceConfig", null, currentLocale), "", sysDeviceConfig.getConfigId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * get paginated and filtered device config list with filter parameters
     * @param deviceName
     * @param fieldId
     * @param categoryId
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysDeviceConfig> findConfigByFilter(String sortBy, String order, String deviceName, Long fieldId, Long categoryId, Long mode, int currentPage, int perPage) {
        QSysDeviceConfig builder = QSysDeviceConfig.sysDeviceConfig;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(deviceName)) {
            predicate.and(builder.device.deviceName.contains(deviceName));
        }
        if (fieldId != null) {
            predicate.and(builder.device.field.fieldId.eq(fieldId));
        }
        if (mode != null) {
            predicate.and(builder.modeId.eq(mode));
        }
        categoryId = 3L;
        predicate.and(builder.device.categoryId.eq(categoryId));
        predicate.and(builder.device.status.eq(SysDevice.Status.ACTIVE));
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sortBy = "device.deviceSerial";
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by(defaultConfigSort).ascending());
        }




        long total = sysDeviceConfigRepository.count(predicate);
        List<SysDeviceConfig> data = sysDeviceConfigRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<SysDeviceConfig>(total, data);
    }

    /**
     * modify device config
     * @param sysDeviceConfig
     * @param manualDeviceIdList
     * @param judgeDeviceIdList
     * @param configDeviceIdList
     */
    @Override
    @Transactional
    public void modifyDeviceConfig(SysDeviceConfig sysDeviceConfig, List<Long> manualDeviceIdList, List<Long> judgeDeviceIdList,
                                   List<Long> configDeviceIdList) {
        Optional<SysDeviceConfig> sysDeviceConfigData = sysDeviceConfigRepository.findOne(
                QSysDeviceConfig.sysDeviceConfig.configId.eq(sysDeviceConfig.getConfigId()));
        SysDeviceConfig oldSysDeviceConfig = sysDeviceConfigData.get();
        String valueBefore = getJsonFromDeviceConfig(oldSysDeviceConfig);
        sysDeviceConfig.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        List<SysDeviceConfig> deviceConfigList = new ArrayList<>();
        if (configDeviceIdList != null && configDeviceIdList.size() > 0) {
            deviceConfigList = StreamSupport
                    .stream(sysDeviceConfigRepository.findAll(QSysDeviceConfig.sysDeviceConfig
                            .deviceId.in(configDeviceIdList)).spliterator(), false)
                    .collect(Collectors.toList());

            for (int i = 0; i < configDeviceIdList.size(); i++) {
                boolean isExist = false;
                SysDeviceConfig deviceConfig = null;
                for (int j = 0; j < deviceConfigList.size(); j++) {
                    if (deviceConfigList.get(j).getDeviceId().equals(configDeviceIdList.get(i))) {
                        deviceConfig = deviceConfigList.get(j);
                        deviceConfig.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                        isExist = true;
                        break;
                    }
                }
                if (isExist == false) {
                    deviceConfig = SysDeviceConfig.builder().deviceId(configDeviceIdList.get(i)).build();
                    deviceConfig.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                    deviceConfigList.add(deviceConfig);
                }
                deviceConfig.setModeId(sysDeviceConfig.getModeId());
                deviceConfig.setAtrSwitch(sysDeviceConfig.getAtrSwitch());
                deviceConfig.setManualSwitch(sysDeviceConfig.getManualSwitch());
                deviceConfig.setManRemoteGender(sysDeviceConfig.getManRemoteGender());
                deviceConfig.setWomanRemoteGender(sysDeviceConfig.getWomanRemoteGender());
                deviceConfig.setManManualGender(sysDeviceConfig.getManManualGender());
                deviceConfig.setWomanManualGender(sysDeviceConfig.getWomanManualGender());
                deviceConfig.setManDeviceGender(sysDeviceConfig.getManDeviceGender());
                deviceConfig.setWomanDeviceGender(sysDeviceConfig.getWomanDeviceGender());
            }
            deviceConfigList.add(sysDeviceConfig);
        } else {
            deviceConfigList.add(sysDeviceConfig);
        }
        sysDeviceConfigRepository.saveAll(deviceConfigList);

        List<SysManualGroup> manualGroups = new ArrayList<>();
        List<SysJudgeGroup> judgeGroups = new ArrayList<>();

        for (int i = 0; i < deviceConfigList.size(); i++) {
            if (deviceConfigList.get(i).getManualGroupList() != null) {
                for (int j = 0; j < deviceConfigList.get(i).getManualGroupList().size(); j++) {
                    manualGroups.add(deviceConfigList.get(i).getManualGroupList().get(j));
                }
            }

            if (deviceConfigList.get(i).getJudgeGroupList() != null) {
                for (int j = 0; j < deviceConfigList.get(i).getJudgeGroupList().size(); j++) {
                    judgeGroups.add(deviceConfigList.get(i).getJudgeGroupList().get(j));
                }
            }

        }
        sysManualGroupRepository.deleteAll(manualGroups);
        sysJudgeGroupRepository.deleteAll(judgeGroups);
        fromConfigIdRepository.deleteAll(sysDeviceConfig.getFromConfigIdList());

        manualGroups = new ArrayList<>();
        judgeGroups = new ArrayList<>();
        List<FromConfigId> configList = new ArrayList<>();
        for (int i = 0; i < deviceConfigList.size(); i++) {
            if (manualDeviceIdList != null) {
                for (int j = 0; j < manualDeviceIdList.size(); j++) {
                    SysManualGroup manualGroup = SysManualGroup.builder()
                            .configId(deviceConfigList.get(i).getConfigId())
                            .manualDeviceId(manualDeviceIdList.get(j))
                            .build();
                    manualGroup.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                    manualGroups.add(manualGroup);
                }
            }

            if (judgeDeviceIdList != null) {
                for (int j = 0; j < judgeDeviceIdList.size(); j++) {
                    SysJudgeGroup judgeGroup = SysJudgeGroup.builder()
                            .configId(deviceConfigList.get(i).getConfigId())
                            .judgeDeviceId(judgeDeviceIdList.get(j))
                            .build();
                    judgeGroup.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                    judgeGroups.add(judgeGroup);
                }
            }
            if (deviceConfigList.get(i).getConfigId() != sysDeviceConfig.getConfigId()) {
                FromConfigId configIdVal = FromConfigId.builder()
                        .configId(sysDeviceConfig.getConfigId())
                        .deviceId(deviceConfigList.get(i).getDeviceId())
                        .fromDeviceId(sysDeviceConfig.getDeviceId())
                        .build();
                configIdVal.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                configList.add(configIdVal);
            }
        }
        fromConfigIdRepository.saveAll(configList);
        sysManualGroupRepository.saveAll(manualGroups);
        sysJudgeGroupRepository.saveAll(judgeGroups);

        String valueAfter = getJsonFromDeviceConfig(sysDeviceConfig);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("DeviceConfig", null, currentLocale), "", sysDeviceConfig.getConfigId().toString(), null, true, valueBefore, valueAfter);
    }



    /**
     * remove device config
     * @param sysDeviceConfig
     */
    @Override
    @Transactional
    public void removeDeviceConfig(SysDeviceConfig sysDeviceConfig) {
        SysManualGroup manualGroup = (sysDeviceConfig.getManualGroupList() != null && sysDeviceConfig.getManualGroupList().size() > 0) ?
                sysDeviceConfig.getManualGroupList().get(0) : null;
        if (manualGroup != null) {
            sysManualGroupRepository.delete(manualGroup);
        }
        //remove correspond judge group
        SysJudgeGroup judgeGroup = (sysDeviceConfig.getJudgeGroupList() != null && sysDeviceConfig.getJudgeGroupList().size() > 0) ?
                sysDeviceConfig.getJudgeGroupList().get(0) : null;
        if (judgeGroup != null) {
            sysJudgeGroupRepository.delete(judgeGroup);
        }
        //remove correspond from config.
        FromConfigId fromConfigId = (sysDeviceConfig.getFromConfigIdList() != null && sysDeviceConfig.getFromConfigIdList().size() > 0) ?
                sysDeviceConfig.getFromConfigIdList().get(0) : null;
        if (fromConfigId != null) {
            fromConfigIdRepository.delete(fromConfigId);
        }
        sysDeviceConfigRepository.delete(sysDeviceConfig);
    }

    /**
     * find all device config except specified device id
     * @param deviceId
     * @return
     */
    @Override
    public List<SysDeviceConfig> findAllDeviceConfigExceptId(Long deviceId) {
        QSysDeviceConfig builder = QSysDeviceConfig.sysDeviceConfig;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        Long categoryId = 3L;
        predicate.and(builder.device.categoryId.eq(categoryId));
        predicate.and(builder.device.status.eq(SysDevice.Status.ACTIVE));
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        List<SysDeviceConfig> preSysDeviceConfigList = StreamSupport
                .stream(sysDeviceConfigRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());


        return preSysDeviceConfigList;
    }

    /**
     * find all workmode
     * @return
     */
    @Override
    public List<SysWorkMode> findAllWorkMode() {
        return sysWorkModeRepository.findAll();
    }

    /**
     * find all manual device
     * @return
     */
    @Override
    public List<SysManualDevice> findAllManualDevice() {
        QSysManualDevice builder = QSysManualDevice.sysManualDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.device.status.eq(SysDevice.Status.ACTIVE));

        return StreamSupport
                .stream(sysManualDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * find all judge device
     * @return
     */
    @Override
    public List<SysJudgeDevice> findAllJudgeDevice() {
        QSysJudgeDevice builder = QSysJudgeDevice.sysJudgeDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.device.status.eq(SysDevice.Status.ACTIVE));

        return StreamSupport
                .stream(sysJudgeDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public SerSecurityDeviceDetailModel getSecurityInfoFromDatabase(String guid) {
        SysDevice deviceModel = new SysDevice();
        deviceModel.setGuid(guid);
        // 获取当前设备的工作模式
        SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig.device.guid.eq(guid)).
                get();
        SysDevice device = sysDeviceConfig.getDevice();
        SysJudgeGroup sysJudgeGroup = new SysJudgeGroup();
        sysJudgeGroup.setConfigId(sysDeviceConfig.getConfigId());
        List<SysJudgeGroup> sysJudgeGroups = StreamSupport
                .stream(sysJudgeGroupRepository.findAll(QSysJudgeGroup.sysJudgeGroup.configId.eq(sysDeviceConfig.getConfigId())).spliterator(), false)
                .collect(Collectors.toList());sysDeviceConfig.getJudgeGroupList();
        List<SysManualGroup> sysManualGroups = StreamSupport
                .stream(sysManualGroupRepository.findAll(QSysManualGroup.sysManualGroup.configId.eq(sysDeviceConfig.getConfigId())).spliterator(), false)
                .collect(Collectors.toList());sysDeviceConfig.getJudgeGroupList();

        List<SysJudgeGroupSimple> judgeGroupSimpleList = new ArrayList<>();
        List<SysManualGroupSimple> manualGroupSimpleList = new ArrayList<>();

        for(int i = 0; i < sysJudgeGroups.size(); i ++) {
            SysJudgeGroupSimple groupSimple = SysJudgeGroupSimple.builder()
                    .configId(sysJudgeGroups.get(i).getConfigId())
                    .judgeDeviceId(sysJudgeGroups.get(i).getJudgeDeviceId())
                    .judgeGroupId(sysJudgeGroups.get(i).getJudgeGroupId())
                    .build();
            judgeGroupSimpleList.add(groupSimple);
        }

        for(int i = 0; i < sysManualGroups.size(); i ++) {
            SysManualGroupSimple groupSimple = SysManualGroupSimple.builder()
                    .configId(sysManualGroups.get(i).getConfigId())
                    .manualDeviceId(sysManualGroups.get(i).getManualDeviceId())
                    .manualGroupId(sysManualGroups.get(i).getManualGroupId())
                    .build();
            manualGroupSimpleList.add(groupSimple);
        }

        SerSecurityDeviceDetailModel model = new SerSecurityDeviceDetailModel();
        SysDeviceRedis sysDeviceSimple = SysDeviceRedis.builder()
                .deviceId(device.getDeviceId())
                .guid(device.getGuid())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .deviceSerial(device.getDeviceSerial())
                .originalFactoryNumber(device.getOriginalFactoryNumber())
                .manufacturerDate(device.getManufacturerDate())
                .purchaseDate(device.getPurchaseDate())
                .supplier(device.getSupplier())
                .contacts(device.getContacts())
                .mobile(device.getMobile())
                .registrationNumber(device.getRegistrationNumber())
                .imageUrl(device.getImageUrl())
                .fieldId(device.getFieldId())
                .archiveId(device.getArchiveId())
                .categoryId(device.getCategoryId())
                .deviceIP(device.getDeviceIp())
                .status(device.getStatus())
                .currentStatus(device.getCurrentStatus())
                .workStatus(device.getWorkStatus())
                .softwareVersion(device.getSoftwareVersion())
                .algorithmVersion(device.getAlgorithmVersion())
                .build();

        SysWorkModeSimple sysWorkMode = SysWorkModeSimple.builder()
                .modeId(sysDeviceConfig.getModeId())
                .build();
        SysDeviceConfigSimple deviceConfigSimple = SysDeviceConfigSimple.builder()
                .configId(sysDeviceConfig.getConfigId())
                .atrSwitch(sysDeviceConfig.getAtrSwitch())
                .manualSwitch(sysDeviceConfig.getManualSwitch())
                .manDeviceGender(sysDeviceConfig.getManDeviceGender())
                .manManualGender(sysDeviceConfig.getManManualGender())
                .manRemoteGender(sysDeviceConfig.getManRemoteGender())
                .womanDeviceGender(sysDeviceConfig.getWomanDeviceGender())
                .womanManualGender(sysDeviceConfig.getWomanManualGender())
                .womanRemoteGender(sysDeviceConfig.getWomanRemoteGender())
                .sysDevice(sysDeviceSimple)
                .sysWorkMode(sysWorkMode)
                .build();

        model.setDeviceConfig(deviceConfigSimple);

        model.setJudgeGroups(judgeGroupSimpleList);
        model.setManualGroups(manualGroupSimpleList);
        return model;
    }


}
