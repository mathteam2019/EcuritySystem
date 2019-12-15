package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.PreviewStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.PreviewStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.PreviewStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
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
public class PreviewStatisticsController extends BaseController {


    /**
     * Preview Statistics RequestBody
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
     * preview statistics generate request body.
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
     * get preview statistics request
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/preview", method = RequestMethod.POST)
    public Object previewStatisticsGet(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = previewStatisticsService.getStatistics(
                requestBody.getFilter().getFieldId(),
                requestBody.getFilter().getDeviceId(),
                requestBody.getFilter().getUserCategory(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getStartTime(),
                requestBody.getFilter().getEndTime(),
                requestBody.getFilter().getStatWidth(),
                requestBody.getCurrentPage(),
                requestBody.getPerPage());

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, response));

        return value;

    }


    /**
     * Preview Statistics generate pdf file request.
     */
    @RequestMapping(value = "/preview/generate/print", method = RequestMethod.POST)
    public Object previewStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        StatisticsRequestBody body = requestBody.getFilter();

        TreeMap<Long, TotalStatistics> totalStatistics = previewStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        PreviewStatisticsPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = PreviewStatisticsPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=previewStatistics.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Preview Statistics generate excel file request.
     */
    @RequestMapping(value = "/preview/generate/export", method = RequestMethod.POST)
    public Object previewStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Long, TotalStatistics> totalStatistics = previewStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = PreviewStatisticsExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=previewStatistics.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Preview Statistics generate Word file request.
     */
    @RequestMapping(value = "/preview/generate/word", method = RequestMethod.POST)
    public Object previewStatisticsGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Long, TotalStatistics> totalStatistics = previewStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = PreviewStatisticsWordView.buildWordDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=previewStatistics.docx");

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
    private TreeMap<Long, TotalStatistics> getExportList(TreeMap<Long, TotalStatistics> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Long, TotalStatistics> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(",");

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

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
