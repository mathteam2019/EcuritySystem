package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.devicemanagement.DeviceControlController;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    SerScanParamsFromRepository serScanParamsFromRepository;

    @Autowired
    FromConfigIdRepository fromConfigIdRepository;

    @Autowired
    Utils utils;

    @Override
    public boolean checkDeviceExist(Long deviceId) {
        return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceId.eq(deviceId));
    }

    @Override
    public boolean checkArchiveExist(Long archiveId) {
        return serArchiveRepository.exists(QSerArchive.serArchive
                .archiveId.eq(archiveId));
    }

    @Override
    public boolean checkDeviceNameExist(String deviceName, Long deviceId) {
        if(deviceId == null) {
            return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceName.eq(deviceName));
        }
        return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceName.eq(deviceName)
                .and(QSysDevice.sysDevice.deviceId.ne(deviceId)));
    }

    @Override
    public boolean checkDeviceSerialExist(String deviceSerial, Long deviceId) {
        if(deviceId == null) {
            return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceSerial.eq(deviceSerial));
        }
        return sysDeviceRepository.exists(QSysDevice.sysDevice.deviceSerial.eq(deviceSerial)
                .and(QSysDevice.sysDevice.deviceId.ne(deviceId)));
    }

    @Override
    public boolean checkDeviceGuidExist(String guid, Long deviceId) {
        if(deviceId == null) {
            return sysDeviceRepository.exists(QSysDevice.sysDevice.guid.eq(guid));
        }
        return sysDeviceRepository.exists(QSysDevice.sysDevice.guid.eq(guid)
                .and(QSysDevice.sysDevice.deviceId.ne(deviceId)));
    }

    private PageResult<SysDevice> getFilterDeviceByCategory(List<SysDevice> preDeviceList, Long categoryId, int startIndex, int endIndex) {
        if(endIndex == -1) {
            endIndex = preDeviceList.size();
        }
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
        PageResult<SysDevice> result = new PageResult<>(total, data);
        return result;
    }

    @Override
    public PageResult<SysDevice> getFilterDeviceList(String archiveName, String deviceName, String status, Long fieldId, Long categoryId,
                                               int startIndex, int endIndex) {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(archiveName)) {
            predicate.and(builder.archive.archivesName.contains(archiveName));
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
        /*
        * Todo
        *  Strange Category is null
        *if (filter.getCategoryId() != null) {
            predicate.and(builder.archive.archiveTemplate.category.categoryId.eq(filter.getCategoryId()));
        }
        * */

        List<SysDevice> allData = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        return getFilterDeviceByCategory(allData, categoryId, startIndex, endIndex);
    }

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
                }
            }
        } else {
            exportList = deviceList;
        }
        return exportList;
    }

    @Override
    public List<SysDevice> getExportDataList(String archiveName, String deviceName, String status, Long fieldId, Long categoryId,
                                             boolean isAll, String idList) {
        List<SysDevice> preList = getFilterDeviceList(archiveName, deviceName, status, fieldId, categoryId, 0, -1).getDataList();
        return getExportList(preList, isAll, idList);
    }

    @Override
    @Transactional
    public void updateStatus(Long deviceId, String status) {
        Optional<SysDevice> optionalSysDevice = sysDeviceRepository.findOne(QSysDevice.
                sysDevice.deviceId.eq(deviceId));
        SysDevice sysDevice = optionalSysDevice.get();

        // Update status.
        sysDevice.setStatus(status);

        // Add edited info.
        sysDevice.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);
    }

    @Override
    @Transactional
    public void createDevice(SysDevice sysDevice, MultipartFile portraitFile) {
        String fileName = utils.saveImageFile(portraitFile);
        sysDevice.setImageUrl(fileName);


        // Add created info.
        sysDevice.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);
        SysDeviceConfig deviceConfig = SysDeviceConfig.builder().deviceId(sysDevice.getDeviceId()).build();
        deviceConfig.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysDeviceConfigRepository.save(deviceConfig);

        SerScanParam scanParam = SerScanParam.builder().deviceId(sysDevice.getDeviceId()).build();
        scanParam.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        serScanParamRepository.save(scanParam);
    }

    @Override
    @Transactional
    public void modifyDevice(SysDevice sysDevice, MultipartFile portraitFile) {
        SysDevice oldSysDevice = sysDeviceRepository.findOne(QSysDevice.sysDevice
                .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

        sysDevice.setCreatedBy(oldSysDevice.getCreatedBy());
        sysDevice.setCreatedTime(oldSysDevice.getCreatedTime());
        sysDevice.setStatus(oldSysDevice.getStatus());
        sysDevice.setCurrentStatus(oldSysDevice.getCurrentStatus());
        sysDevice.setWorkStatus(oldSysDevice.getWorkStatus());

        String fileName = utils.saveImageFile(portraitFile);
        if(!fileName.equals("")) {
            sysDevice.setImageUrl(fileName);
        }

        // Add edited info.
        sysDevice.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);
    }

    @Override
    @Transactional
    public void removeDevice(Long deviceId) {
        SysDevice sysDevice = sysDeviceRepository.findOne(QSysDevice.sysDevice
                .deviceId.eq(deviceId)).orElse(null);


        sysDeviceRepository.deleteById(deviceId);

        SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig
                .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

        //check device config exist or not
        if(sysDeviceConfig != null) {

            sysDeviceConfigRepository.deleteById(sysDeviceConfig.getConfigId());
            //remove correspond manual group
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
                fromConfigIdRepository.deleteById(fromConfigId.getFromConfigId());
            }


        }


        SerScanParam scanParam = serScanParamRepository.findOne(QSerScanParam.serScanParam
                .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

        //check scan param exist or not
        if(scanParam != null) {
            serScanParamRepository.deleteById(scanParam.getScanParamsId());
            //remove correspond from config.
            SerScanParamsFrom fromParams = (scanParam.getFromParamsList() != null && scanParam.getFromParamsList().size() > 0)?
                    scanParam.getFromParamsList().get(0): null;

            //check from params exist or not
            if(fromParams != null) {
                serScanParamsFromRepository.deleteById(fromParams.getScanParamsId());
            }

        }



    }

    @Override
    @Transactional
    public void modifyDeviceField(List<SysDevice> deviceList) {
        for(int i = 0; i < deviceList.size(); i ++) {
            SysDevice device = deviceList.get(i);
            // Add edited info.
            SysDevice realDevice = sysDeviceRepository.findOne(QSysDevice.sysDevice
                    .deviceId.eq(device.getDeviceId())).orElse(null);
            if(realDevice != null) {
                realDevice.setFieldId(device.getFieldId());
                realDevice.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                sysDeviceRepository.save(realDevice);
            }
        }
    }

    @Override
    public List<SysDevice> findAll() {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.status.eq(SysDevice.Status.ACTIVE));

        return StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<SysDevice> getEmptyFieldDevice(Long categoryId) {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());
        predicate.and(builder.fieldId.isNull());

        List<SysDevice> preSysDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysDevice> sysDeviceList = getFilterDeviceByCategory(preSysDeviceList, categoryId, 0, preSysDeviceList.size()).getDataList();
        return sysDeviceList;
    }

    @Override
    @Transactional
    public List<SysDevice> getDeviceByField(Long fieldId, Long categoryId) {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());



        if(fieldId!= null) {
            predicate.and(builder.fieldId.eq(fieldId));
        }


        List<SysDevice> preSysDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysDevice> sysDeviceList = getFilterDeviceByCategory(preSysDeviceList, categoryId, 0, preSysDeviceList.size()).getDataList();
        return sysDeviceList;
    }
}
