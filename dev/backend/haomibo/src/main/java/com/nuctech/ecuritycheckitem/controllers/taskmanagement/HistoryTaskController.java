/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（历史任务 controller 1.0）
 * 文件名：	HistoryTaskController.java
 * 描述：	Controller to process API requests of history tasks
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
import com.nuctech.ecuritycheckitem.export.taskmanagement.HistoryTaskExcelView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.HistoryTaskPdfView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.HistoryTaskWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.DeviceImageModel;
import com.nuctech.ecuritycheckitem.models.reusables.DownImage;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTaskManagement;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformCheckService;
import com.nuctech.ecuritycheckitem.service.taskmanagement.HistoryService;
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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;

@RestController
@RequestMapping("/task/history-task")
public class HistoryTaskController extends BaseController {

    @Autowired
    PlatformCheckService platformCheckService;
    /**
     * History Task datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class HistoryGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String taskNumber; //task number
            Long mode; //mode id
            String status; //task status
            Long fieldId; //field id
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
        String sort; //sortby and order ex: deviceName|asc
        HistoryGetByFilterAndPageRequestBody.Filter filter;

    }

    /**
     * Get detailed information of a history task request body
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class HistoryTaskGetByIdRequestBody {

        @NotNull
        @Min(1)
        Long historyId; //id of a history task

    }


    /**
     * history task generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class HistoryGenerateRequestBody {

        String idList; //id list of history tasks which is combined with comma. Example : "1,2,3"
        @NotNull
        Boolean isAll; //true/false if isAll is true, idList is ignored

        String sort; //sortby and order ex: deviceName|asc
        HistoryGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     *  detailed information of a history task.
     */
    @RequestMapping(value = "/get-one", method = RequestMethod.POST)
    public Object historyTaskGetById(
            @RequestBody @Valid HistoryTaskGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        HistorySimplifiedForHistoryTaskManagement optionalHistory = historyService.getOne(requestBody.getHistoryId()); //get detailed history task from historyService

        if (optionalHistory == null) { //if history task with specified id does not exist
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER); //return invalid parameter
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, optionalHistory)); //make response body : response message - ok

        // Set filters.
        SimpleFilterProvider filters = ModelJsonFilters
                .getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName")); //only return "modeName" from SysWorkMode model

        value.setFilters(filters); //apply filter to response data

        return value;
    }

    /**
     * History datatable data.
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object historyTaskGetByFilterAndPage(
            @RequestBody @Valid HistoryGetByFilterAndPageRequestBody requestBody,
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

        Integer currentPage = requestBody.getCurrentPage(); //get current page from input parameter
        Integer perPage = requestBody.getPerPage(); //get record count per page from input parameter
        currentPage --;

        //get paginated result from historyService
        PageResult<HistorySimplifiedForHistoryTaskManagement> result = historyService.getHistoryTaskByFilter(
                requestBody.getFilter().getTaskNumber(), //get task number from input parameter
                requestBody.getFilter().getMode(), //get mode id from input parameter
                requestBody.getFilter().getStatus(), //get status from input parameter
                requestBody.getFilter().getFieldId(), //get field id from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                sortParams.get("sortBy"),
                sortParams.get("order"),
                currentPage,
                perPage);

        long total = result.getTotal(); //set total records count
        List<HistorySimplifiedForHistoryTaskManagement> data = result.getDataList(); //set data list

        //make response body
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(total) //set total count
                        .perPage(perPage) //record count per page
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) total) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current page
                        .to(perPage * currentPage + data.size()) //set end index of current page
                        .data(data) //set data list
                        .build()));

        // Set filters.
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName")); //only return modeName from SysWorkMode model

        value.setFilters(filters);

        return value;
    }

    private List<HistorySimplifiedForHistoryTaskManagement> getExportListFromnRequest(HistoryGenerateRequestBody requestBody, Map<String, String> sortParams) {
        List<HistorySimplifiedForHistoryTaskManagement> taskList = new ArrayList<>();
        taskList = historyService.getHistoryTaskAll(
                requestBody.getFilter().getTaskNumber(), //get task number from input parameter
                requestBody.getFilter().getMode(), //get mode id from input parameter
                requestBody.getFilter().getStatus(), //get status from input parameter
                requestBody.getFilter().getFieldId(), //get field id from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                sortParams.get("sortBy"), //field name
                sortParams.get("order")); //asc or desc

        List<HistorySimplifiedForHistoryTaskManagement> exportList = getExportList(taskList, requestBody.getIsAll(), requestBody.getIdList()); //get data list to be exported with isAll and idList
        return exportList;
    }

    /**
     * Task table original image file request.
     */
    @RequestMapping(value = "/generate/image", method = RequestMethod.POST)
    public Object historyTaskGetOriginalImageFile(@RequestBody @Valid HistoryGenerateRequestBody requestBody,
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
            List<HistorySimplifiedForHistoryTaskManagement> exportList = getExportListFromnRequest(requestBody, sortParams);
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
    public Object historyTaskGenerateExcelFile(@RequestBody @Valid HistoryGenerateRequestBody requestBody,
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

        //get all pending case deal list
        List<HistorySimplifiedForHistoryTaskManagement> exportList = getExportListFromnRequest(requestBody, sortParams);
        setDictionary(); //set dictionary data key and values
        HistoryTaskExcelView.setMessageSource(messageSource);
        InputStream inputStream = HistoryTaskExcelView.buildExcelDocument(exportList); //get inputstream to be exported


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=history-task.xlsx"); //set filename and make header

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }



    /**
     * Task table generate excel file request.
     */
    @RequestMapping(value = "/generate/docx", method = RequestMethod.POST)
    public Object historyTaskGenerateWordFile(@RequestBody @Valid HistoryGenerateRequestBody requestBody,
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

        //get all pending case deal list
        List<HistorySimplifiedForHistoryTaskManagement> exportList = getExportListFromnRequest(requestBody, sortParams);
        setDictionary(); //set dictionary data key and values
        HistoryTaskWordView.setMessageSource(messageSource);
        InputStream inputStream = HistoryTaskWordView.buildWordDocument(exportList); //get inputstream to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=history-task.docx"); //set filename and make header

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * history-task generate pdf file request.
     */
    @RequestMapping(value = "/generate/pdf", method = RequestMethod.POST)
    public Object historyTaskPDFGenerateFile(@RequestBody @Valid HistoryGenerateRequestBody requestBody,
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

        List<HistorySimplifiedForHistoryTaskManagement> exportList = getExportListFromnRequest(requestBody, sortParams);
        setDictionary(); //set dictionary data key and values
        HistoryTaskPdfView.setMessageSource(messageSource);
        HistoryTaskPdfView.setResource(getFontResource()); //set header font

        InputStream inputStream = HistoryTaskPdfView.buildPDFDocument(exportList); //get inputstream of datas to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=history-task.pdf"); //set filename and make header

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Extract records to export documents(word/excel/pdf)
     * @param taskList : total records
     * @param isAll : true - print all, false - print records in idList
     * @param idList : idList to be extracted
     * @return
     */
    private List<HistorySimplifiedForHistoryTaskManagement> getExportList(List<HistorySimplifiedForHistoryTaskManagement> taskList, boolean isAll, String idList) {

        List<HistorySimplifiedForHistoryTaskManagement> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(","); //get ids from idList
            for(int i = 0; i < taskList.size(); i ++) {
                HistorySimplifiedForHistoryTaskManagement task = taskList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(task.getHistoryId().toString())) { //if specified id is contained idList
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {//if exist
                    exportList.add(task);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {//if isAll is true
            for(int i = 0; i < taskList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(taskList.get(i));
            }
        }
        return exportList;
    }
}
