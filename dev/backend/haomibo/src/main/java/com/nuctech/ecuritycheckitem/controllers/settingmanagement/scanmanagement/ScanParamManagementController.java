/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName ScanParamManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.settingmanagement.scanmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/system-setting/scan-param")
public class ScanParamManagementController extends BaseController {
    /**
     * Scan Param datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ScanParamGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String deviceName;
            String status;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;


    }

    /**
     * Scan Param modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ScanParamModifyRequestBody {

        @NotNull
        Long scanParamsId;

        Long airCaliWarnTime;

        Long standByTime;

        String alarmSound;

        String passSound;

        String posErrorSound;

        String standSound;

        String scanSound;

        String scanOverUseSound;

        String autoRecognise;

        Long recognitionRate;

        String saveScanData;

        String saveSuspectData;

        String facialBlurring;

        String chestBlurring;

        String hipBlurring;

        String groinBlurring;

        Long fromDeviceId;

    }

    /**
     * Scan Param datatable data.
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object scanParamGetByFilterAndPage(
            @RequestBody @Valid ScanParamGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        QSerScanParam builder = QSerScanParam.serScanParam;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        ScanParamGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getDeviceName())) {
                predicate.and(builder.device.deviceName.contains(filter.getDeviceName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.device.status.eq(filter.getStatus()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serScanParamRepository.count(predicate);
        List<SerScanParam> data = serScanParamRepository.findAll(predicate, pageRequest).getContent();


        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build()));

        // Set filters.

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceId", "deviceName", "field", "deviceSerial", "guid"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"));

        value.setFilters(filters);

        return value;
    }

    /**
     * Scan Param datatable data.
     */
    @RequestMapping(value = "/get-all", method = RequestMethod.POST)
    public Object scanParamGetAll() {

        QSerScanParam builder = QSerScanParam.serScanParam;


        List<SerScanParam> data = serScanParamRepository.findAll();


        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, data));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceId", "deviceName"));

        value.setFilters(filters);


        return value;
    }

    /**
     * Scan Param modify request.
     */
    @PreAuthorize(Role.Authority.HAS_SCAN_PARAM_MODIFY)
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object scanParamModify(
            @RequestBody @Valid ScanParamModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SerScanParam serScanParam = serScanParamRepository.findOne(QSerScanParam.serScanParam
                .scanParamsId.eq(requestBody.getScanParamsId())).orElse(null);

        //check if ser scan param is valid.
        if(serScanParam == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long paramDeviceId = requestBody.getFromDeviceId();

        SerScanParamsFrom fromParams = (serScanParam.getFromParamsList() != null && serScanParam.getFromParamsList().size() > 0)?
                serScanParam.getFromParamsList().get(0): null;
        //check from params exist or not
        if(fromParams != null) {
            if(paramDeviceId != null) {
                fromParams.setFromDeviceId(paramDeviceId);
                fromParams.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                serScanParamsFromRepository.save(fromParams);
            } else {
                serScanParamsFromRepository.delete(fromParams);
            }

        } else if(paramDeviceId != null) {//create judge group
            fromParams = SerScanParamsFrom.
                    builder()
                    .fromDeviceId(paramDeviceId)
                    .deviceId(serScanParam.getDeviceId())
                    .scanParamsId(requestBody.getScanParamsId())
                    .build();
            fromParams.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            serScanParamsFromRepository.save(fromParams);
        }

        serScanParam.setAirCaliWarnTime(requestBody.getAirCaliWarnTime());
        serScanParam.setStandByTime(requestBody.getStandByTime());
        serScanParam.setAlarmSound(requestBody.getAlarmSound());
        serScanParam.setPassSound(requestBody.getPassSound());
        serScanParam.setPosErrorSound(requestBody.getPosErrorSound());
        serScanParam.setStandSound(requestBody.getStandSound());
        serScanParam.setScanSound(requestBody.getScanSound());
        serScanParam.setScanOverUseSound(requestBody.getScanOverUseSound());
        serScanParam.setAutoRecognise(requestBody.getAutoRecognise());
        serScanParam.setRecognitionRate(requestBody.getRecognitionRate());
        serScanParam.setSaveScanData(requestBody.getSaveScanData());
        serScanParam.setSaveSuspectData(requestBody.getSaveSuspectData());
        serScanParam.setFacialBlurring(requestBody.getFacialBlurring());
        serScanParam.setChestBlurring(requestBody.getChestBlurring());
        serScanParam.setHipBlurring(requestBody.getHipBlurring());
        serScanParam.setGroinBlurring(requestBody.getGroinBlurring());

        // Add edited info.
        serScanParam.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serScanParamRepository.save(serScanParam);

        return new CommonResponseBody(ResponseMessage.OK);
    }
}
