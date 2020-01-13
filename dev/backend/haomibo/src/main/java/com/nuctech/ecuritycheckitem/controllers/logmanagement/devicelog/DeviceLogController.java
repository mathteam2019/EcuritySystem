/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceLogController）
 * 文件名：	DeviceLogController.java
 * 描述：	Device Log Controller.
 * 作者名：	Choe
 * 日期：	2019/11/21
 */

package com.nuctech.ecuritycheckitem.controllers.logmanagement.devicelog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPersonalExcelView;
import com.nuctech.ecuritycheckitem.export.logmanagement.DeviceLogExcelView;
import com.nuctech.ecuritycheckitem.export.logmanagement.DeviceLogPdfView;
import com.nuctech.ecuritycheckitem.export.logmanagement.DeviceLogWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.DeviceLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
            String deviceType; //device type
            String deviceName; //device name
            String userName; //user name
            Long category; //category id
            Long level;
            @JsonFormat(pattern = Constants.LOG_DATETIME_FORMAT)
            Date operateStartTime;
            @JsonFormat(pattern = Constants.LOG_DATETIME_FORMAT)
            Date operateEndTime;
        }

        @NotNull
        @Min(1)
        int currentPage;
        String sort;
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

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        String sort;
        DeviceLogGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * get paginated result from service
     * @param filter
     * @param currentPage
     * @param perPage
     * @return
     */
    private PageResult<SerDevLog> getPageResult(String sortBy, String order, DeviceLogGetByFilterAndPageRequestBody.Filter filter, int currentPage, int perPage) {
        String deviceType = "";
        String deviceName = "";
        String userName = "";
        Long category = null;
        Long level = null;
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            deviceType = filter.getDeviceType(); //get device type from input parameter
            deviceName = filter.getDeviceName(); //get device name from input parameter
            userName = filter.getUserName(); //get user name from input parameter
            category = filter.getCategory(); //get category id from input parameter
            level = filter.getLevel(); //get level from input parameter
            operateStartTime = filter.getOperateStartTime(); //get operate start time from input parameter
            operateEndTime = filter.getOperateEndTime(); //get operate end time from input parameter
        }

        PageResult<SerDevLog> result = deviceLogService.getDeviceLogListByFilter(sortBy, order, deviceType, deviceName, userName, category, level,
                operateStartTime, operateEndTime, currentPage, perPage); //get result through service
        return result;
    }

    private List<SerDevLog> getExportResult(String sortBy, String order, DeviceLogGetByFilterAndPageRequestBody.Filter filter, boolean isAll, String idList) {
        String deviceType = "";
        String deviceName = "";
        String userName = "";
        Long category = null;
        Long level = null;
        Date operateStartTime = null;
        Date operateEndTime = null;

        if(filter != null) {
            deviceType = filter.getDeviceType(); //get device type from input parameter
            deviceName = filter.getDeviceName(); //get device name from input parameter
            userName = filter.getUserName(); //get user name from input parameter
            category = filter.getCategory(); //get category id from input parameter
            level = filter.getLevel(); //get level from input parameter
            operateStartTime = filter.getOperateStartTime(); //get operate start time from input parameter
            operateEndTime = filter.getOperateEndTime(); //get operate end time from input parameter
        }

        List<SerDevLog> result = deviceLogService.getExportList(sortBy, order, deviceType, deviceName, userName, category, level,
                operateStartTime, operateEndTime, isAll, idList); //get export list from service
        return result;
    }

    /**
     * Device log datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceLogGetByFilterAndPage(
            @RequestBody @Valid DeviceLogGetByFilterAndPageRequestBody requestBody,
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
        PageResult<SerDevLog> result = getPageResult(sortBy, order, requestBody.getFilter(), currentPage, perPage);

        long total = result.getTotal();
        List<SerDevLog> data = result.getDataList();

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
        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam")) //return all fields except specified fields from SysDevice model
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent")) //return all fields except parent from SysField model
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")) //return all fields except parent from SysDeviceCategory model
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups")); //return all fields except specified fields from SysUser model
        value.setFilters(filters);

        return value;
    }

    /**
     * Device Log generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_LOG_EXPORT)
    @RequestMapping(value = "/xlsx", method = RequestMethod.POST)
    public Object deviceLogGenerateExcelFile(@RequestBody @Valid DeviceLogGenerateRequestBody requestBody,
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
        List<SerDevLog> exportList = getExportResult(sortBy, order, requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList()); //get list to be exported
        setDictionary(); //set dictionary data
        DeviceLogExcelView.setMessageSource(messageSource);
        InputStream inputStream = DeviceLogExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-log.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device Log generate word file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/docx", method = RequestMethod.POST)
    public Object deviceLogGenerateWordFile(@RequestBody @Valid DeviceLogGenerateRequestBody requestBody,
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
        List<SerDevLog> exportList = getExportResult(sortBy, order, requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList()); //set list to be exported
        setDictionary(); //set dictionary data
        DeviceLogWordView.setMessageSource(messageSource);
        InputStream inputStream = DeviceLogWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-log.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device Log generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_LOG_PRINT)
    @RequestMapping(value = "/pdf", method = RequestMethod.POST)
    public Object deviceLogGeneratePDFFile(@RequestBody @Valid DeviceLogGenerateRequestBody requestBody,
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
        List<SerDevLog> exportList = getExportResult(sortBy, order, requestBody.getFilter(), requestBody.getIsAll(), requestBody.getIdList()); //get list to be printed
        DeviceLogPdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary data
        DeviceLogPdfView.setMessageSource(messageSource);
        InputStream inputStream = DeviceLogPdfView.buildPDFDocument(exportList); //create inputstream of result to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-log.pdf"); //set filename
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
}
