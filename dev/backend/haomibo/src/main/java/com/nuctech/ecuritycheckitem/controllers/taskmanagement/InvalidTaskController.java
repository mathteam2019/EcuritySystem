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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.taskmanagement.InvalidTaskPdfView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.InvalidTaskExcelView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.InvalidTaskWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.DeviceImageModel;
import com.nuctech.ecuritycheckitem.models.reusables.DownImage;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.models.simplifieddb.*;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformCheckService;
import com.nuctech.ecuritycheckitem.service.taskmanagement.InvalidService;
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
@RequestMapping("/task/invalid-task")
public class InvalidTaskController extends BaseController {

    @Autowired
    PlatformCheckService platformCheckService;

    @Autowired
    InvalidService invalidService;

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
        String locale;
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

        SerTaskSimplifiedForInvalidTaskManagement optionalTask = invalidService.getOne(id);

        if (optionalTask == null) { //if invalid task with specified id does not exist
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, optionalTask));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        //set filter to the response. there
        filters.addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName")); //only return modeName from SysWorkMode model
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
        PageResult<HistorySimplifiedForInvalidTableManagement> result = invalidService.getInvalidTaskByFilter(
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
        List<HistorySimplifiedForInvalidTableManagement> data = result.getDataList(); //get data list to return

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
        filters.addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.filterOutAllExcept("task", "scanStartTime", "scanEndTime", "scanDevice", "scanPointsman"))
                .addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskNumber", "field", "taskId", "serScan", "workFlow"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"));
        filters.addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName")); //only return modeName from SysWorkMode model
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

    private List<HistorySimplifiedForInvalidTableManagement> getExportListFromRequest(TaskGenerateRequestBody requestBody, Map<String, String> sortParams) {
        List<HistorySimplifiedForInvalidTableManagement> taskList = new ArrayList<>();
        taskList = invalidService.getExportInvalidTask(
                requestBody.getFilter().getTaskNumber(),//get task numer from request body
                requestBody.getFilter().getMode(),//get mode id from request body
                requestBody.getFilter().getStatus(), //get status from request body
                requestBody.getFilter().getFieldId(),//get field id from request body
                requestBody.getFilter().getUserName(),//get user name from request body
                requestBody.getFilter().getStartTime(),//get start time from request body
                requestBody.getFilter().getEndTime(), //get end time from request body
                sortParams.get("sortBy"), //field name
                sortParams.get("order"),
                requestBody.getIsAll(),
                requestBody.getIdList()); //asc or desc

        //List<SerTaskSimplifiedForProcessTaskManagement> exportList = getExportList(taskList, requestBody.getIsAll(), requestBody.getIdList());
        return taskList;
    }

    /**
     * Task table original image file request.
     */
    @RequestMapping(value = "/generate/image", method = RequestMethod.POST)
    public Object invalidTaskGetOriginalImageFile(@RequestBody @Valid TaskGenerateRequestBody requestBody,
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
            List<InvalidSimplifiedForImageManagement> exportList = invalidService.getExportInvalidImage(requestBody.getFilter().getTaskNumber(),//get task numer from request body
                    requestBody.getFilter().getMode(),//get mode id from request body
                    requestBody.getFilter().getStatus(), //get status from request body
                    requestBody.getFilter().getFieldId(),//get field id from request body
                    requestBody.getFilter().getUserName(),//get user name from request body
                    requestBody.getFilter().getStartTime(),//get start time from request body
                    requestBody.getFilter().getEndTime(), //get end time from request body
                    sortParams.get("sortBy"), //field name
                    sortParams.get("order"),
                    requestBody.getIsAll(),
                    requestBody.getIdList());
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
        List<HistorySimplifiedForInvalidTableManagement> exportList = getExportListFromRequest(requestBody, sortParams);
        setDictionary(requestBody.getLocale()); //set dictionary key and values
        InvalidTaskExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            InvalidTaskExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            InvalidTaskExcelView.setCurrentLocale(Locale.ENGLISH);
        }
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
        List<HistorySimplifiedForInvalidTableManagement> exportList = getExportListFromRequest(requestBody, sortParams);
        setDictionary(requestBody.getLocale()); //set dictionary key and values
        InvalidTaskWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            InvalidTaskWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            InvalidTaskWordView.setCurrentLocale(Locale.ENGLISH);
        }
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
        List<HistorySimplifiedForInvalidTableManagement> exportList = getExportListFromRequest(requestBody, sortParams);
        InvalidTaskPdfView.setResource(getFontResource()); //set header font
        setDictionary(requestBody.getLocale()); //set dicionary key and values
        InvalidTaskPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            InvalidTaskPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            InvalidTaskPdfView.setCurrentLocale(Locale.ENGLISH);
        }
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
