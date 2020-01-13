/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（手检统计 controller 1.0）
 * 文件名：	HandExaminationStatisticsController.java
 * 描述：	Controller to get Hand examination statistics
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.EvaluateJudgeStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.HandExaminationStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.HandExaminationStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.HandExaminationStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationStatisticsPaginationResponse;
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
public class HandExaminationStatisticsController extends BaseController {

    /**
     * Request body to get HandExaminationStatistics
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
     * HandExamination statistics generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsGenerateRequestBody {

        String idList; //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        String sort;
        StatisticsRequestBody filter;
    }

    /**
     * get hand-examination-statistics request
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-handexamination-statistics", method = RequestMethod.POST)
    public Object getHandExaminationSummary(
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
        HandExaminationStatisticsPaginationResponse response = new HandExaminationStatisticsPaginationResponse();

        //get statistics from database through handExaminationStatisticsService
        response = handExaminationStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getUserCategory(), //get user category id from input parameter
                requestBody.getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getStatWidth(),//get statistics width from input parameter
                requestBody.getCurrentPage(), //get current page no from input parameter
                requestBody.getPerPage()); //get record count per page from input parameter

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * HandExamination Statistics generate pdf file request.
     */
    @RequestMapping(value = "/handexamination/generate/pdf", method = RequestMethod.POST)
    public Object handExaminationStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
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
        TreeMap<Integer, HandExaminationResponseModel> totalStatistics = handExaminationStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(), //get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();


        TreeMap<Integer, HandExaminationResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        HandExaminationStatisticsPdfView.setResource(getFontResource()); //set header font
        setDictionary(); //set dictionary data key and values
        HandExaminationStatisticsPdfView.setMessageSource(messageSource);
        InputStream inputStream = HandExaminationStatisticsPdfView.buildPDFDocument(exportList);  //make inputstream of data to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=handStatistics.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * HandExamination Statistics generate excel file request.
     */
    @RequestMapping(value = "/handexamination/generate/xlsx", method = RequestMethod.POST)
    public Object handExaminationsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
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
        //get statistics fromd database through handExaminationStatisticsService
        TreeMap<Integer, HandExaminationResponseModel> totalStatistics = handExaminationStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, HandExaminationResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data key and values
        HandExaminationStatisticsExcelView.setMessageSource(messageSource);
        InputStream inputStream = HandExaminationStatisticsExcelView.buildExcelDocument(exportList);  //make inputstream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=handStatistics.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * HandExamination Statistics generate word file request.
     */
    @RequestMapping(value = "/handexamination/generate/docx", method = RequestMethod.POST)
    public Object handExaminationsGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
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
        TreeMap<Integer, HandExaminationResponseModel> totalStatistics = handExaminationStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, HandExaminationResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();  //set dictionary data key and values
        HandExaminationStatisticsWordView.setMessageSource(messageSource);
        InputStream inputStream = HandExaminationStatisticsWordView.buildWordDocument(exportList);  //make input stream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=handStatistics.docx"); //set file name

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
    private TreeMap<Integer, HandExaminationResponseModel> getExportList(TreeMap<Integer, HandExaminationResponseModel> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, HandExaminationResponseModel> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(","); //get ids from idList
            for (Map.Entry<Integer, HandExaminationResponseModel> entry : detailedStatistics.entrySet()) {
                HandExaminationResponseModel record = entry.getValue();
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(Long.toString(record.getTime()))) {  //if specified id is contained idList
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {  //if exist
                    exportList.put(entry.getKey(), record);
                }
            }
        } else { //if isAll is true
            exportList = detailedStatistics;
        }
        return exportList;
    }
}
