package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.HandExaminationStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.SuspictionHandgoodsStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.SuspictionHandgoodsStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.SuspictionHandgoodsStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.SuspicionHandGoodsPaginationResponse;
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
public class SuspicionHandgoodsStatisticsController extends BaseController {

    /**
     * Suspiction hand goods Statistics RequestBody
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
     * Hand goods list
     */
    public static final List<String> handGoodsIDList = Arrays.asList(new String[]{
            "1000001601",
            "1000001602",
            "1000001603",
            "1000001604",
            "1000001605"
    });

    /**
     * Suspiction hand goods statistics generate request body.
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

    /**
     * Get suspiction hand goods statistics request
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-suspicionhandgoods-statistics", method = RequestMethod.POST)
    public Object getSuspicionHandGoodsSummary(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SuspicionHandGoodsPaginationResponse response = new SuspicionHandGoodsPaginationResponse();

        response = suspictionHandgoodsStatisticsService.getStatistics(
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
     * Suspiction handgoods  generate pdf file request.
     */
    @RequestMapping(value = "/suspiciongoods/generate/print", method = RequestMethod.POST)
    public Object suspicionGoodsStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, TreeMap<String, Long>> totalStatistics = suspictionHandgoodsStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, TreeMap<String, Long>> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        HandExaminationStatisticsPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = SuspictionHandgoodsStatisticsPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=suspicionGoodsStatistics.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Suspiction handgoods Statistics generate excel file request.
     */
    @RequestMapping(value = "/suspiciongoods/generate/export", method = RequestMethod.POST)
    public Object suspicioGoodsStatisticsGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, TreeMap<String, Long>> totalStatistics = suspictionHandgoodsStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, TreeMap<String, Long>> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = SuspictionHandgoodsStatisticsExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=suspicionGoodsStatistics.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Suspiction handgoods Statistics generate word file request.
     */
    @RequestMapping(value = "/suspiciongoods/generate/word", method = RequestMethod.POST)
    public Object suspicioGoodsStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, TreeMap<String, Long>> totalStatistics = suspictionHandgoodsStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, TreeMap<String, Long>> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = SuspictionHandgoodsStatisticsWordView.buildWordDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=suspicionGoodsStatistics.docx");

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
    private TreeMap<Integer, TreeMap<String, Long>> getExportList(TreeMap<Integer, TreeMap<String, Long>> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, TreeMap<String, Long>> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(",");

            for (Map.Entry<Integer, TreeMap<String, Long>> entry : detailedStatistics.entrySet()) {

                TreeMap<String, Long> record = entry.getValue();

                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(Long.toString(record.get("time")))) {
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
