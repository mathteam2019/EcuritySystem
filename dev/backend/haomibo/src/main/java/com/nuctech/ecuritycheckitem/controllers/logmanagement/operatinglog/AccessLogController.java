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
@RequestMapping("/log-management/operating-log/access")
public class AccessLogController extends BaseController {
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

    private BooleanBuilder getPredicate(AccessLogGetByFilterAndPageRequestBody.Filter filter) {
        QSysAccessLog builder = QSysAccessLog.sysAccessLog;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getClientIp())) {
                predicate.and(builder.clientIp.contains(filter.getClientIp()));
            }

            if (!StringUtils.isEmpty(filter.getOperateAccount())) {
                predicate.and(builder.operateAccount.contains(filter.getOperateAccount()));
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
     * Access Log datatable data.
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object accessLogGetByFilterAndPage(
            @RequestBody @Valid AccessLogGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        BooleanBuilder predicate = getPredicate(requestBody.getFilter());

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysAccessLogRepository.count(predicate);
        List<SysAccessLog> data = sysAccessLogRepository.findAll(predicate, pageRequest).getContent();


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

    private List<SysAccessLog> getExportList(List<SysAccessLog> logList, boolean isAll, String idList) {
        List<SysAccessLog> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < logList.size(); i ++) {
                SysAccessLog log = logList.get(i);
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
     * Access Log generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_ACCESS_LOG_EXPORT)
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public Object accessLogGenerateExcelFile(@RequestBody @Valid AccessLogGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());

        //get all access log list
        List<SysAccessLog> logList = StreamSupport
                .stream(sysAccessLogRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysAccessLog> exportList = getExportList(logList, requestBody.getIsAll(), requestBody.getIdList());

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

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());

        //get all access log list
        List<SysAccessLog> logList = StreamSupport
                .stream(sysAccessLogRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysAccessLog> exportList = getExportList(logList, requestBody.getIsAll(), requestBody.getIdList());
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

