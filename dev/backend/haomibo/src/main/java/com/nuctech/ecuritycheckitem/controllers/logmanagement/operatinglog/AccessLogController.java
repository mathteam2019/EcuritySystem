/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName AccessLogController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.logmanagement.operatinglog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.logmanagement.AccessLogExcelView;
import com.nuctech.ecuritycheckitem.export.logmanagement.AccessLogPdfView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.AccessLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.catalina.AccessLog;
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
@RequestMapping("/log-management/operating-log/access")
public class AccessLogController extends BaseController {

    @Autowired
    AccessLogService accessLogService;
    /**
     * Access log datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class AccessLogGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String clientIp;
            String operateAccount;
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
     * Access log  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class AccessLogGenerateRequestBody {


        String idList;
        @NotNull
        Boolean isAll;

        AccessLogGetByFilterAndPageRequestBody.Filter filter;
    }

    private PageResult<SysAccessLog> getPageResult(AccessLogGetByFilterAndPageRequestBody.Filter filter, int currentPage, int perPage) {
        String clientIp = "";
        String operateAccount = "";
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            clientIp = filter.getClientIp();
            operateAccount = filter.getOperateAccount();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        PageResult<SysAccessLog> result = accessLogService.getAccessLogListByFilter(clientIp, operateAccount, operateStartTime, operateEndTime, currentPage, perPage);
        return result;
    }

    private List<SysAccessLog> getExportResult(AccessLogGetByFilterAndPageRequestBody.Filter filter, boolean isAll, String idList) {
        String clientIp = "";
        String operateAccount = "";
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            clientIp = filter.getClientIp();
            operateAccount = filter.getOperateAccount();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        List<SysAccessLog> result = accessLogService.getExportList(clientIp, operateAccount, operateStartTime, operateEndTime, isAll, idList);
        return result;
    }



    /**
     * Access Log datatable data.
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object accessLogGetByFilterAndPage(
            @RequestBody @Valid AccessLogGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();
        PageResult<SysAccessLog> result = getPageResult(requestBody.getFilter(), requestBody.getCurrentPage(), requestBody.getPerPage());

        long total = result.getTotal();
        List<SysAccessLog> data = result.getDataList();

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
                .getDefaultFilters();

        value.setFilters(filters);

        return value;
    }



    /**
     * Access Log generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_ACCESS_LOG_EXPORT)
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public Object accessLogGenerateExcelFile(@RequestBody @Valid AccessLogGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        List<SysAccessLog> exportList = getExportResult(requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = AccessLogExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=access-log.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Access Log generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_ACCESS_LOG_PRINT)
    @RequestMapping(value = "/print", method = RequestMethod.POST)
    public Object accessLogGeneratePDFFile(@RequestBody @Valid AccessLogGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysAccessLog> exportList = getExportResult(requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList());

        AccessLogPdfView.setResource(res);
        InputStream inputStream = AccessLogPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=access-log.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }
}

