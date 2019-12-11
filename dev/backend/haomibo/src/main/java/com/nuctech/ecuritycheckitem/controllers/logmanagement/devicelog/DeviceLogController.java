/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName DeviceLogController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.logmanagement.devicelog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.logmanagement.DeviceLogExcelView;
import com.nuctech.ecuritycheckitem.export.logmanagement.DeviceLogPdfView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.DeviceLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/log-management/device-log")
public class DeviceLogController extends BaseController {
    @Autowired
    DeviceLogService deviceLogService;
    /**
     * Device log datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceLogGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String deviceType;
            String deviceName;
            String userName;
            Long category;
            Long level;

            @JsonFormat(pattern = Constants.LOG_DATETIME_FORMAT)
            Date operateStartTime;

            @JsonFormat(pattern = Constants.LOG_DATETIME_FORMAT)
            Date operateEndTime;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;
    }

    /**
     * Device log  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceLogGenerateRequestBody {

        String idList;
        @NotNull
        Boolean isAll;

        DeviceLogGetByFilterAndPageRequestBody.Filter filter;
    }


    private PageResult<SerDevLog> getPageResult(DeviceLogGetByFilterAndPageRequestBody.Filter filter, int currentPage, int perPage) {
        String deviceType = "";
        String deviceName = "";
        String userName = "";
        Long category = null;
        Long level = null;
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            deviceType = filter.getDeviceType();
            deviceName = filter.getDeviceName();
            userName = filter.getUserName();
            category = filter.getCategory();
            level = filter.getLevel();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        PageResult<SerDevLog> result = deviceLogService.getDeviceLogListByFilter(deviceType, deviceName, userName, category, level,
                operateStartTime, operateEndTime, currentPage, perPage);
        return result;
    }

    private List<SerDevLog> getExportResult(DeviceLogGetByFilterAndPageRequestBody.Filter filter, boolean isAll, String idList) {
        String deviceType = "";
        String deviceName = "";
        String userName = "";
        Long category = null;
        Long level = null;
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            deviceType = filter.getDeviceType();
            deviceName = filter.getDeviceName();
            userName = filter.getUserName();
            category = filter.getCategory();
            level = filter.getLevel();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        List<SerDevLog> result = deviceLogService.getExportList(deviceType, deviceName, userName, category, level,
                operateStartTime, operateEndTime, isAll, idList);
        return result;
    }

    /**
     * Device log datatable data.
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceLogGetByFilterAndPage(
            @RequestBody @Valid DeviceLogGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();
        PageResult<SerDevLog> result = getPageResult(requestBody.getFilter(), currentPage, perPage);

        long total = result.getTotal();
        List<SerDevLog> data = result.getDataList();


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
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }



    /**
     * Device Log generate excel file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_LOG_EXPORT)
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public Object deviceLogGenerateExcelFile(@RequestBody @Valid DeviceLogGenerateRequestBody requestBody,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SerDevLog> exportList = getExportResult(requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = DeviceLogExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-log.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Device Log generate excel file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_LOG_PRINT)
    @RequestMapping(value = "/print", method = RequestMethod.POST)
    public Object deviceLogGeneratePDFFile(@RequestBody @Valid DeviceLogGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SerDevLog> exportList = getExportResult(requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList());
        DeviceLogPdfView.setResource(res);
        InputStream inputStream = DeviceLogPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-log.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }
}
