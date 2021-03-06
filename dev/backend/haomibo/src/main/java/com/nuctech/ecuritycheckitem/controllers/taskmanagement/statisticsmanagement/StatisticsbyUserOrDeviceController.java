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
import java.util.*;

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
            String statWidth; //statistics width (possible values: hour, day, week, month, quarter, year)

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
            String statWidth; //statistics width (possible values: hour, day, week, month, quarter, year)

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
        String locale;
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
        String locale;
    }

    /**
     * get statistics by user request
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-statistics-filter-by-user/detail", method = RequestMethod.POST)
    public Object getStatisticsByUserSummaryDetail(
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
        TotalTimeStatisticsResponse response = userStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getModeId(), //get work mode id from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getStatWidth(),//get statistics width from input parameter
                requestBody.getCurrentPage(), //get current page no from input parameter
                requestBody.getPerPage()); //get record count per page from input parameter

        return new CommonResponseBody(ResponseMessage.OK, response);
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
        TotalTimeStatistics response = userStatisticsService.getChartStatistics(
                requestBody.getFilter().getModeId(), //get work mode id from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getStatWidth()//get statistics width from input parameter
                ); //get record count per page from input parameter

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

        //get statistics from database through deviceStatisticsService
        TotalTimeStatistics response = deviceStatisticsService.getChartStatistics(
                requestBody.getFilter().getDeviceType(), //get device category id from input parameter
                requestBody.getFilter().getDeviceName(), //get device id from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getStatWidth()//get statistics width from input parameter
                ); //get record count per page from input parameter

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * get statistics by device request body
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-statistics-filter-by-device/detail", method = RequestMethod.POST)
    public Object getStatisticsByDeviceSummaryDetail(
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
        TotalTimeStatisticsResponse response = deviceStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getDeviceType(), //get device category id from input parameter
                requestBody.getFilter().getDeviceName(), //get device id from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getStatWidth(),//get statistics width from input parameter
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
        TotalTimeStatisticsResponse response = userStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getModeId(), //get work mode id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                1,
                Integer.MAX_VALUE);

//        TreeMap<Long, TotalTimeStatistics> totalStatistics = response.getDetailedStatistics();

        //TreeMap<Long, TotalTimeStatistics> exportList = getExportList(null, requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsPdfView.setResource(getFontResource()); //get header font
        setDictionary(requestBody.getLocale());//set dictionary data key and values
        UserOrDeviceStatisticsPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            UserOrDeviceStatisticsPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            UserOrDeviceStatisticsPdfView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = UserOrDeviceStatisticsPdfView.buildPDFDocument(response.getDetailedStatistics(), true); //make inputstream of data to be printed

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
        TotalTimeStatisticsResponse response = userStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getModeId(), //get work mode id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                1,
                Integer.MAX_VALUE);

//        TreeMap<Long, TotalTimeStatistics> userStatistics = response.getDetailedStatistics();

        //TreeMap<Long, TotalTimeStatistics> exportList = getExportList(null, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        UserOrDeviceStatisticsExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            UserOrDeviceStatisticsExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            UserOrDeviceStatisticsExcelView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = UserOrDeviceStatisticsExcelView.buildExcelDocument(response.getDetailedStatistics(), true); //make inputstream of data to be exported

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
        TotalTimeStatisticsResponse response = userStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getModeId(), //get work mode id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                1,
                Integer.MAX_VALUE);

//        TreeMap<Long, TotalTimeStatistics> userStatistics = response.getDetailedStatistics();

        //TreeMap<Long, TotalTimeStatistics> exportList = getExportList(null, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        UserOrDeviceStatisticsWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            UserOrDeviceStatisticsWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            UserOrDeviceStatisticsWordView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = UserOrDeviceStatisticsWordView.buildWordDocument(response.getDetailedStatistics(), true); //make inputstream of data to be exported

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
        TotalTimeStatisticsResponse response = deviceStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getDeviceType(), //get device category id from input parameter
                requestBody.getFilter().getFilter().getDeviceName(), //get device id from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                1, //get current page no from input parameter
                Integer.MAX_VALUE); //get record count per page from input parameter

        //TreeMap<Long, TotalTimeStatistics> exportList = getExportList(null, requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            UserOrDeviceStatisticsPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            UserOrDeviceStatisticsPdfView.setCurrentLocale(Locale.ENGLISH);
        }
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        InputStream inputStream = UserOrDeviceStatisticsPdfView.buildPDFDocument(response.getDetailedStatistics(), false); //make inputstream of data to be exported

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
        TotalTimeStatisticsResponse response = deviceStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getDeviceType(), //get device category id from input parameter
                requestBody.getFilter().getFilter().getDeviceName(), //get device id from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                1, //get current page no from input parameter
                Integer.MAX_VALUE); //get record count per page from input parameter

        //TreeMap<Long, TotalTimeStatistics> exportList = getExportList(null, requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            UserOrDeviceStatisticsExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            UserOrDeviceStatisticsExcelView.setCurrentLocale(Locale.ENGLISH);
        }
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        InputStream inputStream = UserOrDeviceStatisticsExcelView.buildExcelDocument(response.getDetailedStatistics(), false); //make input stream of data to be exported

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
        TotalTimeStatisticsResponse response = deviceStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getDeviceType(), //get device category id from input parameter
                requestBody.getFilter().getFilter().getDeviceName(), //get device id from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                1, //get current page no from input parameter
                Integer.MAX_VALUE); //get record count per page from input parameter

        //TreeMap<Long, TotalTimeStatistics> exportList = getExportList(null, requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            UserOrDeviceStatisticsWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            UserOrDeviceStatisticsWordView.setCurrentLocale(Locale.ENGLISH);
        }
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        InputStream inputStream = UserOrDeviceStatisticsWordView.buildWordDocument(response.getDetailedStatistics(), false);

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
    private TreeMap<Long, TotalTimeStatistics> getExportList(TreeMap<Long, TotalTimeStatistics> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Long, TotalTimeStatistics> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(","); //get ids from idList

            for (Map.Entry<Long, TotalTimeStatistics> entry : detailedStatistics.entrySet()) {

                TotalTimeStatistics record = entry.getValue();
                Long key = entry.getKey();

                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(String.valueOf(key))) { //if specified id is contained idList
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {//if exist
                    exportList.put(entry.getKey(), record);

                }

            }

        } else {//if isAll is true
            for (Map.Entry<Long, TotalTimeStatistics> entry : detailedStatistics.entrySet()) {
                TotalTimeStatistics record = entry.getValue();
                exportList.put(entry.getKey(), record);

            }
        }

        return exportList;
    }

}