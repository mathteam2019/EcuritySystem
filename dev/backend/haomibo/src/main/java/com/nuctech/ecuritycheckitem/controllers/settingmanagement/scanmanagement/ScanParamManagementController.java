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
import com.nuctech.ecuritycheckitem.service.settingmanagement.ScanParamService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

@RestController
@RequestMapping("/system-setting/scan-param")
public class ScanParamManagementController extends BaseController {

    @Autowired
    ScanParamService scanParamService;

    /**
     * Scan Param datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ScanParamByIdRequestBody {

        @NotNull
        Long paramId;
    }


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

        Integer storageAlarm;

        Integer storageAlarmPercent;

        SerScanParam convert2SerScanParam() {

            return SerScanParam
                    .builder()
                    .scanParamsId(this.getScanParamsId())
                    .airCaliWarnTime(this.getAirCaliWarnTime())
                    .standByTime(this.getStandByTime())
                    .alarmSound(this.getAlarmSound())
                    .passSound(this.getPassSound())
                    .posErrorSound(this.getPosErrorSound())
                    .standSound(this.getStandSound())
                    .scanSound(this.getScanSound())
                    .scanOverUseSound(this.getScanOverUseSound())
                    .autoRecognise(this.getAutoRecognise())
                    .recognitionRate(this.getRecognitionRate())
                    .saveScanData(this.getSaveScanData())
                    .saveSuspectData(this.getSaveSuspectData())
                    .facialBlurring(this.getFacialBlurring())
                    .chestBlurring(this.getChestBlurring())
                    .hipBlurring(this.getHipBlurring())
                    .groinBlurring(this.getGroinBlurring())
                    .deviceStorageAlarm(this.getStorageAlarm())
                    .deviceStorageAlarmPercent(this.getStorageAlarmPercent())
                    .build();

        }

    }

    @RequestMapping(value = "/get-by-id", method = RequestMethod.POST)
    public Object scanParamGetById(
            @RequestBody @Valid ScanParamByIdRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long paramId = requestBody.getParamId();

        SerScanParam serScanParam = scanParamService.getById(paramId);
        if (serScanParam == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serScanParam));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceId", "deviceName", "field", "deviceSerial", "guid"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"));
        value.setFilters(filters);
        return value;
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

        Integer currentPage = requestBody.getCurrentPage();
        Integer perPage = requestBody.getPerPage();
        currentPage--;
        PageResult<SerScanParam> result = scanParamService.getScanParamListByFilter(
                requestBody.getFilter().getDeviceName(),
                requestBody.getFilter().getStatus(),
                currentPage,
                perPage);

        long total = result.getTotal();
        List<SerScanParam> data = result.getDataList();

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

        List<SerScanParam> data = scanParamService.getAllWithoutFilter();

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

        SerScanParam serScanParamNew  = requestBody.convert2SerScanParam();




        Long paramDeviceId = requestBody.getFromDeviceId();

        if (scanParamService.modifyScanParam(paramDeviceId, serScanParamNew)) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        else {
            return new CommonResponseBody(ResponseMessage.OK);
        }



    }
}
