/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName AuditLogController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.logmanagement.operatinglog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogExcelView;
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogPdfView;
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
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
@RequestMapping("/log-management/operating-log/audit")
public class AuditLogController extends BaseController {
    @Autowired
    AuditLogService auditLogService;
    /**
     * Audit log datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class AuditLogGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String clientIp;
            String operateResult;
            String operateObject;
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
     * Audit log  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class AuditLogGenerateRequestBody {


        String idList;
        @NotNull
        Boolean isAll;

        AuditLogGetByFilterAndPageRequestBody.Filter filter;
    }


    private PageResult<SysAuditLog> getPageResult(AuditLogGetByFilterAndPageRequestBody.Filter filter, int currentPage, int perPage) {
        String clientIp = "";
        String operateResult = "";
        String operateObject = "";
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            clientIp = filter.getClientIp();
            operateResult = filter.getOperateResult();
            operateObject = filter.getOperateObject();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        PageResult<SysAuditLog> result = auditLogService.getAuditLogListByFilter(clientIp, operateResult, operateObject, operateStartTime, operateEndTime, currentPage, perPage);
        return result;
    }

    private List<SysAuditLog> getExportResult(AuditLogGetByFilterAndPageRequestBody.Filter filter, boolean isAll, String idList) {
        String clientIp = "";
        String operateResult = "";
        String operateObject = "";
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            clientIp = filter.getClientIp();
            operateResult = filter.getOperateResult();
            operateObject = filter.getOperateObject();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        List<SysAuditLog> result = auditLogService.getExportList(clientIp, operateResult, operateObject, operateStartTime, operateEndTime, isAll, idList);
        return result;
    }

    /**
     * Audit Log datatable data.
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object auditLogGetByFilterAndPage(
            @RequestBody @Valid AuditLogGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageResult<SysAuditLog> result = getPageResult(requestBody.getFilter(), requestBody.getCurrentPage(), requestBody.getPerPage());

        long total = result.getTotal();
        List<SysAuditLog> data = result.getDataList();


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
     * Audit Log generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_AUDIT_LOG_EXPORT)
    @RequestMapping(value = "/xlsx", method = RequestMethod.POST)
    public Object auditLogGenerateExcelFile(@RequestBody @Valid AuditLogGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysAuditLog> exportList = getExportResult(requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = AuditLogExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=audit-log.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Audit Log generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_AUDIT_LOG_TOWORD)
    @RequestMapping(value = "/docx", method = RequestMethod.POST)
    public Object auditLogGenerateWordFile(@RequestBody @Valid AuditLogGenerateRequestBody requestBody,
                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysAuditLog> exportList = getExportResult(requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = AuditLogWordView.buildWordDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=audit-log.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Audit Log generate pdf file request.
     */
    @PreAuthorize(Role.Authority.HAS_AUDIT_LOG_PRINT)
    @RequestMapping(value = "/pdf", method = RequestMethod.POST)
    public Object auditLogGeneratePDFFile(@RequestBody @Valid AuditLogGenerateRequestBody requestBody,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysAuditLog> exportList = getExportResult(requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList());
        AuditLogPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = AuditLogPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=audit-log.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }
}
