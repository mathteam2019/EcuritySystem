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

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.*;


import com.nuctech.ecuritycheckitem.repositories.*;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        Optional<SerScanParam> optionalSerScanParam = serScanParamRepository.findOne(QSerScanParam.
                serScanParam.deviceId.eq(deviceId));
        SerScanParam serScanParam = optionalSerScanParam.get();
        if(serScanParam != null) {
            if(serScanParam.getStatus().equals(SerScanParam.Status.ACTIVE)) {
                return 2;
            }
        }
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
        if(sysDevice.getStatus().equals(SysDevice.Status.INACTIVE)) {
            return 0;
        }
        if(serScanRepository.exists(QSerScan.serScan.scanDeviceId.eq(deviceId))) {
            return 1;
        }

        if(serAssignRepository.exists(QSerAssign.serAssign.assignJudgeDeviceId.eq(deviceId))) {
            return 1;
        }
        if(serAssignRepository.exists(QSerAssign.serAssign.assignHandDeviceId.eq(deviceId))) {
            return 1;
        }
        if(historyRepository.exists(QHistory.history.handDeviceId.eq(deviceId))) {
            return 1;
        }
        if(historyRepository.exists(QHistory.history.judgeDeviceId.eq(deviceId))) {
            return 1;
        }
        return 2;
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
    public PageResult<SysDevice> getFilterDeviceList(String sortBy, String order, String archiveName, String deviceName, String status, Long fieldId, Long categoryId,
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

        Sort sort;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = new Sort(Sort.Direction.ASC, sortBy);
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = new Sort(Sort.Direction.DESC, sortBy);
            }
            List<SysDevice> allData = StreamSupport
                    .stream(sysDeviceRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
            return getFilterDeviceByCategory(allData, categoryId, startIndex, endIndex);
        }
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
    public List<SysDevice> getExportDataList(String sortBy, String order, String archiveName, String deviceName, String status, Long fieldId, Long categoryId,
                                             boolean isAll, String idList) {
        List<SysDevice> preList = getFilterDeviceList(sortBy, order, archiveName, deviceName, status, fieldId, categoryId, 0, -1).getDataList();
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

        SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig
                .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

        //check device config exist or not
        if(sysDeviceConfig != null) {
            sysDeviceConfig.setStatus(SysDeviceConfig.Status.INACTIVE);
            sysDeviceConfig.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysDeviceConfigRepository.save(sysDeviceConfig);
        }

        SerScanParam scanParam = serScanParamRepository.findOne(QSerScanParam.serScanParam
                .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

        //check scan param exist or not
        if(scanParam != null) {
            //change status of scan param
            scanParam.setStatus(SerScanParam.Status.INACTIVE);
            scanParam.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            serScanParamRepository.save(scanParam);
        }

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


        // Add created info.
        sysDevice.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);

        if(category.getCategoryId() == Constants.JUDGE_CATEGORY_ID) {
            SysJudgeDevice sysJudgeDevice = SysJudgeDevice.builder()
                    .judgeDeviceId(sysDevice.getDeviceId())
                    .deviceStatus(SysDevice.DeviceStatus.UNREGISTER)
                    .build();
            sysJudgeDevice.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysJudgeDeviceRepository.save(sysJudgeDevice);
        } else if(category.getCategoryId() == Constants.MANUAL_CATEGORY_ID) {
            SysManualDevice sysManualDevice = SysManualDevice.builder()
                    .manualDeviceId(sysDevice.getDeviceId())
                    .deviceStatus(SysDevice.DeviceStatus.UNREGISTER)
                    .build();
            sysManualDevice.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysManualDeviceRepository.save(sysManualDevice);
        } else if(category.getCategoryId() == Constants.SECURITY_CATEGORY_ID) {
            SysDeviceConfig deviceConfig = SysDeviceConfig.builder()
                    .deviceId(sysDevice.getDeviceId())
                    .status(SysDeviceConfig.Status.INACTIVE)
                    .build();
            deviceConfig.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysDeviceConfigRepository.save(deviceConfig);

            SerScanParam scanParam = SerScanParam.builder()
                    .deviceId(sysDevice.getDeviceId())
                    .status(SerScanParam.Status.INACTIVE)
                    .build();
            scanParam.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            serScanParamRepository.save(scanParam);
        }



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
        return getInactiveConfigDevice(sysDeviceList);
    }

    private List<SysDevice> getInactiveConfigDevice(List<SysDevice> preSysDeviceList) {
        List<SysDevice> sysDeviceList = new ArrayList<>();
        List<SysDeviceConfig> deviceConfigList = sysDeviceConfigRepository.findAll();
        for(int i = 0; i < preSysDeviceList.size(); i ++) {
            SysDevice sysDevice = preSysDeviceList.get(i);
            if(sysDevice.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryId() == Constants.SECURITY_CATEGORY_ID) {
                for(int j = 0; j < deviceConfigList.size(); j ++) {
                    if(deviceConfigList.get(j).getStatus().equals(SysDeviceConfig.Status.ACTIVE)) {
                        continue;
                    }
                    if(deviceConfigList.get(j).getDeviceId() == sysDevice.getDeviceId()) {
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


        List<SysDevice> preSysDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysDevice> sysDeviceList = getFilterDeviceByCategory(preSysDeviceList, categoryId, 0, preSysDeviceList.size()).getDataList();
        return getInactiveConfigDevice(sysDeviceList);
    }


}
