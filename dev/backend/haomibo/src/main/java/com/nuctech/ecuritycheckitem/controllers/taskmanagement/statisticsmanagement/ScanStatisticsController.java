/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（统计预览 controller 1.0）
 * 文件名：	ScanStatisticsController.java
 * 描述：	Controller to get scan statistics
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.PreviewStatisticsWordView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatisticsResponse;
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
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/task/statistics/")
public class ScanStatisticsController extends BaseController {

    /**
     * Scan Statistics RequestBody
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            Long fieldId; //field id
            Long deviceId; //device id
            Long userCategory; //user category id
            String userName; //user name
            String workMode; //work mode
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime; //start time
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime; //end time
            String statWidth; //statistics width (possible values: hour, day, week, month, quarter, year)

        }
        String sort;
        Integer currentPage; //current page no
        Integer perPage; //record count per page
        StatisticsRequestBody.Filter filter;

    }

    /**
     * Scan statistics generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsGenerateRequestBody {

        String idList; //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data
        String sort;
        StatisticsRequestBody filter;
        String locale;
    }

    /**
     * get scan statistics request
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/scan", method = RequestMethod.POST)
    public Object scanStatisticsGet(
            @RequestBody @Valid StatisticsRequestBody requestBody,
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
        //get Scan statistics
        ScanStatisticsResponse scanStatistics = new ScanStatisticsResponse();

        //get statistics from database through scanStatisticsService
        scanStatistics = scanStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getWorkMode(),//get work mode from input parameter
                requestBody.getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getStatWidth(),//get statistics width from input parameter
                requestBody.getCurrentPage(),//get current page no from input parameter
                requestBody.getPerPage()); //get record count per page from input parameter

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, scanStatistics));

        return value;

    }

    /**
     * Scan Statistics generate excel file request.
     */
    @RequestMapping(value = "/scan/generate/xlsx", method = RequestMethod.POST)
    public Object scanStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
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
        TreeMap<Integer, ScanStatistics> totalStatistics = scanStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getWorkMode(),//get work mode from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, ScanStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale());//set dictionary data key and values
        ScanStatisticsExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            ScanStatisticsExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            ScanStatisticsExcelView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = ScanStatisticsExcelView.buildExcelDocument(exportList);  //make inputstream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=scanStatistics.xlsx");  //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Scan Statistics generate word file request.
     */
    @RequestMapping(value = "/scan/generate/docx", method = RequestMethod.POST)
    public Object scanStatisticsGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
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
        //get statistics fromd database through scanStatisticsService
        TreeMap<Integer, ScanStatistics> totalStatistics = scanStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getWorkMode(),//get work mode from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, ScanStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        ScanStatisticsWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            ScanStatisticsWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            ScanStatisticsWordView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = ScanStatisticsWordView.buildWordDocument(exportList);//make inputstream of data to be exported


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=scanStatistics.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Scan Statistics generate pdf file request.
     */
    @RequestMapping(value = "/scan/generate/pdf", method = RequestMethod.POST)
    public Object scanStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
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
        //get statistics from database through scanStatisticsService
        TreeMap<Integer, ScanStatistics> totalStatistics = scanStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getWorkMode(),//get work mode from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, ScanStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        ScanStatisticsPdfView.setResource(getFontResource()); //set header font
        ScanStatisticsPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            ScanStatisticsPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            ScanStatisticsPdfView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = ScanStatisticsPdfView.buildPDFDocument(exportList);//make input stream of data to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=scanStatistics.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Extract records to export documents(word/excel/pdf)
     * @param detailedStatistics : total records
     * @param isAll : true - print all, false - print records in idList
     * @param idList : idList to be extracted
     * @return
     */
    private TreeMap<Integer, ScanStatistics> getExportList(TreeMap<Integer, ScanStatistics> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, ScanStatistics> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(",");//get ids from idList

            for (Map.Entry<Integer, ScanStatistics> entry : detailedStatistics.entrySet()) {

                ScanStatistics record = entry.getValue();

                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(Long.toString(record.getTime()))) { //if specified id is contained idList
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) { //if exist
                    exportList.put(entry.getKey(), record);
                }

            }

        } else {//if isAll is true
            exportList = detailedStatistics;
        }

        return exportList;
    }


}
