/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（过程任务 1.0）
 * 文件名：	ProcessTaskController.java
 * 描述：	Controller to process API requests of tasks being in process
 * 作者名：	Tiny
 * 日期：	2019/12/22
 *
 */

package com.nuctech.ecuritycheckitem.controllers.taskmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.taskmanagement.InvalidTaskPdfView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.ProcessTaskExcelView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.ProcessTaskPdfView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.ProcessTaskWordView;
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

import javax.naming.ldap.HasControls;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;

@RestController
@RequestMapping("/task")
public class ProcessTaskController extends BaseController {

    /**
     * Table type`
     */
    @AllArgsConstructor
    @Getter
    public enum TableType {
        SER_SCAN(1),
        SER_JUDGE_GRAPH(2),
        SER_HAND_EXAMINATION(3);

        private final Integer value;
    }

    /**
     * Process Task datatable request body.
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
            String status; //task status
            Long fieldId; //sccene id
            String userName; //user name
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime; //start time
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime; //end time
        }

        @NotNull
        @Min(1)
        int currentPage; //current page no

        @NotNull
        int perPage; //record count per page
        TaskGetByFilterAndPageRequestBody.Filter filter;

        String sort; //contains sort information
    }

    /**
     * Get detailed info of a process task request body
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
     * process task generate request body.
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
     * process task datatable data.
     */
    @RequestMapping(value = "/process-task/get-one", method = RequestMethod.POST)
    public Object processTaskGetById(
            @RequestBody @Valid TaskGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        Long id = requestBody.getTaskId();

        SerTask optionalTask = taskService.getOne(id);

        if (optionalTask == null) { //if processing task with specified id does not exist
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, optionalTask));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskId", "taskNumber", "taskStatus", "field","history", "serScan", "serJudgeGraph", "serHandExamination", "workFlow", "scanDeviceImages",  "serCheckResult", "note")) //only return specified fields from task model
                .addFilter(ModelJsonFilters.FILTER_SER_CHECK_RESULT, SimpleBeanPropertyFilter.filterOutAllExcept("handGoods", "handAttached")) //return only handGoods and handAttached from SerCheckResult model
                .addFilter(ModelJsonFilters.FILTER_HISTORY, SimpleBeanPropertyFilter.filterOutAllExcept("handAppraise")) //return only handAppraise
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation")) //only return "fieldDesignation" from SysField model
                .addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.serializeAllExcept("task")) // return all fields except "task" from SerScan model
                .addFilter(ModelJsonFilters.FILTER_SER_JUDGE_GRAPH, SimpleBeanPropertyFilter.filterOutAllExcept("judgeDevice", "judgeResult", "judgeTimeout", "judgeUser", "judgeStartTime", "judgeEndTime")) //only return "judgeDevice", "judgeUser", "judgeStartTime", "judgeEndTime" from SerJudgeGraph model
                .addFilter(ModelJsonFilters.FILTER_SER_HAND_EXAMINATION, SimpleBeanPropertyFilter.filterOutAllExcept("handDevice", "handUser", "handStartTime", "handEndTime")) //only return "handDevice", "handUser", "handStartTime", "handEndTime" from SerHandExamination model
                .addFilter(ModelJsonFilters.FILTER_SER_IMAGE, SimpleBeanPropertyFilter.filterOutAllExcept("imageUrl", "imageLabel"))  //only return "imageUrl" and "imageLabel" from SerImage model
                .addFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW, SimpleBeanPropertyFilter.filterOutAllExcept("workMode"))  //only return workModeId from SysWorkFlow
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName")) //only return modeName from SysWorkMode model
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))  //only return "deviceName" from SysDevice model
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName")); //only return "userName" from SysUser model
        value.setFilters(filters);

        return value;
    }

    /**
     * process task datatable data.
     */
    @RequestMapping(value = "/process-task/get-by-filter-and-page", method = RequestMethod.POST)
    public Object processTaskGetByFilterAndPage(
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
        PageResult<SerTask> result = taskService.getProcessTaskByFilter(
                requestBody.getFilter().getTaskNumber(),//task number from request body
                requestBody.getFilter().getMode(), //modeId from request body
                requestBody.getFilter().getStatus(), //status from request body
                requestBody.getFilter().getFieldId(),//field id from request body
                requestBody.getFilter().getUserName(),//user name from request body
                requestBody.getFilter().getStartTime(),//start time from request body
                requestBody.getFilter().getEndTime(),// end time from request body
                sortParams.get("sortBy"),
                sortParams.get("order"),
                currentPage,
                perPage);

        long total = result.getTotal(); // get total count
        List<SerTask> data = result.getDataList(); //get data list to return

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set response message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(total) //set total count
                        .perPage(perPage) //set perpage count
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) total) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current page
                        .to(perPage * currentPage + data.size()) //set last index of last page
                        .data(data) //set data list
                        .build()));

        // Set filters.
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskId", "taskNumber", "taskStatus", "field", "serScan", "workFlow")) //only return specified fields from SerTask model
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation")) //only return "fieldDesignation" from SysField model
                .addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.filterOutAllExcept("scanDevice", "scanPointsman", "scanStartTime", "scanEndTime"))  //only return specified fields from SerScan model
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
     * Process Task table generate excel file request.
     */
    @RequestMapping(value = "/process-task/generate/xlsx", method = RequestMethod.POST)
    public Object processTaskGenerateExcelFile(@RequestBody @Valid TaskGenerateRequestBody requestBody,
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

        List<SerTask> taskList = new ArrayList<>();
        taskList = taskService.getProcessTaskAll(
                requestBody.getFilter().getTaskNumber(),//get task numer from request body
                requestBody.getFilter().getMode(),//get mode id from request body
                requestBody.getFilter().getStatus(), // get status from request body
                requestBody.getFilter().getFieldId(),// get field id from request body
                requestBody.getFilter().getUserName(),//get user name from request body
                requestBody.getFilter().getStartTime(),//get start time from request body
                requestBody.getFilter().getEndTime(), //get end time from request body
                sortParams.get("sortBy"), //field name
                sortParams.get("order")); //asc or desc

        List<SerTask> exportList = getExportList(taskList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        ProcessTaskExcelView.setMessageSource(messageSource);
        InputStream inputStream = ProcessTaskExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=process-task.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Process Task table generate word file request.
     */
    @RequestMapping(value = "/process-task/generate/docx", method = RequestMethod.POST)
    public Object processTaskGenerateWordFile(@RequestBody @Valid TaskGenerateRequestBody requestBody,
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

        List<SerTask> taskList = new ArrayList<>();
        taskList = taskService.getProcessTaskAll(
                requestBody.getFilter().getTaskNumber(),//get task numer from request body
                requestBody.getFilter().getMode(),//get mode id from request body
                requestBody.getFilter().getStatus(), // get status from request body
                requestBody.getFilter().getFieldId(),// get field id from request body
                requestBody.getFilter().getUserName(),//get user name from request body
                requestBody.getFilter().getStartTime(),//get start time from request body
                requestBody.getFilter().getEndTime(), //get end time from request body
                sortParams.get("sortBy"), //field name
                sortParams.get("order")); //asc or desc

        List<SerTask> exportList = getExportList(taskList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();  //set dictionary key and values
        ProcessTaskWordView.setMessageSource(messageSource);
        InputStream inputStream = ProcessTaskWordView.buildWordDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=process-task.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Process-task generate pdf file request.
     */
    @RequestMapping(value = "/process-task/generate/pdf", method = RequestMethod.POST)
    public Object processTaskPDFGenerateFile(@RequestBody @Valid TaskGenerateRequestBody requestBody,
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

        List<SerTask> taskList = new ArrayList<>();
        taskList = taskService.getProcessTaskAll(
                requestBody.getFilter().getTaskNumber(),//get task numer from request body
                requestBody.getFilter().getMode(),//get mode id from request body
                requestBody.getFilter().getStatus(), // get status from request body
                requestBody.getFilter().getFieldId(),// get field id from request body
                requestBody.getFilter().getUserName(),//get user name from request body
                requestBody.getFilter().getStartTime(),//get start time from request body
                requestBody.getFilter().getEndTime(), //get end time from request body
                sortParams.get("sortBy"), //field name
                sortParams.get("order")); //asc or desc

        List<SerTask> exportList = getExportList(taskList, requestBody.getIsAll(), requestBody.getIdList());
        ProcessTaskPdfView.setResource(getFontResource()); //set header font
        setDictionary(); //set dictionary key and values
        ProcessTaskPdfView.setMessageSource(messageSource);
        InputStream inputStream = ProcessTaskPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=process-task.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

}

