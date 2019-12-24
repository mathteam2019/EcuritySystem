package com.nuctech.ecuritycheckitem.service.settingmanagement.impl;

import com.nuctech.ecuritycheckitem.controllers.settingmanagement.scanmanagement.ScanParamManagementController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.repositories.SerScanParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SerScanParamsFromRepository;
import com.nuctech.ecuritycheckitem.repositories.SerScanRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.settingmanagement.ScanParamService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ScanParamServiceImpl implements ScanParamService {

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SerScanParamRepository serScanParamRepository;

    @Autowired
    SerScanParamsFromRepository serScanParamsFromRepository;

    /**
     *
     * @param deviceName
     * @param status
     * @return
     */
    BooleanBuilder getPredicate(String deviceName, String status) {
        QSerScanParam builder = QSerScanParam.serScanParam;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(deviceName) && !deviceName.isEmpty()) {
            predicate.and(builder.device.deviceName.contains(deviceName));
        }
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.device.status.eq(status));
        }

        return predicate;

    }

    /**
     *
     * @param paramId
     * @return
     */
    public SerScanParam getById(Long paramId) {

        Optional<SerScanParam> optionalSerScanParam = serScanParamRepository.findOne(QSerScanParam.
                serScanParam.scanParamsId.eq(paramId));
        if (!optionalSerScanParam.isPresent()) {
            return null;
        }

        SerScanParam serScanParam = optionalSerScanParam.get();

        return serScanParam;
    }

    /**
     *
     * @param deviceName
     * @param status
     * @param currentPage
     * @param perPage
     * @return
     */
    public PageResult<SerScanParam> getScanParamListByFilter(String deviceName, String status, Integer currentPage, Integer perPage) {

        BooleanBuilder predicate = getPredicate(deviceName, status);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serScanParamRepository.count(predicate);
        List<SerScanParam> data = serScanParamRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<SerScanParam>(total, data);

    }

    /**
     *
     * @param deviceName
     * @param status
     * @return
     */
    public List<SerScanParam> getAllWithFilter(String deviceName, String status) {

        BooleanBuilder predicate = getPredicate(deviceName, status);

        List<SerScanParam> data = StreamSupport
                .stream(serScanParamRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        return data;

    }

    /**
     * get all records of ser_scan_param table
     * @return
     */
    @Override
    public List<SerScanParam> getAllWithoutFilter() {

        return serScanParamRepository.findAll();

    }

    @Override
    public boolean modifyScanParam(List<Long> paramDeviceIdList, SerScanParam serScanParamNew) {

        SerScanParam serScanParam = serScanParamRepository.findOne(QSerScanParam.serScanParam
                .scanParamsId.eq(serScanParamNew.getScanParamsId())).orElse(null);

        //check if ser scan param is valid.
        if (serScanParam == null) {
            return false;
        }

        List<SerScanParamsFrom> fromParams = serScanParam.getFromParamsList();
        //check from params exist or not
        if (fromParams != null) {
            serScanParamsFromRepository.deleteAll(fromParams);
        }
        // Add edited info.
        serScanParamNew.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        serScanParamNew.setCreatedBy(serScanParam.getCreatedBy());
        serScanParamNew.setCreatedTime(serScanParam.getCreatedTime());
        serScanParamNew.setDeviceId(serScanParam.getDeviceId());

        serScanParamRepository.save(serScanParamNew);

        if(paramDeviceIdList != null && paramDeviceIdList.size() > 0) {
            List<SerScanParamsFrom> serScanParamsFromList = new ArrayList<>();
            List<SerScanParam> serScanParamList = StreamSupport
                    .stream(serScanParamRepository.findAll(QSerScanParam.serScanParam
                    .deviceId.in(paramDeviceIdList)).spliterator(), false)
                    .collect(Collectors.toList());
            for(int i = 0; i < paramDeviceIdList.size(); i ++) {
                boolean isExist = false;
                SerScanParam scanParam = null;
                for(int j = 0; j < serScanParamList.size(); j ++) {
                    if(serScanParamList.get(j).getDeviceId() == paramDeviceIdList.get(i)) {
                        scanParam = serScanParamList.get(j);
                        scanParam.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                        isExist = true;
                        break;
                    }
                }
                if(isExist == false) {
                    scanParam = SerScanParam.builder().deviceId(paramDeviceIdList.get(i)).build();
                    scanParam.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                    serScanParamList.add(scanParam);
                }
                scanParam.setAirCaliWarnTime(serScanParamNew.getAirCaliWarnTime());
                scanParam.setStandByTime(serScanParamNew.getStandByTime());
                scanParam.setAlarmSound(serScanParamNew.getAlarmSound());
                scanParam.setPassSound(serScanParamNew.getPassSound());
                scanParam.setPosErrorSound(serScanParamNew.getPosErrorSound());
                scanParam.setStandSound(serScanParamNew.getStandSound());
                scanParam.setScanSound(serScanParamNew.getScanSound());
                scanParam.setScanOverUseSound(serScanParamNew.getScanOverUseSound());
                scanParam.setAutoRecognise(serScanParamNew.getAutoRecognise());
                scanParam.setRecognitionRate(serScanParamNew.getRecognitionRate());
                scanParam.setSaveScanData(serScanParamNew.getSaveScanData());
                scanParam.setSaveSuspectData(serScanParamNew.getSaveSuspectData());
                scanParam.setFacialBlurring(serScanParamNew.getFacialBlurring());
                scanParam.setChestBlurring(serScanParamNew.getChestBlurring());
                scanParam.setHipBlurring(serScanParamNew.getHipBlurring());
                scanParam.setGroinBlurring(serScanParamNew.getGroinBlurring());
                scanParam.setDeviceStorageAlarm(serScanParamNew.getDeviceStorageAlarm());
                scanParam.setDeviceStorageAlarmPercent(serScanParamNew.getDeviceStorageAlarmPercent());
            }
            for(int i = 0; i < paramDeviceIdList.size(); i ++) {
                Long deviceId = paramDeviceIdList.get(i);
                SerScanParamsFrom paramsFrom = SerScanParamsFrom.builder()
                        .deviceId(deviceId)
                        .fromDeviceId(serScanParam.getDeviceId())
                        .scanParamsId(serScanParam.getScanParamsId())
                        .build();
                paramsFrom.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                serScanParamsFromList.add(paramsFrom);
            }
            serScanParamRepository.saveAll(serScanParamList);
            serScanParamsFromRepository.saveAll(serScanParamsFromList);
        }




        return  true;
    }

}
