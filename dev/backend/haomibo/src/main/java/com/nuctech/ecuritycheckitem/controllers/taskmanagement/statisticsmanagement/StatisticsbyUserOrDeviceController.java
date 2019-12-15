package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsWordView;
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

            Long modeId;
            String userName;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime;

        }

        Integer currentPage;
        Integer perPage;

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

            Long deviceCategoryId;
            Long deviceId;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime;

        }

        Integer currentPage;
        Integer perPage;
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

        String idList;
        @NotNull
        Boolean isAll;

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

        String idList;
        @NotNull
        Boolean isAll;

        StatisticsByUserRequestBody filter;
    }

    /**
     * get statistics by user request
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-statistics-filter-by-user", method = RequestMethod.POST)
    public Object getStatisticsByUserSummary(
            @RequestBody @Valid StatisticsByUserRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = userStatisticsService.getStatistics(
                requestBody.getFilter().getModeId(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getStartTime(),
                requestBody.getFilter().getEndTime(),
                requestBody.getCurrentPage(),
                requestBody.getPerPage());

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * get statistics by device request body
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-statistics-filter-by-device", method = RequestMethod.POST)
    public Object getStatisticsByDeviceSummary(
            @RequestBody @Valid StatisticsByDeviceRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = deviceStatisticsService.getStatistics(
                requestBody.getFilter().getDeviceCategoryId(),
                requestBody.getFilter().getDeviceId(),
                requestBody.getFilter().getStartTime(),
                requestBody.getFilter().getEndTime(),
                requestBody.getCurrentPage(),
                requestBody.getPerPage());

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * User Statistics generate pdf file request.
     */
    @RequestMapping(value = "/userstatistics/generate/print", method = RequestMethod.POST)
    public Object userStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsByUserGenerateRequestBody requestBody,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = userStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getModeId(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                null,
                null);

        TreeMap<Long, TotalStatistics> totalStatistics = response.getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = UserOrDeviceStatisticsPdfView.buildPDFDocument(exportList, true);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsbyuser.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User Statistics generate excel file request.
     */
    @RequestMapping(value = "/userstatistics/generate/export", method = RequestMethod.POST)
    public Object userStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsByUserGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = userStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getModeId(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                null,
                null);

        TreeMap<Long, TotalStatistics> userStatistics = response.getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(userStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = UserOrDeviceStatisticsExcelView.buildExcelDocument(exportList, true);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByUser.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User Statistics generate word file request.
     */
    @RequestMapping(value = "/userstatistics/generate/word", method = RequestMethod.POST)
    public Object userStatisticsGenerateWordFile(@RequestBody @Valid StatisticsByUserGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = userStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getModeId(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                null,
                null);

        TreeMap<Long, TotalStatistics> userStatistics = response.getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(userStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = UserOrDeviceStatisticsWordView.buildWordDocument(exportList, true);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByUser.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device Statistics generate pdf file request.
     */
    @RequestMapping(value = "/devicestatistics/generate/print", method = RequestMethod.POST)
    public Object deviceStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsByDeviceGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = deviceStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getDeviceCategoryId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                null,
                null);

        TreeMap<Long, TotalStatistics> exportList = getExportList(response.getDetailedStatistics(), requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = UserOrDeviceStatisticsPdfView.buildPDFDocument(exportList, false);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsbydevice.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device Statistics generate excel file request.
     */
    @RequestMapping(value = "/devicestatistics/generate/export", method = RequestMethod.POST)
    public Object userStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsByDeviceGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = deviceStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getDeviceCategoryId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                null,
                null);

        TreeMap<Long, TotalStatistics> exportList = getExportList(response.getDetailedStatistics(), requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = UserOrDeviceStatisticsExcelView.buildExcelDocument(exportList, false);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByDevice.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }


    /**
     * Device Statistics generate word file request.
     */
    @RequestMapping(value = "/devicestatistics/generate/word", method = RequestMethod.POST)
    public Object userStatisticsGenerateWordFile(@RequestBody @Valid StatisticsByDeviceGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = deviceStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getDeviceCategoryId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                null,
                null);

        TreeMap<Long, TotalStatistics> exportList = getExportList(response.getDetailedStatistics(), requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = UserOrDeviceStatisticsWordView.buildWordDocument(exportList, false);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByDevice.docx");

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