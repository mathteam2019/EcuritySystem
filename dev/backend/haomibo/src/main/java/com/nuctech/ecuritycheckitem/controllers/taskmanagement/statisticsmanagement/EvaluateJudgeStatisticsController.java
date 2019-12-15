package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.EvaluateJudgeStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.EvaluateJudgeStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.EvaluateJudgeStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeStatisticsPaginationResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
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

import javax.persistence.Query;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/task/statistics/")
public class EvaluateJudgeStatisticsController extends BaseController {

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
            Long fieldId;
            Long deviceId;
            Long userCategory;
            String userName;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime;
            String statWidth;
        }

        Integer currentPage;
        Integer perPage;

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

        String idList;
        @NotNull
        Boolean isAll;

        StatisticsRequestBody filter;
    }


    @RequestMapping(value = "/get-evaluatejudge-statistics", method = RequestMethod.POST)
    public Object getEvaluateJudgeSummary(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();

        response = evaluateJudgeStatisticsService.getStatistics(
                requestBody.getFilter().getFieldId(),
                requestBody.getFilter().getDeviceId(),
                requestBody.getFilter().getUserCategory(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getStartTime(),
                requestBody.getFilter().getEndTime(),
                requestBody.getFilter().getStatWidth(),
                requestBody.getCurrentPage(),
                requestBody.getPerPage());

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * Evaluate Statistics generate pdf file request.
     */
    @RequestMapping(value = "/evaluatejudge/generate/print", method = RequestMethod.POST)
    public Object evaluateJudgeStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, EvaluateJudgeResponseModel> totalStatistics = evaluateJudgeStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, EvaluateJudgeResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        EvaluateJudgeStatisticsPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = EvaluateJudgeStatisticsPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=evaluateJudgeStatistics.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * EvaluateJudge Statistics generate pdf file request.
     */
    @RequestMapping(value = "/evaluatejudge/generate/export", method = RequestMethod.POST)
    public Object evaluateJudgeGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, EvaluateJudgeResponseModel> totalStatistics = evaluateJudgeStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, EvaluateJudgeResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = EvaluateJudgeStatisticsExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=evaluateJudgeStatistics.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * EvaluateJudge Statistics generate word file request.
     */
    @RequestMapping(value = "/evaluatejudge/generate/word", method = RequestMethod.POST)
    public Object evaluateJudgeGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, EvaluateJudgeResponseModel> totalStatistics = evaluateJudgeStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, EvaluateJudgeResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());

        setDictionary();
        InputStream inputStream = EvaluateJudgeStatisticsWordView.buildWordDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=evaluateJudgeStatistics.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    private TreeMap<Integer, EvaluateJudgeResponseModel> getExportList(TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, EvaluateJudgeResponseModel> exportList = new TreeMap<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (Map.Entry<Integer, EvaluateJudgeResponseModel> entry : detailedStatistics.entrySet()) {
                EvaluateJudgeResponseModel record = entry.getValue();
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(Long.toString(record.getTime()))) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.put(entry.getKey(), record);
                }
            }

        } else {
            exportList = detailedStatistics;
        }
        return exportList;
    }

}
