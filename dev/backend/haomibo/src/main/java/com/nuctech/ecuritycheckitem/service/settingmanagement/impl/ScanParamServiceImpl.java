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
    public boolean modifyScanParam(Long paramDeviceId, SerScanParam serScanParamNew) {

        SerScanParam serScanParam = serScanParamRepository.findOne(QSerScanParam.serScanParam
                .scanParamsId.eq(serScanParamNew.getScanParamsId())).orElse(null);

        //check if ser scan param is valid.
        if (serScanParam == null) {
            return false;
        }

        SerScanParamsFrom fromParams = (serScanParam.getFromParamsList() != null && serScanParam.getFromParamsList().size() > 0) ?
                serScanParam.getFromParamsList().get(0) : null;
        //check from params exist or not
        if (fromParams != null) {
            if (paramDeviceId != null) {
                fromParams.setFromDeviceId(paramDeviceId);
                fromParams.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                serScanParamsFromRepository.save(fromParams);
            } else {
                serScanParamsFromRepository.delete(fromParams);
            }

        } else if (paramDeviceId != null) {//create judge group
            fromParams = SerScanParamsFrom.
                    builder()
                    .fromDeviceId(paramDeviceId)
                    .deviceId(serScanParam.getDeviceId())
                    .scanParamsId(serScanParam.getScanParamsId())
                    .build();
            fromParams.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            serScanParamsFromRepository.save(fromParams);
        }

        serScanParam = serScanParamNew;
        // Add edited info.
        serScanParam.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serScanParamRepository.save(serScanParam);

        return  true;
    }

}
