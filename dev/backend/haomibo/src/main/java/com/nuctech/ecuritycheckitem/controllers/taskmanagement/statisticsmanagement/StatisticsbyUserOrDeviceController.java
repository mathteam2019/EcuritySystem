/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（人员工时统计/设备运行时长统计 controller 1.0）
 * 文件名：	StatisticsbyUserOrDeviceController.java
 * 描述：	Controller to get statistics by user or device
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

@RestController
@RequestMapping("/task/statistics/")
public class StatisticsbyUserOrDeviceController extends BaseController {

    /**
     * Statistics By User RequestBody
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsByUserRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            String modeId; //work mode id
            String userName; //user name
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime; //start time
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime; //end time

        }

        Integer currentPage; //current page no
        Integer perPage; //record count per page
        String sort;
        StatisticsByUserRequestBody.Filter filter;

    }

    /**
     * Statistics By Device RequestBody
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsByDeviceRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            String deviceType; //device category id
            String deviceName; //device id
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime; //start time
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime; //end time

        }
        String sort;
        Integer currentPage; //current page no
        Integer perPage; //record count per page
        StatisticsByDeviceRequestBody.Filter filter;

    }


    /**
     * statistics by device generate file request
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsByDeviceGenerateRequestBody {

        String idList; //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data
        String sort;
        StatisticsByDeviceRequestBody filter;
    }

    /**
     * statistics by user generate file request
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsByUserGenerateRequestBody {

        String idList; //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data
        String sort;
        StatisticsByUserRequestBody filter;
    }

    /**
     * get statistics by user request
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-statistics-filter-by-user", method = RequestMethod.POST)
    public Object getStatisticsByUserSummary(
            @RequestBody @Valid StatisticsByUserRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
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
        //get statistics from database through userStatisticsService
        TotalStatisticsResponse response = userStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getModeId(), //get work mode id from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getCurrentPage(), //get current page no from input parameter
                requestBody.getPerPage()); //get record count per page from input parameter

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * get statistics by device request body
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-statistics-filter-by-device", method = RequestMethod.POST)
    public Object getStatisticsByDeviceSummary(
            @RequestBody @Valid StatisticsByDeviceRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
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
        //get statistics from database through deviceStatisticsService
        TotalStatisticsResponse response = deviceStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getDeviceType(), //get device category id from input parameter
                requestBody.getFilter().getDeviceName(), //get device id from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getCurrentPage(), //get current page no from input parameter
                requestBody.getPerPage()); //get record count per page from input parameter

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * User Statistics generate pdf file request.
     */
    @RequestMapping(value = "/userstatistics/generate/pdf", method = RequestMethod.POST)
    public Object userStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsByUserGenerateRequestBody requestBody,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
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
        //get statistics from database through userStatisticsService
        TotalStatisticsResponse response = userStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getModeId(), //get work mode id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                null,
                null);

        TreeMap<Long, TotalStatistics> totalStatistics = response.getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsPdfView.setResource(getFontResource()); //get header font
        setDictionary();//set dictionary data key and values
        UserOrDeviceStatisticsPdfView.setMessageSource(messageSource);
        InputStream inputStream = UserOrDeviceStatisticsPdfView.buildPDFDocument(exportList, true); //make inputstream of data to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsbyuser.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User Statistics generate excel file request.
     */
    @RequestMapping(value = "/userstatistics/generate/xlsx", method = RequestMethod.POST)
    public Object userStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsByUserGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid_parameter message when invalid parameters were input
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
        //get statistics from database through userStatisticsService
        TotalStatisticsResponse response = userStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getModeId(), //get work mode id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                null,
                null);

        TreeMap<Long, TotalStatistics> userStatistics = response.getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(userStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data key and values
        UserOrDeviceStatisticsExcelView.setMessageSource(messageSource);
        InputStream inputStream = UserOrDeviceStatisticsExcelView.buildExcelDocument(exportList, true); //make inputstream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByUser.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User Statistics generate word file request.
     */
    @RequestMapping(value = "/userstatistics/generate/docx", method = RequestMethod.POST)
    public Object userStatisticsGenerateWordFile(@RequestBody @Valid StatisticsByUserGenerateRequestBody requestBody,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid_parameter message when invalid parameters were input
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
        //get statistics from database through userStatisticsService
        TotalStatisticsResponse response = userStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getModeId(), //get work mode id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                null,
                null);

        TreeMap<Long, TotalStatistics> userStatistics = response.getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(userStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data key and values
        UserOrDeviceStatisticsWordView.setMessageSource(messageSource);
        InputStream inputStream = UserOrDeviceStatisticsWordView.buildWordDocument(exportList, true); //make inputstream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByUser.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device Statistics generate pdf file request.
     */
    @RequestMapping(value = "/devicestatistics/generate/pdf", method = RequestMethod.POST)
    public Object deviceStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsByDeviceGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid_parameter message when invalid parameters were input
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
        TotalStatisticsResponse response = deviceStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getDeviceType(), //get device category id from input parameter
                requestBody.getFilter().getFilter().getDeviceName(), //get device id from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                null,
                null);

        TreeMap<Long, TotalStatistics> exportList = getExportList(response.getDetailedStatistics(), requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsPdfView.setResource(getFontResource()); //set header font
        setDictionary(); //set dictionary data key and values
        InputStream inputStream = UserOrDeviceStatisticsPdfView.buildPDFDocument(exportList, false); //make inputstream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsbydevice.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device Statistics generate excel file request.
     */
    @RequestMapping(value = "/devicestatistics/generate/xlsx", method = RequestMethod.POST)
    public Object deviceStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsByDeviceGenerateRequestBody requestBody,
                                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid_parameter message when invalid parameters were input
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
        //get statistics from database through deviceStatisticsService
        TotalStatisticsResponse response = deviceStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getDeviceType(), //get device category id from input parameter
                requestBody.getFilter().getFilter().getDeviceName(), //get device id from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                null,
                null);

        TreeMap<Long, TotalStatistics> exportList = getExportList(response.getDetailedStatistics(), requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data key and values
        InputStream inputStream = UserOrDeviceStatisticsExcelView.buildExcelDocument(exportList, false); //make input stream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByDevice.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }


    /**
     * Device Statistics generate word file request.
     */
    @RequestMapping(value = "/devicestatistics/generate/docx", method = RequestMethod.POST)
    public Object deviceStatisticsGenerateWordFile(@RequestBody @Valid StatisticsByDeviceGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid_parameter message when invalid parameters were input
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
        //get statistics from database through deviceStatisticsService
        TotalStatisticsResponse response = deviceStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getDeviceType(), //get device category id from input parameter
                requestBody.getFilter().getFilter().getDeviceName(), //get device id from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                null,
                null);

        TreeMap<Long, TotalStatistics> exportList = getExportList(response.getDetailedStatistics(), requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data key and values
        InputStream inputStream = UserOrDeviceStatisticsWordView.buildWordDocument(exportList, false);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByDevice.docx"); //make input stream of data to be exported

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Extract records to export documents(word/excel/pdf)
     *
     * @param detailedStatistics : total records
     * @param isAll              : true - print all, false - print records in idList
     * @param idList             : idList to be extracted
     * @return
     */
    private TreeMap<Long, TotalStatistics> getExportList(TreeMap<Long, TotalStatistics> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Long, TotalStatistics> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(","); //get ids from idList

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(Long.toString(record.getId()))) { //if specified id is contained idList
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {//if exist
                    exportList.put(entry.getKey(), record);
                }

            }

        } else {//if isAll is true
            exportList = detailedStatistics;
        }

        return exportList;
    }

}