package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections4.IterableUtils;
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
import java.util.stream.Collectors;

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
     * preview statistics generate request body.
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
     * preview statistics generate request body.
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

    @RequestMapping(value = "/get-by-user-sum", method = RequestMethod.POST)
    public Object statisticsByUserGet(
            @RequestBody @Valid StatisticsByUserRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        long totalSecs = 0;
        long scanSecs = 0;
        long judgeSecs = 0;
        long handSecs = 0;

        scanSecs = getWorkingSecondsByUser(requestBody, ProcessTaskController.TableType.SER_SCAN);
        judgeSecs = getWorkingSecondsByUser(requestBody, ProcessTaskController.TableType.SER_JUDGE_GRAPH);
        handSecs = getWorkingSecondsByUser(requestBody, ProcessTaskController.TableType.SER_HAND_EXAMINATION);

        totalSecs = scanSecs + judgeSecs + handSecs;

        StatisticsByUserResponse response = new StatisticsByUserResponse();
        response.setTotalSeconds(totalSecs);
        response.setScanSeconds(scanSecs);
        response.setJudgeSeconds(judgeSecs);
        response.setHandSeconds(handSecs);

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/get-by-device-sum", method = RequestMethod.POST)
    public Object statisticsByDeviceGet(
            @RequestBody @Valid StatisticsByDeviceRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        long totalSecs = 0;
        long scanSecs = 0;
        long judgeSecs = 0;
        long handSecs = 0;

        scanSecs = getWorkingSecondsByDevice(requestBody, ProcessTaskController.TableType.SER_SCAN);
        judgeSecs = getWorkingSecondsByDevice(requestBody, ProcessTaskController.TableType.SER_JUDGE_GRAPH);
        handSecs = getWorkingSecondsByDevice(requestBody, ProcessTaskController.TableType.SER_HAND_EXAMINATION);

        totalSecs = scanSecs + judgeSecs + handSecs;

        StatisticsByUserResponse response = new StatisticsByUserResponse();
        response.setTotalSeconds(totalSecs);
        response.setScanSeconds(scanSecs);
        response.setJudgeSeconds(judgeSecs);
        response.setHandSeconds(handSecs);

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * Evaluate Statistics generate pdf file request.
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

        InputStream inputStream = UserOrDeviceStatisticsWordView.buildWordDocument(exportList, true);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByUser.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Evaluate Statistics generate pdf file request.
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
     * EvaluateJudge Statistics generate excel file request.
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
     * EvaluateJudge Statistics generate word file request.
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

        InputStream inputStream = UserOrDeviceStatisticsWordView.buildWordDocument(exportList, false);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByDevice.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

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

    public long getWorkingSecondsByUser(StatisticsByUserRequestBody requestBody, ProcessTaskController.TableType tbType) {

        QSerTask serTask = QSerTask.serTask;
        BooleanBuilder predicate = new BooleanBuilder(serTask.isNotNull());
        StatisticsByUserRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (filter.getModeId() != null) {
                predicate.and(serTask.workFlow.workMode.modeId.eq(filter.getModeId()));
            }
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {
                if (tbType == ProcessTaskController.TableType.SER_SCAN) {
                    Predicate scanUserName = serTask.serScan.scanPointsman.userName.contains(filter.getUserName());
                    predicate.and(scanUserName);
                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {
                    Predicate judgeUserName = serTask.serJudgeGraph.judgeUser.userName.contains(filter.getUserName());
                    predicate.and(judgeUserName);
                } else if (tbType == ProcessTaskController.TableType.SER_HAND_EXAMINATION) {
                    Predicate handUserName = serTask.serHandExamination.handUser.userName.contains(filter.getUserName());
                    predicate.and(handUserName);
                }
            }
            if (filter.getStartTime() != null) {
                if (tbType == ProcessTaskController.TableType.SER_SCAN) {
                    predicate.and(serTask.serScan.scanStartTime.after(filter.getStartTime()));
                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {
                    predicate.and(serTask.serJudgeGraph.judgeStartTime.after(filter.getStartTime()));
                } else {
                    predicate.and(serTask.serHandExamination.handStartTime.after(filter.getStartTime()));
                }
            }
            if (filter.getEndTime() != null) {
                if (tbType == ProcessTaskController.TableType.SER_SCAN) {
                    predicate.and(serTask.serScan.scanEndTime.before(filter.getEndTime()));
                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {
                    predicate.and(serTask.serJudgeGraph.judgeEndTime.before(filter.getEndTime()));
                } else {
                    predicate.and(serTask.serHandExamination.handEndTime.before(filter.getEndTime()));
                }
            }
        }

        Iterable<SerTask> listTasks = serTaskRespository.findAll(predicate);

        long workingSeconds = 0;


        for (SerTask item : listTasks) {

            if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                try {
                    workingSeconds += (item.getSerScan().getScanEndTime().getTime() - item.getSerScan().getScanStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                try {
                    workingSeconds += (item.getSerJudgeGraph().getJudgeEndTime().getTime() - item.getSerJudgeGraph().getJudgeStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            } else {
                try {
                    workingSeconds += (item.getSerHandExamination().getHandEndTime().getTime() - item.getSerHandExamination().getHandStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            }


        }

        return workingSeconds;

    }

    public long getWorkingSecondsByDevice(StatisticsByDeviceRequestBody requestBody, ProcessTaskController.TableType tbType) {

        QSerTask serTask = QSerTask.serTask;

        BooleanBuilder predicate = new BooleanBuilder(serTask.isNotNull());

        StatisticsByDeviceRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {

            if (filter.getDeviceCategoryId() != null) {

                if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                    Predicate scanDeviceCategory = serTask.serScan.scanDevice.categoryId.eq(filter.getDeviceCategoryId());
                    predicate.and(scanDeviceCategory);

                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                    Predicate judgeDeviceCategory = serTask.serJudgeGraph.judgeDevice.categoryId.eq(filter.getDeviceCategoryId());
                    predicate.and(judgeDeviceCategory);

                } else if (tbType == ProcessTaskController.TableType.SER_HAND_EXAMINATION) {

                    Predicate handDeviceCategory = serTask.serHandExamination.handDevice.categoryId.eq(filter.getDeviceCategoryId());
                    predicate.and(handDeviceCategory);

                }
            }
            if (filter.getDeviceId() != null) {

                if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                    Predicate scanDeviceId = serTask.serScan.scanDevice.deviceId.eq(filter.getDeviceId());
                    predicate.and(scanDeviceId);

                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                    Predicate judgeDeviceId = serTask.serJudgeGraph.judgeDevice.deviceId.eq(filter.getDeviceId());
                    predicate.and(judgeDeviceId);

                } else if (tbType == ProcessTaskController.TableType.SER_HAND_EXAMINATION) {

                    Predicate handDeviceId = serTask.serHandExamination.handDevice.deviceId.eq(filter.getDeviceId());
                    predicate.and(handDeviceId);

                }

            }

            if (filter.getStartTime() != null) {

                if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                    predicate.and(serTask.serScan.scanStartTime.after(filter.getStartTime()));

                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                    predicate.and(serTask.serJudgeGraph.judgeStartTime.after(filter.getStartTime()));

                } else {

                    predicate.and(serTask.serHandExamination.handStartTime.after(filter.getStartTime()));

                }

            }

            if (filter.getEndTime() != null) {

                if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                    predicate.and(serTask.serScan.scanEndTime.before(filter.getEndTime()));

                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                    predicate.and(serTask.serJudgeGraph.judgeEndTime.before(filter.getEndTime()));

                } else {

                    predicate.and(serTask.serHandExamination.handEndTime.before(filter.getEndTime()));

                }

            }
        }

        Iterable<SerTask> listTasks = serTaskRespository.findAll(predicate);

        long workingSeconds = 0;

        for (SerTask item : listTasks) {

            if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                try {
                    workingSeconds += (item.getSerScan().getScanEndTime().getTime() - item.getSerScan().getScanStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                try {
                    workingSeconds += (item.getSerJudgeGraph().getJudgeEndTime().getTime() - item.getSerJudgeGraph().getJudgeStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            } else {

                try {
                    workingSeconds += (item.getSerHandExamination().getHandEndTime().getTime() - item.getSerHandExamination().getHandStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            }

        }

        return workingSeconds;

    }

}