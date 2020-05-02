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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.taskmanagement.ProcessTaskExcelView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.ProcessTaskPdfView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.ProcessTaskWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.DeviceImageModel;
import com.nuctech.ecuritycheckitem.models.reusables.DownImage;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForProcessTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTaskManagement;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformCheckService;
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
@RequestMapping("/task")
public class ProcessTaskController extends BaseController {
    @Autowired
    PlatformCheckService platformCheckService;
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
            String taskStatus; //task status
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
        String locale;
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

        SerTaskSimplifiedForProcessTaskManagement optionalTask = taskService.getOne(id);

        if (optionalTask == null) { //if processing task with specified id does not exist
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, optionalTask));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName")); //only return modeName from SysWorkMode model
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
        PageResult<HistorySimplifiedForProcessTableManagement> result = historyService.getProcessTaskByFilter(
                requestBody.getFilter().getTaskNumber(),//task number from request body
                requestBody.getFilter().getMode(), //modeId from request body
                requestBody.getFilter().getTaskStatus(), //status from request body
                requestBody.getFilter().getFieldId(),//field id from request body
                requestBody.getFilter().getUserName(),//user name from request body
                requestBody.getFilter().getStartTime(),//start time from request body
                requestBody.getFilter().getEndTime(),// end time from request body
                sortParams.get("sortBy"),
                sortParams.get("order"),
                currentPage,
                perPage);

        long total = result.getTotal(); // get total count
        List<HistorySimplifiedForProcessTableManagement> data = result.getDataList(); //get data list to return

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
        filters.addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.filterOutAllExcept("task", "scanStartTime", "scanEndTime", "scanDevice", "scanPointsman"))
                .addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskNumber", "field", "taskId", "serScan", "workFlow", "serAssignList", "taskStatus"))
                .addFilter(ModelJsonFilters.FILTER_SER_ASSIGN, SimpleBeanPropertyFilter.filterOutAllExcept("assignId"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"));
        filters.addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"));

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
    private List<SerTaskSimplifiedForProcessTaskManagement> getExportList(List<SerTaskSimplifiedForProcessTaskManagement> taskList, boolean isAll, String idList) {

        List<SerTaskSimplifiedForProcessTaskManagement> exportList = new ArrayList<>();
        if(isAll == false) { //if isAll is false
            String[] splits = idList.split(","); //get ids list from idList
            for(int i = 0; i < taskList.size(); i ++) {
                SerTaskSimplifiedForProcessTaskManagement task = taskList.get(i); //get task using task id
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(task.getTaskId().toString())) { //check if idList contains specified task id
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) { //if is All is true
                    exportList.add(task);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < taskList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(taskList.get(i));
            }
        }
        return exportList;
    }

    private List<SerTaskSimplifiedForProcessTableManagement> getExportListFromRequest(TaskGenerateRequestBody requestBody, Map<String, String> sortParams) {
        List<SerTaskSimplifiedForProcessTableManagement> taskList = new ArrayList<>();
        taskList = taskService.getExportProcessTask(
                requestBody.getFilter().getTaskNumber(),//get task numer from request body
                requestBody.getFilter().getMode(),//get mode id from request body
                requestBody.getFilter().getTaskStatus(), // get status from request body
                requestBody.getFilter().getFieldId(),// get field id from request body
                requestBody.getFilter().getUserName(),//get user name from request body
                requestBody.getFilter().getStartTime(),//get start time from request body
                requestBody.getFilter().getEndTime(), //get end time from request body
                sortParams.get("sortBy"), //field name
                sortParams.get("order"),
                requestBody.getIdList()); //asc or desc

        //List<SerTaskSimplifiedForProcessTaskManagement> exportList = getExportList(taskList, requestBody.getIsAll(), requestBody.getIdList());
        return taskList;
    }

    /**
     * Task table original image file request.
     */
    @RequestMapping(value = "/process-task/generate/image", method = RequestMethod.POST)
    public Object processTaskGetOriginalImageFile(@RequestBody @Valid TaskGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SerPlatformCheckParams> paramsList = platformCheckService.findAll();
        boolean isEnabledCartoon = false;
        boolean isEnabledOriginal = false;
        if(paramsList.size() > 0) {
            String historyDataExport = paramsList.get(0).getHistoryDataExport();
            String splitList[] = historyDataExport.split(",");
            for(int i = 0; i < splitList.length; i ++) {
                if(splitList[i].equals(SerPlatformCheckParams.HistoryData.CARTOON)) {
                    isEnabledCartoon = true;
                }
                if(splitList[i].equals(SerPlatformCheckParams.HistoryData.ORIGINAL)) {
                    isEnabledOriginal = true;
                }
            }
        }
        DownImage downImage = new DownImage();
        downImage.setEnabledCartoon(isEnabledCartoon);
        downImage.setEnabledOriginal(isEnabledOriginal);
        if(isEnabledCartoon == true || isEnabledOriginal == true) {
            Map<String, String> sortParams = new HashMap<String, String>();
            if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
                sortParams = Utils.getSortParams(requestBody.getSort());
                if (sortParams.isEmpty()) {
                    return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
                }
            }

            //get all pending case deal list
            List<SerTaskSimplifiedForProcessTableManagement> exportList = getExportListFromRequest(requestBody, sortParams);
            List<String> cartoonImageList = new ArrayList<>();
            List<String> originalImageList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for(int i = 0; i < exportList.size(); i ++) {
                String scanDeviceImages = exportList.get(i).getSerScan().getScanDeviceImages();
                try {
                    List<DeviceImageModel> deviceImageModel = objectMapper.readValue(scanDeviceImages, objectMapper.getTypeFactory().constructCollectionType(
                            List.class, DeviceImageModel.class));
                    for(int j = 0; j < deviceImageModel.size(); j ++) {
                        if(isEnabledCartoon) {
                            cartoonImageList.add(deviceImageModel.get(j).getCartoon());
                        }
                        if(isEnabledOriginal) {
                            originalImageList.add(deviceImageModel.get(j).getImage());
                        }
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                }
                if(isEnabledCartoon) {

                }
            }
            downImage.setCartoonImageList(cartoonImageList);
            downImage.setOriginalImageList(originalImageList);
        }
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, downImage));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        value.setFilters(filters);
        return value;
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

        List<SerTaskSimplifiedForProcessTableManagement> exportList = getExportListFromRequest(requestBody, sortParams);
        setDictionary(requestBody.getLocale());
        ProcessTaskExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            ProcessTaskExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            ProcessTaskExcelView.setCurrentLocale(Locale.ENGLISH);
        }
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

        List<SerTaskSimplifiedForProcessTableManagement> exportList = getExportListFromRequest(requestBody, sortParams);
        setDictionary(requestBody.getLocale());  //set dictionary key and values
        ProcessTaskWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            ProcessTaskWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            ProcessTaskWordView.setCurrentLocale(Locale.ENGLISH);
        }
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

        List<SerTaskSimplifiedForProcessTableManagement> exportList = getExportListFromRequest(requestBody, sortParams);
        ProcessTaskPdfView.setResource(getFontResource()); //set header font
        setDictionary(requestBody.getLocale()); //set dictionary key and values
        ProcessTaskPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            ProcessTaskPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            ProcessTaskPdfView.setCurrentLocale(Locale.ENGLISH);
        }
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

