/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AuditLogController）
 * 文件名：	AuditLogController.java
 * 描述：	Audit Log Controller.
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
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogExcelView;
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogPdfView;
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            String operateResult;
            String operateObject;
            String userName;
            String action;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date operateStartTime;
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
     * Audit log  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class AuditLogGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        String sort;
        AuditLogGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * get paginated list from service
     * @param filter
     * @param currentPage
     * @param perPage
     * @return
     */
    private PageResult<SysAuditLog> getPageResult(String sortBy, String order, AuditLogGetByFilterAndPageRequestBody.Filter filter, int currentPage, int perPage) {
        String userName = "";
        String action = "";
        String operateResult = "";
        String operateObject = "";
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            userName = filter.getUserName();
            action = filter.getAction();
            operateResult = filter.getOperateResult();
            operateObject = filter.getOperateObject();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        PageResult<SysAuditLog> result = auditLogService.getAuditLogListByFilter(sortBy, order, userName, action, operateResult, operateObject, operateStartTime, operateEndTime, currentPage, perPage);
        return result;
    }

    /**
     * get export list
     * @param filter
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysAuditLog> getExportResult(String sortBy, String order, AuditLogGetByFilterAndPageRequestBody.Filter filter, boolean isAll, String idList) {
        String userName = "";
        String action = "";
        String operateResult = "";
        String operateObject = "";
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            userName = filter.getUserName();
            action = filter.getAction();
            operateResult = filter.getOperateResult();
            operateObject = filter.getOperateObject();
            operateStartTime = filter.getOperateStartTime();
            operateEndTime = filter.getOperateEndTime();
        }

        List<SysAuditLog> result = auditLogService.getExportList(sortBy, order, userName, action, operateResult, operateObject, operateStartTime, operateEndTime, isAll, idList);
        return result;
    }

    /**
     * Audit Log datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object auditLogGetByFilterAndPage(
            @RequestBody @Valid AuditLogGetByFilterAndPageRequestBody requestBody,
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
        PageResult<SysAuditLog> result = getPageResult(sortBy, order, requestBody.getFilter(), currentPage, perPage);

        long total = result.getTotal();
        List<SysAuditLog> data = result.getDataList();

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

        // Set filters.
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName")); //only return userName from SysWorkMode model
        value.setFilters(filters);
        return value;
    }

    /**
     * Audit Log generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_AUDIT_LOG_EXPORT)
    @RequestMapping(value = "/xlsx", method = RequestMethod.POST)
    public Object auditLogGenerateExcelFile(@RequestBody @Valid AuditLogGenerateRequestBody requestBody,
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
        List<SysAuditLog> exportList = getExportResult(sortBy, order, requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList()); //get export list
        setDictionary(); //set dictionary data
        AuditLogExcelView.setMessageSource(messageSource);
        InputStream inputStream = AuditLogExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=audit-log.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Audit Log generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/docx", method = RequestMethod.POST)
    public Object auditLogGenerateWordFile(@RequestBody @Valid AuditLogGenerateRequestBody requestBody,
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
        List<SysAuditLog> exportList = getExportResult(sortBy, order, requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList()); //get export list
        setDictionary(); //set dictionary data
        AuditLogWordView.setMessageSource(messageSource);
        InputStream inputStream = AuditLogWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=audit-log.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Audit Log generate pdf file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_AUDIT_LOG_PRINT)
    @RequestMapping(value = "/pdf", method = RequestMethod.POST)
    public Object auditLogGeneratePDFFile(@RequestBody @Valid AuditLogGenerateRequestBody requestBody,
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
        List<SysAuditLog> exportList = getExportResult(sortBy, order, requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList()); //get export list
        AuditLogPdfView.setResource(getFontResource()); // set font resource
        //AuditLogPdfView.setResourceFile(resourceFile); // set font resource
        setDictionary(); //set dictionary data
        AuditLogPdfView.setMessageSource(messageSource);
        InputStream inputStream = AuditLogPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=audit-log.pdf"); //set file name

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
}
