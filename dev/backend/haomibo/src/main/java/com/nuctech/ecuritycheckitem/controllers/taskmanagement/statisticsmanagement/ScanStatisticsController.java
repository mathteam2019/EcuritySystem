package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatisticsResponse;
import com.sun.istack.NotNull;
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
     * Scan statistics generate request body.
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
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get Scan statistics
        ScanStatisticsResponse scanStatistics = new ScanStatisticsResponse();

        scanStatistics = scanStatisticsService.getStatistics(
                requestBody.getFilter().getFieldId(),
                requestBody.getFilter().getDeviceId(),
                requestBody.getFilter().getUserCategory(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getStartTime(),
                requestBody.getFilter().getEndTime(),
                requestBody.getFilter().getStatWidth(),
                requestBody.getCurrentPage(),
                requestBody.getPerPage());

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
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, ScanStatistics> totalStatistics = scanStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, ScanStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = ScanStatisticsExcelView.buildExcelDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=scanStatistics.xlsx");

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
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, ScanStatistics> totalStatistics = scanStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, ScanStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = ScanStatisticsWordView.buildWordDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=scanStatistics.docx");

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
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, ScanStatistics> totalStatistics = scanStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, ScanStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        ScanStatisticsPdfView.setResource(res);
        InputStream inputStream = ScanStatisticsPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=scanStatistics.pdf");

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
            String[] splits = idList.split(",");

            for (Map.Entry<Integer, ScanStatistics> entry : detailedStatistics.entrySet()) {

                ScanStatistics record = entry.getValue();

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
