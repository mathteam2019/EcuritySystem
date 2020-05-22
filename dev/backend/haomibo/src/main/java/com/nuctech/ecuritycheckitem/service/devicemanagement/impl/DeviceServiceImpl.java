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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.enums.CustomType;
import com.nuctech.ecuritycheckitem.enums.DefaultType;
import com.nuctech.ecuritycheckitem.enums.GenderType;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;


import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.models.simplifieddb.QHistorySimplifiedForHistoryTaskManagement;
import com.nuctech.ecuritycheckitem.repositories.*;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SysDeviceRepository sysDeviceRepository;

    @Autowired
    SerArchiveRepository serArchiveRepository;

    @Autowired
    SysDeviceConfigRepository sysDeviceConfigRepository;

    @Autowired
    SerScanParamRepository serScanParamRepository;

    @Autowired
    SysManualGroupRepository sysManualGroupRepository;

    @Autowired
    SysJudgeGroupRepository sysJudgeGroupRepository;

    @Autowired
    SysJudgeDeviceRepository sysJudgeDeviceRepository;

    @Autowired
    SysManualDeviceRepository sysManualDeviceRepository;

    @Autowired
    SerScanParamsFromRepository serScanParamsFromRepository;

    @Autowired
    FromConfigIdRepository fromConfigIdRepository;

    @Autowired
    SerScanRepository serScanRepository;

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    SerAssignRepository serAssignRepository;

    @Autowired
    SerDeviceStatusRepository serDeviceStatusRepository;

    @Autowired
    Utils utils;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.CHINESE;

    public static String defaultSort = "deviceSerial";

    public String getJsonFromDevice(SysDevice device) {
        SysDevice newDevice = SysDevice.builder()
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
                .registerId(device.getRegisterId())
                .deviceDesc(device.getDeviceDesc())
                .deviceIp(device.getDeviceIp())
                .devicePassageWay(device.getDevicePassageWay())
                .status(device.getStatus())
                .currentStatus(device.getCurrentStatus())
                .workStatus(device.getWorkStatus())
                .softwareVersion(device.getSoftwareVersion())
                .algorithmVersion(device.getAlgorithmVersion())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newDevice);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return answer;
    }

    /**
     * check if device exists
     * @param deviceId
     * @return
     */
    @Override
    public boolean checkDeviceExist(Long deviceId) {
        return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceId.eq(deviceId));
    }

    /**
     * check if archive id exists
     * @param archiveId
     * @return
     */
    @Override
    public boolean checkArchiveExist(Long archiveId) {
        return serArchiveRepository.exists(QSerArchive.serArchive
                .archiveId.eq(archiveId));
    }



    /**
     * check if device name exists
     * @param deviceName
     * @param deviceId
     * @return
     */
    @Override
    public boolean checkDeviceNameExist(String deviceName, Long deviceId) {
        if(deviceId == null) {
            return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceName.eq(deviceName));
        }
        return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceName.eq(deviceName)
                .and(QSysDevice.sysDevice.deviceId.ne(deviceId)));
    }

    /**
     * check if device serial exists
     * @param deviceSerial
     * @param deviceId
     * @return
     */
    @Override
    public boolean checkDeviceSerialExist(String deviceSerial, Long deviceId) {
        if(deviceId == null) {
            return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceSerial.eq(deviceSerial));
        }
        return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceSerial.eq(deviceSerial)
                .and(QSysDevice.sysDevice.deviceId.ne(deviceId)));
    }

    /**
     * check if device have field or not
     * @param deviceId
     * @return
     */
    public boolean checkDeviceContainField(Long deviceId) {
        Optional<SysDevice> optionalSysDevice = sysDeviceRepository.findOne(QSysDevice.
                sysDevice.deviceId.eq(deviceId));
        SysDevice sysDevice = optionalSysDevice.get();
        if(sysDevice.getField() == null) {
            return false;
        }
        return true;
    }

    /**
     * check if device is security and it's config setting is active or not.
     * @param deviceId
     * @return
     */
    public int checkDeviceConfigActive(Long deviceId) {
        Optional<SysDeviceConfig> optionalSysDeviceConfig = sysDeviceConfigRepository.findOne(QSysDeviceConfig.
                sysDeviceConfig.deviceId.eq(deviceId));
        if(!optionalSysDeviceConfig.isPresent()) {
            return 0;
        }
        SysDeviceConfig sysDeviceConfig = optionalSysDeviceConfig.get();
        if(sysDeviceConfig != null) {
            if(sysDeviceConfig.getStatus().equals(SysDeviceConfig.Status.ACTIVE)) {
                return 1;
            }
        }
//        Optional<SerScanParam> optionalSerScanParam = serScanParamRepository.findOne(QSerScanParam.
//                serScanParam.deviceId.eq(deviceId));
//        SerScanParam serScanParam = optionalSerScanParam.get();
//        if(serScanParam != null) {
//            if(serScanParam.getStatus().equals(SerScanParam.Status.ACTIVE)) {
//                return 2;
//            }
//        }
        return 0;
    }

    /**
     * check if device guid exits
     * @param guid
     * @param deviceId
     * @return
     */
    @Override
    public boolean checkDeviceGuidExist(String guid, Long deviceId) {
        if(deviceId == null) {
            return sysDeviceRepository.exists(QSysDevice.sysDevice.guid.eq(guid));
        }
        return sysDeviceRepository.exists(QSysDevice.sysDevice.guid.eq(guid)
                .and(QSysDevice.sysDevice.deviceId.ne(deviceId)));
    }

    /**
     * check if device status and it used in history
     * @param deviceId
     * @return
     */
    @Override
    public int checkDeviceStatus(Long deviceId) {
        Optional<SysDevice> optionalSysDevice = sysDeviceRepository.findOne(QSysDevice.
                sysDevice.deviceId.eq(deviceId));
        SysDevice sysDevice = optionalSysDevice.get();

        if(serScanRepository.exists(QSerScan.serScan.scanDeviceId.eq(deviceId))) {
            return 1;
        }

        if(serAssignRepository.exists(QSerAssign.serAssign.assignJudgeDeviceId.eq(deviceId))) {
            return 1;
        }
        if(serAssignRepository.exists(QSerAssign.serAssign.assignHandDeviceId.eq(deviceId))) {
            return 1;
        }
        if(historyRepository.exists(QHistorySimplifiedForHistoryTaskManagement.historySimplifiedForHistoryTaskManagement.handDeviceId.eq(deviceId))) {
            return 1;
        }
        if(historyRepository.exists(QHistorySimplifiedForHistoryTaskManagement.historySimplifiedForHistoryTaskManagement.judgeDeviceId.eq(deviceId))) {
            return 1;
        }
        return 2;
    }

    private BooleanBuilder getPredicate(Long archiveId, String deviceName, String status, Long fieldId, Long categoryId) {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (archiveId != null) {
            predicate.and(builder.archive.archiveId.eq(archiveId));
        }
        if (!StringUtils.isEmpty(deviceName)) {
            predicate.and(builder.deviceName.contains(deviceName));
        }
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.status.eq(status));
        }
        if(fieldId != null) {
            predicate.and(builder.fieldId.eq(fieldId));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }

        if (categoryId != null) {
            predicate.and(builder.categoryId.eq(categoryId));
        }
        return predicate;
    }


    /**
     * get filtered device list
     * @param archiveId
     * @param deviceName
     * @param status
     * @param fieldId
     * @param categoryId
     * @param perPage
     * @param currentPage
     * @return
     */
    @Override
    public PageResult<SysDevice> getFilterDeviceList(String sortBy, String order, Long archiveId, String deviceName, String status, Long fieldId, Long categoryId,
                                                     int perPage, int currentPage) {
        BooleanBuilder predicate = getPredicate(archiveId, deviceName, status, fieldId, categoryId);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by(defaultSort).ascending());
        }

        long total = sysDeviceRepository.count(predicate);
        List<SysDevice> data = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate, pageRequest).spliterator(), false)
                .collect(Collectors.toList());
        PageResult<SysDevice> result = new PageResult<>(total, data);
        return result;
    }

    /**
     * get device export list
     * @param deviceList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysDevice> getExportList(List<SysDevice> deviceList, boolean isAll, String idList) {
        List<SysDevice> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < deviceList.size(); i ++) {
                SysDevice device = deviceList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(device.getDeviceId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(device);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < deviceList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(deviceList.get(i));
            }
        }
        return exportList;
    }

    /**
     * get export device data list
     * @param archiveId
     * @param deviceName
     * @param status
     * @param fieldId
     * @param categoryId
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SysDevice> getExportDataList(String sortBy, String order, Long archiveId, String deviceName, String status, Long fieldId, Long categoryId,
                                             boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(archiveId, deviceName, status, fieldId, categoryId);
        String[] splits = idList.split(",");
        List<Long> deviceIdList = new ArrayList<>();
        for(String idStr: splits) {
            deviceIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QSysDevice.sysDevice.deviceId.in(deviceIdList));
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by(defaultSort).ascending();
        }
        List<SysDevice> preList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate, sort).spliterator(), false)
                .collect(Collectors.toList());
        return preList;
    }

    /**
     * update device status
     * @param deviceId
     * @param status
     */
    @Override
    @Transactional
    public void updateStatus(Long deviceId, String status) {
        Optional<SysDevice> optionalSysDevice = sysDeviceRepository.findOne(QSysDevice.
                sysDevice.deviceId.eq(deviceId));
        SysDevice sysDevice = optionalSysDevice.get();
        String valueBefore = getJsonFromDevice(sysDevice);
        // Update status.
        sysDevice.setStatus(status);

        SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig
                .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

        //check device config exist or not
        if(sysDeviceConfig != null) {
            sysDeviceConfig.setStatus(SysDeviceConfig.Status.INACTIVE);
            sysDeviceConfig.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
            sysDeviceConfigRepository.save(sysDeviceConfig);
        }

        SerScanParam scanParam = serScanParamRepository.findOne(QSerScanParam.serScanParam
                .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

        //check scan param exist or not
        if(scanParam != null) {
            //change status of scan param
            scanParam.setStatus(SerScanParam.Status.INACTIVE);
            scanParam.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
            serScanParamRepository.save(scanParam);
        }

        // Add edited info.
        sysDevice.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);
        String valueAfter = getJsonFromDevice(sysDevice);
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Device", null, currentLocale), "", sysDevice.getDeviceId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * create new device
     * @param sysDevice
     * @param portraitFile
     */
    @Override
    @Transactional
    public void createDevice(SysDevice sysDevice, MultipartFile portraitFile) {
        String fileName = utils.saveImageFile(portraitFile);
        sysDevice.setImageUrl(fileName);

        Optional<SerArchive> optionalSerArchive = serArchiveRepository.findOne(QSerArchive.
                serArchive.archiveId.eq(sysDevice.getArchiveId()));
        SerArchive archive = optionalSerArchive.get();
        SysDeviceCategory category = archive.getArchiveTemplate().getDeviceCategory();

        if(category.getCategoryId() == Constants.JUDGE_CATEGORY_ID) {
            sysDevice.setDeviceType(SysDevice.DeviceType.JUDGE);
        } else if(category.getCategoryId() == Constants.SECURITY_CATEGORY_ID) {
            sysDevice.setDeviceType(SysDevice.DeviceType.SECURITY);
        } else if(category.getCategoryId() == Constants.MANUAL_CATEGORY_ID) {
            sysDevice.setDeviceType(SysDevice.DeviceType.MANUAL);
        }
        sysDevice.setCategoryId(category.getCategoryId());


        // Add created info.
        sysDevice.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);

        if(category.getCategoryId() == Constants.JUDGE_CATEGORY_ID) {
            SysJudgeDevice sysJudgeDevice = SysJudgeDevice.builder()
                    .judgeDeviceId(sysDevice.getDeviceId())
                    .deviceStatus(SysDevice.DeviceStatus.UNREGISTER)
                    .build();
            sysJudgeDevice.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
            sysJudgeDeviceRepository.save(sysJudgeDevice);
        } else if(category.getCategoryId() == Constants.MANUAL_CATEGORY_ID) {
            SysManualDevice sysManualDevice = SysManualDevice.builder()
                    .manualDeviceId(sysDevice.getDeviceId())
                    .deviceStatus(SysDevice.DeviceStatus.UNREGISTER)
                    .build();
            sysManualDevice.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
            sysManualDeviceRepository.save(sysManualDevice);
        } else if(category.getCategoryId() == Constants.SECURITY_CATEGORY_ID) {
            SysDeviceConfig deviceConfig = SysDeviceConfig.builder()
                    .deviceId(sysDevice.getDeviceId())
                    .status(SysDeviceConfig.Status.INACTIVE)
                    .modeId(Constants.DEFAULT_MODE_ID)
                    .manualSwitch(CustomType.FALSE.getValue())
                    .manDeviceGender(GenderType.MALE.getValue())
                    .womanDeviceGender(GenderType.FEMALE.getValue())
                    .status(SysDeviceConfig.Status.INACTIVE)
                    .build();
            deviceConfig.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
            sysDeviceConfigRepository.save(deviceConfig);

            SerScanParam scanParam = SerScanParam.builder()
                    .deviceId(sysDevice.getDeviceId())
                    .airCaliWarnTime(Constants.DEFAULT_AIR_CALIWARN_TIME)
                    .standByTime(Constants.DEFAULT_STANDBY_TIME)
                    .alarmSound(DefaultType.TRUE.getValue())
                    .passSound(DefaultType.TRUE.getValue())
                    .posErrorSound(DefaultType.TRUE.getValue())
                    .standSound(DefaultType.FALSE.getValue())
                    .scanSound(DefaultType.FALSE.getValue())
                    .scanOverUseSound(DefaultType.FALSE.getValue())
                    .autoRecognise(DefaultType.TRUE.getValue())
                    .recognitionRate(Constants.DEFAULT_RECOGNIZE_RATE)
                    .saveScanData(DefaultType.TRUE.getValue())
                    .saveSuspectData(DefaultType.FALSE.getValue())
                    .facialBlurring(DefaultType.TRUE.getValue())
                    .chestBlurring(DefaultType.TRUE.getValue())
                    .hipBlurring(DefaultType.TRUE.getValue())
                    .groinBlurring(DefaultType.TRUE.getValue())
                    .deviceStorageAlarm(Constants.DEFAULT_STORAGE_ALARM)
                    .deviceStorageAlarmPercent(Constants.DEFAULT_STORAGE_PERCENT)
                    .status(SerScanParam.Status.INACTIVE)
                    .build();
            scanParam.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
            serScanParamRepository.save(scanParam);
        }

        SerDeviceStatus serDeviceStatus = SerDeviceStatus.builder()
                .deviceId(sysDevice.getDeviceId())
                .build();
        serDeviceStatus.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        serDeviceStatusRepository.save(serDeviceStatus);

        String valueAfter = getJsonFromDevice(sysDevice);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Device", null, currentLocale), "", sysDevice.getDeviceId().toString(), null, true, "", valueAfter);


    }

    /**
     * edit device
     * @param sysDevice
     * @param portraitFile
     */
    @Override
    @Transactional
    public void modifyDevice(SysDevice sysDevice, MultipartFile portraitFile) {
        SysDevice oldSysDevice = sysDeviceRepository.findOne(QSysDevice.sysDevice
                .deviceId.eq(sysDevice.getDeviceId())).orElse(null);
        String valueBefore = getJsonFromDevice(oldSysDevice);
        sysDevice.setCreatedBy(oldSysDevice.getCreatedBy());
        sysDevice.setCreatedTime(oldSysDevice.getCreatedTime());
        sysDevice.setStatus(oldSysDevice.getStatus());
        sysDevice.setCurrentStatus(oldSysDevice.getCurrentStatus());
        sysDevice.setWorkStatus(oldSysDevice.getWorkStatus());
        sysDevice.setCategoryId(oldSysDevice.getCategoryId());

        String fileName = utils.saveImageFile(portraitFile);
        if(!fileName.equals("")) {
            sysDevice.setImageUrl(fileName);
        }

        // Add edited info.
        sysDevice.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);
        String valueAfter = getJsonFromDevice(sysDevice);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Device", null, currentLocale), "", sysDevice.getDeviceId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * remove device
     * @param deviceId
     */
    @Override
    @Transactional
    public boolean removeDevice(Long deviceId) {
        SysDevice sysDevice = sysDeviceRepository.findOne(QSysDevice.sysDevice
                .deviceId.eq(deviceId)).orElse(null);
        if(sysDevice.getStatus().equals(SysDevice.Status.ACTIVE)) {
            return false;
        }
        String valueBefore = getJsonFromDevice(sysDevice);


        if(sysDevice.getDeviceType().equals(SysDevice.DeviceType.JUDGE)) {
            SysJudgeDevice sysJudgeDevice = sysJudgeDeviceRepository.findOne(QSysJudgeDevice.sysJudgeDevice
                    .judgeDeviceId.eq(sysDevice.getDeviceId())).orElse(null);
            if(sysJudgeDevice != null) {
                sysJudgeGroupRepository.deleteAll(sysJudgeGroupRepository.findAll(
                        QSysJudgeGroup.sysJudgeGroup.judgeDeviceId.eq(sysJudgeDevice.getId())));
                sysJudgeDeviceRepository.delete(sysJudgeDevice);
            }
        } else if(sysDevice.getDeviceType().equals(SysDevice.DeviceType.MANUAL)) {
            SysManualDevice sysManualDevice = sysManualDeviceRepository.findOne(QSysManualDevice.sysManualDevice
                    .manualDeviceId.eq(sysDevice.getDeviceId())).orElse(null);
            if(sysManualDevice != null) {
                sysManualGroupRepository.deleteAll(sysManualGroupRepository.findAll(
                        QSysManualGroup.sysManualGroup.manualDeviceId.eq(sysManualDevice.getId())));
                sysManualDeviceRepository.delete(sysManualDevice);
            }
        } else {
            SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig
                    .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

            //check device config exist or not
            if(sysDeviceConfig != null) {
                fromConfigIdRepository.deleteAll(fromConfigIdRepository.findAll(
                        QFromConfigId.fromConfigId1.configId.eq(sysDeviceConfig.getConfigId())));
                sysDeviceConfigRepository.delete(sysDeviceConfig);
            }

            SerScanParam scanParam = serScanParamRepository.findOne(QSerScanParam.serScanParam
                    .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

            //check scan param exist or not
            if(scanParam != null) {
                //remove correspond from config.
                serScanParamsFromRepository.deleteAll(serScanParamsFromRepository.findAll(
                        QSerScanParamsFrom.serScanParamsFrom.scanParamsId.eq(scanParam.getScanParamsId())));
                serScanParamRepository.delete(scanParam);
            }
        }
        sysDeviceRepository.delete(sysDevice);
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Device", null, currentLocale), "", sysDevice.getDeviceId().toString(), null, true, valueBefore, "");
        return true;
    }

    /**
     * remove device field
     * @param deviceList
     */
    @Override
    @Transactional
    public void modifyDeviceField(List<SysDevice> deviceList) {
        for(int i = 0; i < deviceList.size(); i ++) {
            SysDevice device = deviceList.get(i);
            // Add edited info.
            SysDevice realDevice = sysDeviceRepository.findOne(QSysDevice.sysDevice
                    .deviceId.eq(device.getDeviceId())).orElse(null);
            if(realDevice != null) {
                String valueBefore = getJsonFromDevice(realDevice);
                realDevice.setFieldId(device.getFieldId());
                realDevice.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
                sysDeviceRepository.save(realDevice);
                String valueAfter = getJsonFromDevice(realDevice);
                auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                        "", messageSource.getMessage("Device", null, currentLocale), "", realDevice.getDeviceId().toString(), null, true, valueBefore, valueAfter);
            }
        }
    }

    /**
     * find all device
     * @return
     */
    @Override
    public List<SysDevice> findAll() {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.status.eq(SysDevice.Status.ACTIVE));

        return StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * get empty field device
     * @param categoryId
     * @return
     */
    @Override
    public List<SysDevice> getEmptyFieldDevice(Long categoryId) {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());
        predicate.and(builder.fieldId.isNull());
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        if(categoryId != null) {
            predicate.and(builder.categoryId.eq(categoryId));
        }


        List<SysDevice> sysDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        return getInactiveConfigDevice(sysDeviceList);
    }

    private List<SysDevice> getInactiveConfigDevice(List<SysDevice> preSysDeviceList) {
        List<SysDevice> sysDeviceList = new ArrayList<>();
        List<SysDeviceConfig> deviceConfigList = sysDeviceConfigRepository.findAll();
        for(int i = 0; i < preSysDeviceList.size(); i ++) {
            SysDevice sysDevice = preSysDeviceList.get(i);
            if(sysDevice.getCategoryId() == Constants.SECURITY_CATEGORY_ID) {
                for(int j = 0; j < deviceConfigList.size(); j ++) {
                    if(SysDeviceConfig.Status.ACTIVE.equals(deviceConfigList.get(j).getStatus())) {
                        continue;
                    }
                    Long configDeviceId = deviceConfigList.get(j).getDeviceId();
                    Long deviceId = sysDevice.getDeviceId();
                    if(configDeviceId != null && configDeviceId.equals(deviceId)) {
                        sysDeviceList.add(sysDevice);
                    }
                }
            } else {
                sysDeviceList.add(sysDevice);
            }
        }
        return sysDeviceList;
    }

    /**
     * get device by field
     * @param fieldId
     * @param categoryId
     * @return
     */
    @Override
    @Transactional
    public List<SysDevice> getDeviceByField(Long fieldId, Long categoryId) {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if(fieldId!= null) {
            predicate.and(builder.fieldId.eq(fieldId));
        }
        if(categoryId != null) {
            predicate.and(builder.categoryId.eq(categoryId));
        }

        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }


        List<SysDevice> sysDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        return getInactiveConfigDevice(sysDeviceList);
    }

    /**
     * get device by field
     * @return
     */
    @Override
    @Transactional
    public List<SysDevice> findAllSecurity() {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.categoryId.eq(Long.valueOf(Constants.SECURITY_CATEGORY_ID)));
        predicate.and(builder.status.eq(SysDevice.Status.ACTIVE));




        List<SysDevice> sysDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        return sysDeviceList;
    }


}
