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

import com.nuctech.ecuritycheckitem.models.db.SysDeviceConfig;
import com.nuctech.ecuritycheckitem.models.db.QSysDeviceConfig;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.models.db.QSysDevice;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SerScanParam;
import com.nuctech.ecuritycheckitem.models.db.SerScanParamsFrom;
import com.nuctech.ecuritycheckitem.models.db.QSerScanParam;
import com.nuctech.ecuritycheckitem.models.db.QSerArchive;


import com.nuctech.ecuritycheckitem.repositories.SysDeviceConfigRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceRepository;
import com.nuctech.ecuritycheckitem.repositories.SysManualGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysJudgeGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.FromConfigIdRepository;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveRepository;
import com.nuctech.ecuritycheckitem.repositories.SerScanParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SerScanParamsFromRepository;

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

import java.util.ArrayList;
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
     * get paginated and filtered device
     * @param preDeviceList
     * @param categoryId
     * @param startIndex
     * @param endIndex
     * @return
     */
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

    /**
     * get filtered device list
     * @param archiveName
     * @param deviceName
     * @param status
     * @param fieldId
     * @param categoryId
     * @param startIndex
     * @param endIndex
     * @return
     */
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
                }
            }
        } else {
            exportList = deviceList;
        }
        return exportList;
    }

    /**
     * get export device data list
     * @param archiveName
     * @param deviceName
     * @param status
     * @param fieldId
     * @param categoryId
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SysDevice> getExportDataList(String archiveName, String deviceName, String status, Long fieldId, Long categoryId,
                                             boolean isAll, String idList) {
        List<SysDevice> preList = getFilterDeviceList(archiveName, deviceName, status, fieldId, categoryId, 0, -1).getDataList();
        return getExportList(preList, isAll, idList);
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

        // Update status.
        sysDevice.setStatus(status);

        // Add edited info.
        sysDevice.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);
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

    /**
     * remove device
     * @param deviceId
     */
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
                realDevice.setFieldId(device.getFieldId());
                realDevice.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                sysDeviceRepository.save(realDevice);
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

        List<SysDevice> preSysDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysDevice> sysDeviceList = getFilterDeviceByCategory(preSysDeviceList, categoryId, 0, preSysDeviceList.size()).getDataList();
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


        List<SysDevice> preSysDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysDevice> sysDeviceList = getFilterDeviceByCategory(preSysDeviceList, categoryId, 0, preSysDeviceList.size()).getDataList();
        return sysDeviceList;
    }
}
