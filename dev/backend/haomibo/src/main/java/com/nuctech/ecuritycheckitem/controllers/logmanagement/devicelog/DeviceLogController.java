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
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
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

    private BooleanBuilder getPredicate(DeviceLogGetByFilterAndPageRequestBody.Filter filter) {
        QSerDevLog builder = QSerDevLog.serDevLog;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getDeviceType())) {
                predicate.and(builder.device.deviceType.eq(filter.getDeviceType()));
            }

            if (!StringUtils.isEmpty(filter.getDeviceName())) {
                predicate.and(builder.device.deviceName.contains(filter.getDeviceName()));
            }

            if (!StringUtils.isEmpty(filter.getUserName())) {
                predicate.and(builder.user.userAccount.contains(filter.getUserName()));
            }

            if (filter.getCategory() != null) {
                predicate.and(builder.category.eq(filter.getCategory()));
            }

            if (filter.getLevel() != null) {
                predicate.and(builder.level.eq(filter.getLevel()));
            }

            if(filter.getOperateStartTime() != null) {
                predicate.and(builder.time.after(filter.getOperateStartTime()));

            }
            if(filter.getOperateEndTime() != null){
                predicate.and(builder.time.before(filter.getOperateEndTime()));
            }
        }
        return predicate;
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

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());


        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serDevLogRepository.count(predicate);
        List<SerDevLog> data = serDevLogRepository.findAll(predicate, pageRequest).getContent();


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

    private List<SerDevLog> getExportList(List<SerDevLog> logList, boolean isAll, String idList) {
        List<SerDevLog> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < logList.size(); i ++) {
                SerDevLog log = logList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(log.getId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(log);
                }
            }
        } else {
            exportList = logList;
        }
        return exportList;
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

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());

        //get all device log list
        List<SerDevLog> logList = StreamSupport
                .stream(serDevLogRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SerDevLog> exportList = getExportList(logList, requestBody.getIsAll(), requestBody.getIdList());

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

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());

        //get all device log list
        List<SerDevLog> logList = StreamSupport
                .stream(serDevLogRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SerDevLog> exportList = getExportList(logList, requestBody.getIsAll(), requestBody.getIdList());
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
