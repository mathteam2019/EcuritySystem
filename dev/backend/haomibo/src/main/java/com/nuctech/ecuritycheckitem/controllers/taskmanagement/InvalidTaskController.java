/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（无效任务 1.0）
 * 文件名：	InvalidTaskController.java
 * 描述：	Controller to process API requests of invalid tasks
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */


package com.nuctech.ecuritycheckitem.controllers.taskmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.taskmanagement.*;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.*;


@RestController
@RequestMapping("/task/invalid-task")
public class InvalidTaskController extends BaseController {

    /**
     * Invalid Task datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class TaskGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Filter {
            String taskNumber; //task number
            Long mode; //mode id
            String status; // task status
            Long fieldId; // field id
            String userName; //operator name
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime; //start time
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime; //end time
        }

        @NotNull
        @Min(1)
        int currentPage; //currnet page no

        @NotNull
        int perPage; //records count per page

        String sort; //sortby and order ex: deviceName|asc
        TaskGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * Get detailed info of an invalid task request body
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class TaskGetByIdRequestBody {

        @NotNull
        @Min(1)
        Long taskId; // task id

    }

    /**
     * invalid task generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class TaskGenerateRequestBody {

        String idList; //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.

        String sort; //sortby and order ex: deviceName|asc
        TaskGetByFilterAndPageRequestBody.Filter filter;
    }


    /**
     * get detailed information of an invalid task request
     */
    @RequestMapping(value = "/get-one", method = RequestMethod.POST)
    public Object invalidTaskGetById(
            @RequestBody @Valid TaskGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long id = requestBody.getTaskId();

        SerTask optionalTask = taskService.getOne(id);

        if (optionalTask == null) { //if invalid task with specified id does not exist
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, optionalTask));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        //set filter to the response. there
        filters.addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskId", "taskNumber", "taskStatus", "field", "serScan", "workFlow", "note"))   //only return specified fields from SerTask model
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation")) //only return "fieldDesignation" from SysField model
                .addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.serializeAllExcept("task")) //only return "task" from SerScan model
                .addFilter(ModelJsonFilters.FILTER_SER_IMAGE, SimpleBeanPropertyFilter.filterOutAllExcept("imageUrl", "imageLabel")) //only return "imageUrl" and "imageLabel" from SerImage model
                .addFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW, SimpleBeanPropertyFilter.filterOutAllExcept("workMode")) //only return workModeId from SysWorkFlow
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName")) //only return modeName from SysWorkMode model
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName")) //only return "deviceName" from SysDevice model
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName")); //only return "userName" from SysUser model
        value.setFilters(filters);

        return value;
    }

    /**
     * Invalid task datatable data.
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object invalidTaskGetByFilterAndPage(
            @RequestBody @Valid TaskGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (sortParams.isEmpty()) {
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            }
        }

        Integer currentPage = requestBody.getCurrentPage();
        Integer perPage = requestBody.getPerPage();
        currentPage --;

        //get result from service
        PageResult<SerTask> result = taskService.getInvalidTaskByFilter(
                requestBody.getFilter().getTaskNumber(), //task number from request body
                requestBody.getFilter().getMode(), //modeId from request body
                requestBody.getFilter().getStatus(), //status from request body
                requestBody.getFilter().getFieldId(), //field id from request body
                requestBody.getFilter().getUserName(), //user name from request body
                requestBody.getFilter().getStartTime(), //start time from request body
                requestBody.getFilter().getEndTime(), // end time from request body
                sortParams.get("sortBy"),
                sortParams.get("order"),
                currentPage, //current page from request body
                perPage);

        long total = result.getTotal(); // get total count
        List<SerTask> data = result.getDataList(); //get data list to return

        //make response body
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set response message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(total) //set total count
                        .perPage(perPage)  //set perpage count
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) total) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current page
                        .to(perPage * currentPage + data.size()) //set last index of last page
                        .data(data) //set data list
                        .build()));

        // Set filters.
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskId", "taskNumber", "field", "serScan", "workFlow")) //only return specified fields from SerTask model
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation")) //only return "fieldDesignation" from SysField model
                .addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.filterOutAllExcept("scanImage", "scanDevice", "scanPointsman", "scanStartTime", "scanEndTime")) //only return "scanImage", "scanDevice", "scanPointsman", "scanStartTime", "scanEndTime" from SerScan model
                .addFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW, SimpleBeanPropertyFilter.filterOutAllExcept("workMode")) //only return workModeId from SysWorkFlow
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName")) //only return modeName from SysWorkMode model
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName")) //only return "deviceName" from SysDevice model
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName")); //only return "userName" from SysUser model
        value.setFilters(filters);

        return value;
    }

    /**
     * Extract records to export documents(word/excel/pdf)
     * @param taskList : total records
     * @param isAll : true - print all, false - print records in idList
     * @param idList : idList to be extracted
     * @return
     */
    private List<SerTask> getExportList(List<SerTask> taskList, boolean isAll, String idList) {

        List<SerTask> exportList = new ArrayList<>();
        if(isAll == false) { //if isAll is false
            String[] splits = idList.split(","); //get ids list from idList
            for(int i = 0; i < taskList.size(); i ++) {
                SerTask task = taskList.get(i); //get task using task id
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(task.getTaskId().toString())) { //check if idList contains specified task id
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) { //if is All is true
                    exportList.add(task);
                }
            }
        } else {
            exportList = taskList;
        }

        return exportList;
    }

    /**
     * Task table generate excel file request.
     */
    @RequestMapping(value = "/generate/xlsx", method = RequestMethod.POST)
    public Object invalidTaskGenerateExcelFile(@RequestBody @Valid TaskGenerateRequestBody requestBody,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid_parameter message when invalid parameters were input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (sortParams.isEmpty()) {
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            }
        }

        //get all pending case deal list
        List<SerTask> taskList = new ArrayList<>();
        taskList = taskService.getInvalidTaskAll(
                requestBody.getFilter().getTaskNumber(),//get task numer from request body
                requestBody.getFilter().getMode(),//get mode id from request body
                requestBody.getFilter().getStatus(), //get status from request body
                requestBody.getFilter().getFieldId(),//get field id from request body
                requestBody.getFilter().getUserName(),//get user name from request body
                requestBody.getFilter().getStartTime(),//get start time from request body
                requestBody.getFilter().getEndTime(), //get end time from request body
                sortParams.get("sortBy"), //field name
                sortParams.get("order")); //asc or desc

        List<SerTask> exportList = getExportList(taskList, requestBody.getIsAll(), requestBody.getIdList());

        setDictionary(); //set dictionary key and values
        InputStream inputStream = InvalidTaskExcelView.buildExcelDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invalid-task.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Invalid task table generate word file request.
     */
    @RequestMapping(value = "/generate/docx", method = RequestMethod.POST)
    public Object invalidTaskGenerateWordFile(@RequestBody @Valid TaskGenerateRequestBody requestBody,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (sortParams.isEmpty()) {
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            }
        }

        //get all pending case deal list
        List<SerTask> taskList = new ArrayList<>();
        taskList = taskService.getInvalidTaskAll(
                requestBody.getFilter().getTaskNumber(),//get task numer from request body
                requestBody.getFilter().getMode(),//get mode id from request body
                requestBody.getFilter().getStatus(), //get status from request body
                requestBody.getFilter().getFieldId(),//get field id from request body
                requestBody.getFilter().getUserName(),//get user name from request body
                requestBody.getFilter().getStartTime(),//get start time from request body
                requestBody.getFilter().getEndTime(), //get end time from request body
                sortParams.get("sortBy"), //field name
                sortParams.get("order")); //asc or desc

        List<SerTask> exportList = getExportList(taskList, requestBody.getIsAll(), requestBody.getIdList());

        setDictionary(); //set dictionary key and values
        InputStream inputStream = InvalidTaskWordView.buildWordDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invalid-task.docx"); //set file name

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Invalid-task generate pdf file request.
     */
    @RequestMapping(value = "/generate/pdf", method = RequestMethod.POST)
    public Object invalidTaskPDFGenerateFile(@RequestBody @Valid TaskGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return INVALID_PARAMETER when invalid parameters were input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (sortParams.isEmpty()) {
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            }
        }

        //get all pending case deal list
        List<SerTask> taskList = new ArrayList<>();
        taskList = taskService.getInvalidTaskAll(
                requestBody.getFilter().getTaskNumber(),//get task numer from request body
                requestBody.getFilter().getMode(),//get mode id from request body
                requestBody.getFilter().getStatus(), //get status from request body
                requestBody.getFilter().getFieldId(),//get field id from request body
                requestBody.getFilter().getUserName(),//get user name from request body
                requestBody.getFilter().getStartTime(),//get start time from request body
                requestBody.getFilter().getEndTime(), //get end time from request body
                sortParams.get("sortBy"), //field name
                sortParams.get("order")); //asc or desc

        List<SerTask> exportList = getExportList(taskList, requestBody.getIsAll(), requestBody.getIdList());
        InvalidTaskPdfView.setResource(getFontResource()); //set header font
        setDictionary(); //set dicionary key and values
        InputStream inputStream = InvalidTaskPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invalid-task.pdf"); //set file name

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

}
