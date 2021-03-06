/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（评价判图 controller 1.0）
 * 文件名：	EvaluateJudgeStatisticsController.java
 * 描述：	Controller to get evaluate judge statistics
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.EvaluateJudgeStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.EvaluateJudgeStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.EvaluateJudgeStatisticsWordView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.InvalidTaskPdfView;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeStatisticsPaginationResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;
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
public class EvaluateJudgeStatisticsController extends BaseController {

    /**
     * get statistics request body
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
            Long fieldId; //fieid id
            Long deviceId; //device id
            Long userCategory; //user category id
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

        StatisticsRequestBody.Filter filter;

    }

    /**
     * statistics generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        String sort;
        StatisticsRequestBody filter;
        String locale;
    }


    /**
     * request body to get Evaluate Judge Statistics
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-evaluatejudge-statistics/detail", method = RequestMethod.POST)
    public Object getEvaluateJudgeSummary(
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

        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();

        //get statistics from database through evaluateJudgeStatisticsService
        response = evaluateJudgeStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFieldId(), //get field id from input parameter
                requestBody.getFilter().getDeviceId(), //get device id from input parameter
                requestBody.getFilter().getUserCategory(), //get user category id from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getStatWidth(), //get statistics width from input parameter
                requestBody.getCurrentPage(), //get current page no from input parameter
                requestBody.getPerPage()); //get per page count from input parameter

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * request body to get Evaluate Judge Statistics
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-evaluatejudge-statistics/chart", method = RequestMethod.POST)
    public Object getEvaluateJudgeSummaryChart(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();

        //get statistics from database through evaluateJudgeStatisticsService
        response = evaluateJudgeStatisticsService.getChartStatistics(
                requestBody.getFilter().getFieldId(), //get field id from input parameter
                requestBody.getFilter().getDeviceId(), //get device id from input parameter
                requestBody.getFilter().getUserCategory(), //get user category id from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getStatWidth() //get statistics width from input parameter
                ); //get per page count from input parameter

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * request body to get Evaluate Judge Statistics
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-evaluatejudge-statistics/total", method = RequestMethod.POST)
    public Object getEvaluateJudgeSummaryTotal(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }



        EvaluateJudgeResponseModel response = new EvaluateJudgeResponseModel();

        //get statistics from database through evaluateJudgeStatisticsService
        response = evaluateJudgeStatisticsService.getTotalStatistics(
                requestBody.getFilter().getFieldId(), //get field id from input parameter
                requestBody.getFilter().getDeviceId(), //get device id from input parameter
                requestBody.getFilter().getUserCategory(), //get user category id from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getStatWidth() //get statistics width from input parameter
                ); //get per page count from input parameter

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * Evaluate Statistics generate pdf file request.
     */
    @RequestMapping(value = "/evaluatejudge/generate/pdf", method = RequestMethod.POST)
    public Object evaluateJudgeStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
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

        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();

        //get statistics from database through evaluateJudgeStatisticsService
        response = evaluateJudgeStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(), //get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(), //get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(), //get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(), //get statistics width from input parameter
                1, //get current page no from input parameter
                Integer.MAX_VALUE); //get per page count from input parameter

        //get statistics from database through evaluateJudgeStatisticsService
//        TreeMap<Integer, EvaluateJudgeResponseModel> totalStatistics = evaluateJudgeStatisticsService.getStatistics(sortBy, order,
//                requestBody.getFilter().getFilter().getFieldId(), //get field id from input parameter
//                requestBody.getFilter().getFilter().getDeviceId(), //get device id from input parameter
//                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
//                requestBody.getFilter().getFilter().getUserName(), //get user name from input parameter
//                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
//                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
//                requestBody.getFilter().getFilter().getStatWidth(), //get statistics width from input parameter
//                null,
//                null).getDetailedStatistics();

        //TreeMap<Integer, EvaluateJudgeResponseModel> exportList = getExportList(null, requestBody.getIsAll(), requestBody.getIdList());
        EvaluateJudgeStatisticsPdfView.setResource(getFontResource()); //set header font
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        EvaluateJudgeStatisticsPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            EvaluateJudgeStatisticsPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            EvaluateJudgeStatisticsPdfView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = EvaluateJudgeStatisticsPdfView.buildPDFDocument(response.getDetailedStatistics()); //make inputstream of data to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=evaluateJudgeStatistics.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * EvaluateJudge Statistics generate excel file request.
     */
    @RequestMapping(value = "/evaluatejudge/generate/xlsx", method = RequestMethod.POST)
    public Object evaluateJudgeGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
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

        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();

        //get statistics from database through evaluateJudgeStatisticsService
        response = evaluateJudgeStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(), //get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(), //get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(), //get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(), //get statistics width from input parameter
                1, //get current page no from input parameter
                Integer.MAX_VALUE); //get per page count from input parameter

        //TreeMap<Integer, EvaluateJudgeResponseModel> exportList = getExportList(null, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        EvaluateJudgeStatisticsExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            EvaluateJudgeStatisticsExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            EvaluateJudgeStatisticsExcelView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = EvaluateJudgeStatisticsExcelView.buildExcelDocument(response.getDetailedStatistics()); //make inputstream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=evaluateJudgeStatistics.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * EvaluateJudge Statistics generate word file request.
     */
    @RequestMapping(value = "/evaluatejudge/generate/docx", method = RequestMethod.POST)
    public Object evaluateJudgeGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
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

        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();

        //get statistics from database through evaluateJudgeStatisticsService
        response = evaluateJudgeStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(), //get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(), //get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(), //get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(), //get statistics width from input parameter
                1, //get current page no from input parameter
                Integer.MAX_VALUE); //get per page count from input parameter

        //TreeMap<Integer, EvaluateJudgeResponseModel> exportList = getExportList(null, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        EvaluateJudgeStatisticsWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            EvaluateJudgeStatisticsWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            EvaluateJudgeStatisticsWordView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = EvaluateJudgeStatisticsWordView.buildWordDocument(response.getDetailedStatistics()); //make input stream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=evaluateJudgeStatistics.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Extract records to export documents(word/excel/pdf)
     * @param detailedStatistics : total records
     * @param isAll : true - print all, false - print records in idList
     * @param idList : idList to be extracted
     * @return
     */
    private TreeMap<Integer, EvaluateJudgeResponseModel> getExportList(TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, EvaluateJudgeResponseModel> exportList = new TreeMap<>();
        if (isAll == false) {
            String[] splits = idList.split(","); //get ids from idList
            for (Map.Entry<Integer, EvaluateJudgeResponseModel> entry : detailedStatistics.entrySet()) {
                EvaluateJudgeResponseModel record = entry.getValue();
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(record.getTime())) { //if specified id is contained idList
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) { //if exist
                    exportList.put(entry.getKey(), record);

                }
            }

        } else { //if isAll is true
            for (Map.Entry<Integer, EvaluateJudgeResponseModel> entry : detailedStatistics.entrySet()) {
                EvaluateJudgeResponseModel record = entry.getValue();
                exportList.put(entry.getKey(), record);

            }
        }
        return exportList;
    }

}
