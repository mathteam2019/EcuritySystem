/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName AuditLogController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.logmanagement.operatinglog;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogExcelView;
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogPdfView;
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
@RequestMapping("/log-management/operating-log/audit")
public class AuditLogController extends BaseController {
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
            @DateTimeFormat(pattern = Constants.LOG_DATETIME_FORMAT)
            Date operateStartTime;
            @DateTimeFormat(pattern = Constants.LOG_DATETIME_FORMAT)
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

    private BooleanBuilder getPredicate(AuditLogGetByFilterAndPageRequestBody.Filter filter) {
        QSysAuditLog builder = QSysAuditLog.sysAuditLog;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getClientIp())) {
                predicate.and(builder.clientIp.contains(filter.getClientIp()));
            }

            if (!StringUtils.isEmpty(filter.getOperateResult())) {
                predicate.and(builder.operateResult.eq(filter.getOperateResult()));
            }

            if (!StringUtils.isEmpty(filter.getOperateObject())) {
                predicate.and(builder.operateObject.contains(filter.getOperateObject()));
            }


            if(filter.getOperateStartTime() != null) {
                predicate.and(builder.operateTime.after(filter.getOperateStartTime()));

            }
            if(filter.getOperateEndTime() != null){
                predicate.and(builder.operateTime.before(filter.getOperateEndTime()));
            }
        }
        return predicate;
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

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysAuditLogRepository.count(predicate);
        List<SysAuditLog> data = sysAuditLogRepository.findAll(predicate, pageRequest).getContent();


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

    private List<SysAuditLog> getExportList(List<SysAuditLog> logList, boolean isAll, String idList) {
        List<SysAuditLog> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < logList.size(); i ++) {
                SysAuditLog log = logList.get(i);
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
     * Audit Log generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_AUDIT_LOG_EXPORT)
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public Object auditLogGenerateExcelFile(@RequestBody @Valid AuditLogGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());


        //get all audit log list
        List<SysAuditLog> logList = StreamSupport
                .stream(sysAuditLogRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysAuditLog> exportList = getExportList(logList, requestBody.getIsAll(), requestBody.getIdList());

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
     * Audit Log generate pdf file request.
     */
    @PreAuthorize(Role.Authority.HAS_AUDIT_LOG_PRINT)
    @RequestMapping(value = "/print", method = RequestMethod.POST)
    public Object auditLogGeneratePDFFile(@RequestBody @Valid AuditLogGenerateRequestBody requestBody,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());


        //get all audit log list
        List<SysAuditLog> logList = StreamSupport
                .stream(sysAuditLogRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysAuditLog> exportList = getExportList(logList, requestBody.getIsAll(), requestBody.getIdList());
        AuditLogPdfView.setResource(res);
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
