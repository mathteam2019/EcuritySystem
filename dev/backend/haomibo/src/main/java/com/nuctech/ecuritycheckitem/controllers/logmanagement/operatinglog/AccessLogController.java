/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AccessLogController）
 * 文件名：	AccessLogController.java
 * 描述：	Access Log Controller.
 * 作者名：	Choe
 * 日期：	2019/11/21
 */

package com.nuctech.ecuritycheckitem.controllers.logmanagement.operatinglog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.logmanagement.AccessLogExcelView;
import com.nuctech.ecuritycheckitem.export.logmanagement.AccessLogPdfView;
import com.nuctech.ecuritycheckitem.export.logmanagement.AccessLogWordView;
import com.nuctech.ecuritycheckitem.export.logmanagement.DeviceLogPdfView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.es.EsSysAccessLog;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.AccessLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.util.*;

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
            String userName;
            String action;
            String operateResult;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date operateStartTime;
            //@JsonFormat(pattern = Constants.LOG_DATETIME_FORMAT)
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date operateEndTime;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;
        String sort;
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

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        String sort;
        AccessLogGetByFilterAndPageRequestBody.Filter filter;
        String locale;
    }

    /**
     * get paginated result from service
     * @param filter
     * @param currentPage
     * @param perPage
     * @return
     */
    private PageResult<SysAccessLog> getPageResult(String sortBy, String order, AccessLogGetByFilterAndPageRequestBody.Filter filter, int currentPage, int perPage) {
        String clientIp = "";
        String userName = "";
        String action = "";
        String operateResult = "";
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            action = filter.getAction();
            operateResult = filter.getOperateResult();
            clientIp = filter.getClientIp();
            userName = filter.getUserName();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        PageResult<SysAccessLog> result = accessLogService.getAccessLogListByFilter(sortBy, order, clientIp, userName, action, operateResult, operateStartTime, operateEndTime, currentPage, perPage);
        return result;
    }

    /**
     * get list to be exported
     * @param filter
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysAccessLog> getExportResult(String sortBy, String order, AccessLogGetByFilterAndPageRequestBody.Filter filter, boolean isAll, String idList) {
        String clientIp = "";
        String userName = "";
        String action = "";
        String operateResult = "";
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            action = filter.getAction();
            operateResult = filter.getOperateResult();
            clientIp = filter.getClientIp();
            userName = filter.getUserName();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        List<SysAccessLog> result = accessLogService.getExportList(sortBy, order, clientIp, userName, action, operateResult, operateStartTime, operateEndTime, isAll, idList);
        return result;
    }

    /**
     * Access Log datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object accessLogGetByFilterAndPage(
            @RequestBody @Valid AccessLogGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();
        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }
        PageResult<SysAccessLog> result = getPageResult(sortBy, order, requestBody.getFilter(), currentPage, perPage);

        long total = result.getTotal();
        List<SysAccessLog> data = result.getDataList();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set response message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(total) //set total count
                        .perPage(perPage) //set record count per page
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) total) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current page
                        .to(perPage * currentPage + data.size()) //set end index of current page
                        .data(data) //set data
                        .build()));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName")); //only return userName from SysWorkMode model
        value.setFilters(filters);

        return value;
    }

    /**
     * Access Log generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_ACCESS_LOG_EXPORT)
    @RequestMapping(value = "/xlsx", method = RequestMethod.POST)
    public Object accessLogGenerateExcelFile(@RequestBody @Valid AccessLogGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=access-log.xlsx"); //set filename
        List<SysAccessLog> exportList = getExportResult(sortBy, order, requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList()); //get list to be exported
        if(exportList == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .headers(headers)
                    .body("Exceed Limit");
        }
        setDictionary(requestBody.getLocale()); //set dictionary data
        AccessLogExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            AccessLogExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            AccessLogExcelView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = AccessLogExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported



        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Access Log generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/docx", method = RequestMethod.POST)
    public Object accessLogGenerateWordFile(@RequestBody @Valid AccessLogGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }
        List<SysAccessLog> exportList = getExportResult(sortBy, order, requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList()); //get export list
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=access-log.docx"); //set filename
        if(exportList == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .headers(headers)
                    .body("Exceed Limit");
        }
        setDictionary(requestBody.getLocale()); //set dictionary data
        AccessLogWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            AccessLogWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            AccessLogWordView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = AccessLogWordView.buildWordDocument(exportList); //create inputstream of result to be exported



        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Access Log generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_ACCESS_LOG_PRINT)
    @RequestMapping(value = "/pdf", method = RequestMethod.POST)
    public Object accessLogGeneratePDFFile(@RequestBody @Valid AccessLogGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=access-log.pdf"); //set filename
        List<SysAccessLog> exportList = getExportResult(sortBy, order, requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList()); //get export list
        if(exportList == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .headers(headers)
                    .body("Exceed Limit");
        }
        setDictionary(requestBody.getLocale()); //set dictionary data
        AccessLogPdfView.setResource(getFontResource()); //set font resource
        AccessLogPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            AccessLogPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            AccessLogPdfView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = AccessLogPdfView.buildPDFDocument(exportList); //create inputstream of result to be printed



        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
}

