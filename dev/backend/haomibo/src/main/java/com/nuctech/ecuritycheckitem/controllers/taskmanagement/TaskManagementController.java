package com.nuctech.ecuritycheckitem.controllers.taskmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import lombok.*;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping("/task")
public class TaskManagementController extends BaseController {

    /**
     * Statistics Response Type
     */
    public static class StatisticWidth {
        public static final String HOUR = "hour";
        public static final String DAY = "day";
        public static final String WEEK = "week";
        public static final String MONTH = "month";
        public static final String QUARTER = "quarter";
        public static final String YEAR = "year";
    }

    /**
     * Scan Statistics Response Body
     */
    @Getter
    @Setter
    class ScanStatisticsResponse {

        ScanStatistics totalStatistics;
        Map<Integer, ScanStatistics> detailedStatistics;

        long total;
        long per_page;
        long current_page;
        long last_page;
        long from;
        long to;

    }

    /**
     * Judge Statistics Response Body
     */
    @Getter
    @Setter
    class JudgeStatisticsResponse {
        JudgeStatistics totalStatistics;
        Map<Integer, JudgeStatistics> detailedStatistics;

        long total;
        long per_page;
        long current_page;
        long last_page;
        long from;
        long to;

    }

    /**
     * HandExamination Statistics Response Body
     */
    @Getter
    @Setter
    class HandExaminationStatisticsResponse {
        HandExaminationStatistics totalStatistics;
        Map<Integer, HandExaminationStatistics> detailedStatistics;

        long total;
        long per_page;
        long current_page;
        long last_page;
        long from;
        long to;

    }

    /**
     * Total Statistics Response Body
     */
    @Getter
    @Setter
    class TotalStatistics {

        ScanStatistics scanStatistics;
        JudgeStatistics judgeStatistics;
        HandExaminationStatistics handExaminationStatistics;

    }

    /**
     * Total Statistics Response Body
     */
    @Getter
    @Setter
    class TotalStatisticsResponse {
        TotalStatistics totalStatistics;
        Map<Integer, TotalStatistics> detailedStatistics;

        long total;
        long per_page;
        long current_page;
        long last_page;
        long from;
        long to;
    }

    /**
     * Statistics Response Body
     */
    @Getter
    @Setter
    @ToString
    class ScanStatistics {

        String axisLabel;
        long totalScan;
        long invalidScan;
        double invalidScanRate;
        long validScan;
        double validScanRate;
        long passedScan;
        double passedScanRate;
        long alarmScan;
        double alarmScanRate;

    }

    /**
     * Judge Statistics Body
     */
    @Getter
    @Setter
    @ToString
    class JudgeStatistics {

        String axisLabel;
        long totalJudge;
        long noSuspictionJudge;
        double noSuspictionJudgeRate;
        long suspictionJudge;
        double suspictionJudgeRate;

    }

    /**
     * Hand Examination Response Body
     */
    @Getter
    @Setter
    @ToString
    class HandExaminationStatistics {

        String axisLabel;
        long totalHandExamination;
        long seizureHandExamination;
        double seizureHandExaminationRate;
        long noSeizureHandExamination;
        double noSeizureHandExaminationRate;

        long checkDuration;

    }

    /**
     * Process Task datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class TaskGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String taskNumber;
            Long mode;
            String status;
            Long fieldId;
            String userName;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;
        TaskGetByFilterAndPageRequestBody.Filter filter;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class TaskGetByIdRequestBody {

        @NotNull
        @Min(1)
        Long taskId;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class HistoryTaskGetByIdRequestBody {

        @NotNull
        @Min(1)
        Long historyId;

    }

    /**
     * History Task datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class HistoryGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String taskNumber;
            Long mode;
            String status;
            Long fieldId;
            String userName;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;
        TaskManagementController.HistoryGetByFilterAndPageRequestBody.Filter filter;

    }

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
            String userCategory;
            String userName;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime;
            String statWidth;

        }

        int currentPage;

        int perPage;

        StatisticsRequestBody.Filter filter;

    }

    /**
     * Task datatable data.
     */
    @RequestMapping(value = "/process-task/get-one", method = RequestMethod.POST)
    public Object processTaskGetById(
            @RequestBody @Valid TaskManagementController.TaskGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        QSerTask builder = QSerTask.serTask;

        Long id = requestBody.getTaskId();

        Optional<SerTask> optionalTask = serTaskRespository.findOne(QSerTask.serTask.taskId.eq(id));

        if (!optionalTask.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!optionalTask.get().getSerScan().getScanInvalid().equals(SerScan.Invalid.TRUE)) {
            return new CommonResponseBody(ResponseMessage.INVALID_SCANID);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, optionalTask.get()));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskId", "taskNumber", "taskStatus", "field", "serScan", "serJudgeGraph", "serHandExamination"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"))
                .addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.filterOutAllExcept("scanImage", "scanDevice", "scanAtrResult", "scanFootAlarm", "scanAssignTimeout", "scanPointsman", "scanStartTime", "scanEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_JUDGE_GRAPH, SimpleBeanPropertyFilter.filterOutAllExcept("judgeDevice", "judgeResult", "judgeTimeout", "judgeUser", "judgeStartTime", "judgeEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_HAND_EXAMINATION, SimpleBeanPropertyFilter.filterOutAllExcept("handDevice", "handUser", "handStartTime", "handEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_IMAGE, SimpleBeanPropertyFilter.filterOutAllExcept("imageUrl", "imageLabel"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW, SimpleBeanPropertyFilter.filterOutAllExcept("workMode"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName"));


        value.setFilters(filters);

        return value;

    }

    /**
     * Task datatable data.
     */
    @RequestMapping(value = "/process-task/get-by-filter-and-page", method = RequestMethod.POST)
    public Object processTaskGetByFilterAndPage(
            @RequestBody @Valid TaskManagementController.TaskGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        QSerTask builder = QSerTask.serTask;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        TaskGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {

            predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.TRUE));

            if (filter.getTaskNumber() != null && !filter.getTaskNumber().isEmpty()) {
                predicate.and(builder.taskNumber.contains(filter.getTaskNumber()));
            }
            if (filter.getMode() != null) {
                predicate.and(builder.serScan.workFlow.workMode.modeId.eq(filter.getMode()));
            }
            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicate.and(builder.taskStatus.eq(filter.getStatus()));
            }
            if (filter.getFieldId() != null) {
                predicate.and(builder.fieldId.eq(filter.getFieldId()));
            }
            if (filter.getUserName() != null && filter.getUserName().isEmpty()) {

                Predicate scanUserName = builder.serScan.scanPointsman.userName.contains(filter.getUserName())
                        .or(builder.serJudgeGraph.judgeUser.userName.contains(filter.getUserName()))
                        .or(builder.serJudgeGraph.judgeUser.userName.contains(filter.getUserName()));
                predicate.and(scanUserName);
            }
            if (filter.getStartTime() != null) {
                predicate.and(builder.createdTime.after(filter.getStartTime()));
            }
            if (filter.getEndTime() != null) {
                predicate.and(builder.createdTime.before(filter.getEndTime()));
            }

        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serTaskRespository.count(predicate);
        List<SerTask> data = serTaskRespository.findAll(predicate, pageRequest).getContent();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build()));

        // Set filters.

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskId", "taskNumber", "taskStatus", "field", "serScan", "serJudgeGraph", "serHandExamination"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"))
                .addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.filterOutAllExcept("scanImage", "scanDevice", "scanPointsman", "scanStartTime", "scanEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_JUDGE_GRAPH, SimpleBeanPropertyFilter.filterOutAllExcept("judgeDevice", "judgeUser", "judgeStartTime", "judgeEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_HAND_EXAMINATION, SimpleBeanPropertyFilter.filterOutAllExcept("handDevice", "handUser", "handStartTime", "handEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_IMAGE, SimpleBeanPropertyFilter.filterOutAllExcept("imageUrl", "imageLabel"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW, SimpleBeanPropertyFilter.filterOutAllExcept("workMode"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName"));
        value.setFilters(filters);

        return value;
    }

    /**
     * History Task datatable data.
     */
    @RequestMapping(value = "/history-task/get-one", method = RequestMethod.POST)
    public Object historyTaskGetById(
            @RequestBody @Valid TaskManagementController.HistoryTaskGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        QHistory builder = QHistory.history;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        Long id = requestBody.getHistoryId();

        Optional<History> optionalHistory = historyRespository.findOne(QHistory.history.historyId.eq(id));

        if (!optionalHistory.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, optionalHistory.get()));

        // Set filters.
        SimpleFilterProvider filters = ModelJsonFilters
                .getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent"));


        value.setFilters(filters);

        return value;

    }

    /**
     * History datatable data.
     */
    @RequestMapping(value = "/history-task/get-by-filter-and-page", method = RequestMethod.POST)
    public Object historyTaskGetByFilterAndPage(
            @RequestBody @Valid TaskManagementController.HistoryGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        QHistory builder = QHistory.history;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        TaskManagementController.HistoryGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (filter.getTaskNumber() != null && !filter.getTaskNumber().isEmpty()) {
                predicate.and(builder.task.taskNumber.contains(filter.getTaskNumber()));
            }
            if (filter.getMode() != null) {
                predicate.and(builder.mode.eq(filter.getMode()));
            }
            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicate.and(builder.task.taskStatus.eq(filter.getStatus()));
            }
            if (filter.getFieldId() != null) {
                predicate.and(builder.task.fieldId.eq(filter.getFieldId()));
            }
            if (filter.getUserName() != null && filter.getUserName().isEmpty()) {

                Predicate scanUserName = builder.scanPointsman.userName.contains(filter.getUserName());
                builder.scanPointsman.userName.contains(filter.getUserName()).or(
                        builder.judgeUser.userName.contains(filter.getUserName())
                ).or(builder.handUser.userName.contains(filter.getUserName()));
                predicate.and(scanUserName);
            }
            if (filter.getStartTime() != null) {
                predicate.and(builder.createdTime.after(filter.getStartTime()));
            }
            if (filter.getEndTime() != null) {
                predicate.and(builder.createdTime.before(filter.getEndTime()));
            }

        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = historyRespository.count(predicate);
        List<History> data = historyRespository.findAll(predicate, pageRequest).getContent();


        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build()));

        // Set filters.

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent"));
        value.setFilters(filters);

        return value;
    }

    /**
     * Task datatable data.
     */
    @RequestMapping(value = "/invalid-task/get-one", method = RequestMethod.POST)
    public Object invalidTaskGetById(
            @RequestBody @Valid TaskManagementController.TaskGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        QSerTask builder = QSerTask.serTask;

        Long id = requestBody.getTaskId();

        Optional<SerTask> optionalTask = serTaskRespository.findOne(QSerTask.serTask.taskId.eq(id));

        if (!optionalTask.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!optionalTask.get().getSerScan().getScanInvalid().equals(SerScan.Invalid.FALSE)) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, optionalTask.get()));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskId", "taskNumber", "taskStatus", "field", "serScan"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"))
                .addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.filterOutAllExcept("scanImage", "scanDevice", "workflow", "scanPointsman", "scanStartTime", "scanEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_IMAGE, SimpleBeanPropertyFilter.filterOutAllExcept("imageUrl", "imageLabel"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW, SimpleBeanPropertyFilter.filterOutAllExcept("workMode"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName"));

        value.setFilters(filters);

        return value;

    }

    /**
     * Task datatable data.
     */
    @RequestMapping(value = "/invalid-task/get-by-filter-and-page", method = RequestMethod.POST)
    public Object invalidTaskGetByFilterAndPage(
            @RequestBody @Valid TaskManagementController.TaskGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        QSerTask builder = QSerTask.serTask;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        TaskGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.FALSE));
        if (filter != null) {

            if (filter.getTaskNumber() != null && !filter.getTaskNumber().isEmpty()) {
                predicate.and(builder.taskNumber.contains(filter.getTaskNumber()));
            }
            if (filter.getMode() != null) {
                predicate.and(builder.serScan.workFlow.workMode.modeId.eq(filter.getMode()));
            }
            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicate.and(builder.taskStatus.eq(filter.getStatus()));
            }
            if (filter.getFieldId() != null) {
                predicate.and(builder.fieldId.eq(filter.getFieldId()));
            }
            if (filter.getUserName() != null && filter.getUserName().isEmpty()) {

                Predicate scanUserName = builder.serScan.scanPointsman.userName.contains(filter.getUserName())
                        .or(builder.serJudgeGraph.judgeUser.userName.contains(filter.getUserName()))
                        .or(builder.serJudgeGraph.judgeUser.userName.contains(filter.getUserName()));
                predicate.and(scanUserName);
            }
            if (filter.getStartTime() != null) {
                predicate.and(builder.createdTime.after(filter.getStartTime()));
            }
            if (filter.getEndTime() != null) {
                predicate.and(builder.createdTime.before(filter.getEndTime()));
            }

        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serTaskRespository.count(predicate);
        List<SerTask> data = serTaskRespository.findAll(predicate, pageRequest).getContent();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build()));

        // Set filters.

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskId", "taskNumber", "taskStatus", "field", "serScan", "serJudgeGraph", "serHandExamination"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"))
                .addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.filterOutAllExcept("scanImage", "scanDevice", "scanPointsman", "scanStartTime", "scanEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_JUDGE_GRAPH, SimpleBeanPropertyFilter.filterOutAllExcept("judgeDevice", "judgeUser", "judgeStartTime", "judgeEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_HAND_EXAMINATION, SimpleBeanPropertyFilter.filterOutAllExcept("handDevice", "handUser", "handStartTime", "handEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW, SimpleBeanPropertyFilter.filterOutAllExcept("workMode"))
                .addFilter(ModelJsonFilters.FILTER_SER_IMAGE, SimpleBeanPropertyFilter.filterOutAllExcept("imageUrl", "imageLabel"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName"));
        value.setFilters(filters);

        return value;
    }

    private ScanStatisticsResponse getScanStatistics(StatisticsRequestBody requestBody) {

        ScanStatistics totalStatistics = new ScanStatistics();

        HashMap<Integer, ScanStatistics> detailedStatistics = new HashMap<Integer, ScanStatistics>();

        totalStatistics = getScanStatisticsByDate(requestBody, null);

        ScanStatisticsResponse response = new ScanStatisticsResponse();

        int keyValueMin = 0, keyValueMax = -1;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    response.setTotal(24);
                    break;
                case StatisticWidth.DAY:
                    keyValueMin = 1;
                    keyValueMax = 31;
                    response.setTotal(31);
                    break;
                case StatisticWidth.WEEK:
                    keyValueMin = 1;
                    keyValueMax = 5;
                    response.setTotal(5);
                    break;
                case StatisticWidth.MONTH:
                    keyValueMin = 1;
                    keyValueMax = 12;
                    response.setTotal(12);
                    break;
                case StatisticWidth.QUARTER:
                    keyValueMin = 1;
                    keyValueMax = 4;
                    response.setTotal(4);
                    break;
                case StatisticWidth.YEAR:
                    Calendar calendar = Calendar.getInstance();
                    if (requestBody.getFilter().getStartTime() != null) {
                        calendar.setTime(requestBody.getFilter().getStartTime());
                        keyValueMin = calendar.get(Calendar.YEAR);
                    } else {
                        keyValueMin = Calendar.getInstance().get(Calendar.YEAR) - 10 + 1;
                    }
                    if (requestBody.getFilter().getEndTime() != null) {
                        calendar.setTime(requestBody.getFilter().getEndTime());
                        keyValueMax = calendar.get(Calendar.YEAR);
                    } else {
                        keyValueMax = Calendar.getInstance().get(Calendar.YEAR);

                    }
                    response.setTotal(keyValueMax - keyValueMin + 1);
                    break;
                default:
                    keyValueMin = 0;
                    keyValueMax = -1;
                    break;
            }
        }

        int curPage = requestBody.getCurrentPage();
        int perPage = requestBody.getPerPage();

        int startIndex = keyValueMin;
        int endIndex = keyValueMax;

        if (curPage != 0 && perPage != 0) {
            startIndex = (curPage - 1) * perPage + 1;
            endIndex = (curPage) * perPage;

            if (requestBody.getFilter().getStatWidth().equals(StatisticWidth.YEAR)) {
                startIndex = keyValueMin + startIndex - 1;
                endIndex = startIndex + perPage - 1;
            }

            if (startIndex < keyValueMin) {
                startIndex = keyValueMin;
            }
            if (endIndex > keyValueMax) {
                endIndex = keyValueMax;
            }


            if (requestBody.getFilter().getStatWidth().equals(StatisticWidth.YEAR)) {
                response.setFrom(startIndex - keyValueMin + 1);
                response.setTo(endIndex - keyValueMin + 1);
            } else {
                response.setFrom(startIndex);
                response.setTo(endIndex);
            }
        }

        int i = 0;
        for (i = startIndex; i <= endIndex; i++) {
            ScanStatistics scanStat = getScanStatisticsByDate(requestBody, i);
            detailedStatistics.put(i, scanStat);
        }


        response.setTotalStatistics(totalStatistics);
        response.setDetailedStatistics(detailedStatistics);

        if (curPage != 0 && perPage != 0) {

            response.setCurrent_page(requestBody.getCurrentPage());
            response.setPer_page(requestBody.getPerPage());
            response.setLast_page(response.getTotal() / response.getPer_page() + 1);
        }
        return response;

    }

    private ScanStatistics getScanStatisticsByDate(StatisticsRequestBody requestBody, Integer keyDate) {

        ScanStatistics scanStatistics = new ScanStatistics();

        QSerScan builder = QSerScan.serScan;

        Date dateFrom = requestBody.getFilter().getStartTime();
        Date dateTo = requestBody.getFilter().getEndTime();

        Predicate predicateDate;
        Predicate predicateField = null;
        Predicate predicateDevice = null;
        Predicate predicateUsername = null;
        Predicate predicateUserCategory = null;
        Predicate predicateStatisticWidth = null;
        Predicate predicateKeyDate = null;


        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty() && keyDate != null) {
            switch (requestBody.getFilter().getStatWidth()) {
                case StatisticWidth.HOUR:
                    predicateKeyDate = builder.scanStartTime.hour().eq(keyDate);
                    break;
                case StatisticWidth.DAY:
                    predicateKeyDate = builder.scanStartTime.dayOfMonth().eq(keyDate);
                    break;
                case StatisticWidth.WEEK:
                    predicateKeyDate = builder.scanStartTime.week().eq(keyDate);
                    break;
                case StatisticWidth.MONTH:
                    predicateKeyDate = builder.scanStartTime.month().eq(keyDate);
                    break;
                case StatisticWidth.QUARTER:
                    predicateKeyDate = builder.scanStartTime.month().eq(keyDate * 3);
                    break;
                case StatisticWidth.YEAR:
                    predicateKeyDate = builder.scanStartTime.year().eq(keyDate);
                    break;
                default:
                    break;
            }
        }

        if (dateFrom == null && dateTo == null) {
            predicateDate = null;
        } else if (dateFrom != null && dateTo == null) {
            predicateDate = builder.scanStartTime.after(dateFrom);
        } else if (dateFrom == null && dateTo != null) {
            predicateDate = builder.scanEndTime.before(dateTo);
        } else {
            predicateDate = builder.scanStartTime.between(dateTo, dateFrom).and(builder.scanEndTime.between(dateTo, dateFrom));
        }


        if (requestBody.getFilter().getFieldId() != null) {

        }


        if (requestBody.getFilter().getDeviceId() != null) {
            predicateDevice = builder.scanDeviceId.eq(requestBody.getFilter().getDeviceId());
        }


        if (requestBody.getFilter().getUserCategory() != null && !requestBody.getFilter().getUserCategory().isEmpty()) {

        }


        if (requestBody.getFilter().getUserName() != null && !requestBody.getFilter().getUserName().isEmpty()) {
            predicateUsername = builder.scanPointsman.userName.contains(requestBody.getFilter().getUserName());
        }

        BooleanBuilder predicateTotal = new BooleanBuilder(builder.isNotNull());
        predicateTotal.and(predicateDate);
        predicateTotal.and(predicateField);
        predicateTotal.and(predicateDevice);
        predicateTotal.and(predicateUsername);
        predicateTotal.and(predicateUserCategory);
        predicateTotal.and(predicateKeyDate);

        BooleanBuilder predicateValid = new BooleanBuilder(builder.isNotNull());
        predicateValid.and(builder.scanInvalid.eq(SerScan.Invalid.TRUE));
        predicateValid.and(predicateDate);
        predicateValid.and(predicateField);
        predicateValid.and(predicateDevice);
        predicateValid.and(predicateUsername);
        predicateValid.and(predicateUserCategory);
        predicateValid.and(predicateKeyDate);


        BooleanBuilder predicateInvalid = new BooleanBuilder(builder.isNotNull());
        predicateInvalid.and(builder.scanInvalid.eq(SerScan.Invalid.FALSE));
        predicateInvalid.and(predicateDate);
        predicateInvalid.and(predicateField);
        predicateInvalid.and(predicateDevice);
        predicateInvalid.and(predicateUsername);
        predicateInvalid.and(predicateUserCategory);
        predicateInvalid.and(predicateKeyDate);


        BooleanBuilder predicatePassed = new BooleanBuilder(builder.isNotNull());
        predicatePassed.and(builder.scanAtrResult.eq(SerScan.ATRResult.TRUE));
        predicatePassed.and(predicateDate);
        predicatePassed.and(predicateField);
        predicatePassed.and(predicateDevice);
        predicatePassed.and(predicateUsername);
        predicatePassed.and(predicateUserCategory);
        predicatePassed.and(predicateKeyDate);

        BooleanBuilder predicateAlarm = new BooleanBuilder(builder.isNotNull());
        predicateAlarm.and(builder.scanAtrResult.eq(SerScan.FootAlarm.TRUE));
        predicateAlarm.and(predicateDate);
        predicateAlarm.and(predicateField);
        predicateAlarm.and(predicateDevice);
        predicateAlarm.and(predicateUsername);
        predicateAlarm.and(predicateUserCategory);
        predicateAlarm.and(predicateKeyDate);

//        JPAQuery query = new JPAQuery(entityManager);
//
//        List<Integer, List<SerScan>> transform = List<Integer, List<SerScan>>) query
//                .from(builder)
//                .transform(
//                        GroupBy.groupBy(builder.scanStartTime.month()).as(GroupBy.list(builder))
//                );

        //scanStatistics.setDetailedScan(transform);
        try {

            long totalScan = serScanRepository.count(predicateTotal);
            long validScan = serScanRepository.count(predicateValid);
            long invalidScan = serScanRepository.count(predicateInvalid);
            long passedScan = serScanRepository.count(predicatePassed);
            long alarmScan = serScanRepository.count(predicateAlarm);

            scanStatistics.setTotalScan(totalScan);
            scanStatistics.setValidScan(validScan);
            scanStatistics.setInvalidScan(invalidScan);
            scanStatistics.setPassedScan(passedScan);
            scanStatistics.setPassedScan(alarmScan);

            scanStatistics.setValidScanRate(validScan / (double) totalScan);
            scanStatistics.setInvalidScanRate(invalidScan / (double) totalScan);
            scanStatistics.setPassedScanRate(passedScan / (double) totalScan);
            scanStatistics.setAlarmScanRate(alarmScan / (double) totalScan);

        } catch (Exception e) {

            scanStatistics.setValidScanRate(0);
            scanStatistics.setInvalidScanRate(0);
            scanStatistics.setPassedScanRate(0);
            scanStatistics.setAlarmScanRate(0);

        }

        return scanStatistics;

    }

    private JudgeStatisticsResponse getJudgeStatistics(StatisticsRequestBody requestBody) {

        JudgeStatistics totalStatistics = new JudgeStatistics();

        HashMap<Integer, JudgeStatistics> detailedStatistics = new HashMap<Integer, JudgeStatistics>();

        totalStatistics = getJudgeStatisticsByDate(requestBody, null);

        JudgeStatisticsResponse response = new JudgeStatisticsResponse();

        int keyValueMin = 0, keyValueMax = -1;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    response.setTotal(24);
                    break;
                case StatisticWidth.DAY:
                    keyValueMin = 1;
                    keyValueMax = 31;
                    response.setTotal(31);
                    break;
                case StatisticWidth.WEEK:
                    keyValueMin = 1;
                    keyValueMax = 5;
                    response.setTotal(5);
                    break;
                case StatisticWidth.MONTH:
                    keyValueMin = 1;
                    keyValueMax = 12;
                    response.setTotal(12);
                    break;
                case StatisticWidth.QUARTER:
                    keyValueMin = 1;
                    keyValueMax = 4;
                    response.setTotal(4);
                    break;
                case StatisticWidth.YEAR:
                    Calendar calendar = Calendar.getInstance();
                    if (requestBody.getFilter().getStartTime() != null) {
                        calendar.setTime(requestBody.getFilter().getStartTime());
                        keyValueMin = calendar.get(Calendar.YEAR);
                    } else {
                        keyValueMin = Calendar.getInstance().get(Calendar.YEAR) - 10 + 1;
                    }
                    if (requestBody.getFilter().getEndTime() != null) {
                        calendar.setTime(requestBody.getFilter().getEndTime());
                        keyValueMax = calendar.get(Calendar.YEAR);
                    } else {
                        keyValueMax = Calendar.getInstance().get(Calendar.YEAR);

                    }
                    response.setTotal(keyValueMax - keyValueMin + 1);

                    break;
                default:
                    keyValueMin = 0;
                    keyValueMax = -1;
                    break;
            }
        }

        int curPage = requestBody.getCurrentPage();
        int perPage = requestBody.getPerPage();

        int startIndex = keyValueMin;
        int endIndex = keyValueMax;

        if (curPage != 0 && perPage != 0) {

            startIndex = (curPage - 1) * perPage + 1;
            endIndex = (curPage) * perPage;

            if (requestBody.getFilter().getStatWidth().equals(StatisticWidth.YEAR)) {
                startIndex = keyValueMin + startIndex - 1;
                endIndex = startIndex + perPage - 1;
            }

            if (startIndex < keyValueMin) {
                startIndex = keyValueMin;
            }
            if (endIndex > keyValueMax) {
                endIndex = keyValueMax;
            }

            if (requestBody.getFilter().getStatWidth().equals(StatisticWidth.YEAR)) {
                response.setFrom(startIndex - keyValueMin + 1);
                response.setTo(endIndex - keyValueMin + 1);
            } else {
                response.setFrom(startIndex);
                response.setTo(endIndex);
            }
        }

        int i = 0;
        for (i = startIndex; i <= endIndex; i++) {
            JudgeStatistics scanStat = getJudgeStatisticsByDate(requestBody, i);
            detailedStatistics.put(i, scanStat);
        }

        response.setTotalStatistics(totalStatistics);
        response.setDetailedStatistics(detailedStatistics);

        if (curPage != 0 && perPage != 0) {

            response.setCurrent_page(requestBody.getCurrentPage());
            response.setPer_page(requestBody.getPerPage());
            response.setLast_page(response.getTotal() / response.getPer_page() + 1);

        }

        return response;

    }

    private JudgeStatistics getJudgeStatisticsByDate(StatisticsRequestBody requestBody, Integer keyDate) {

        JudgeStatistics judgeStatistics = new JudgeStatistics();

        QSerJudgeGraph builder = QSerJudgeGraph.serJudgeGraph;

        Date dateFrom = requestBody.getFilter().getStartTime();
        Date dateTo = requestBody.getFilter().getEndTime();

        Predicate predicateDate;
        Predicate predicateField = null;
        Predicate predicateDevice = null;
        Predicate predicateUsername = null;
        Predicate predicateUserCategory = null;
        Predicate predicateStatisticWidth = null;
        Predicate predicateKeyDate = null;


        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty() && keyDate != null) {
            switch (requestBody.getFilter().getStatWidth()) {
                case StatisticWidth.HOUR:
                    predicateKeyDate = builder.judgeStartTime.hour().eq(keyDate);
                    break;
                case StatisticWidth.DAY:
                    predicateKeyDate = builder.judgeStartTime.dayOfMonth().eq(keyDate);
                    break;
                case StatisticWidth.WEEK:
                    predicateKeyDate = builder.judgeStartTime.week().eq(keyDate);
                    break;
                case StatisticWidth.MONTH:
                    predicateKeyDate = builder.judgeStartTime.month().eq(keyDate);
                    break;
                case StatisticWidth.QUARTER:
                    predicateKeyDate = builder.judgeStartTime.month().eq(keyDate * 3);
                    break;
                case StatisticWidth.YEAR:
                    predicateKeyDate = builder.judgeStartTime.year().eq(keyDate);
                    break;
                default:
                    break;
            }
        }

        if (dateFrom == null && dateTo == null) {
            predicateDate = null;
        } else if (dateFrom != null && dateTo == null) {
            predicateDate = builder.judgeStartTime.after(dateFrom);
        } else if (dateFrom == null && dateTo != null) {
            predicateDate = builder.judgeEndTime.before(dateTo);
        } else {
            predicateDate = builder.judgeStartTime.between(dateTo, dateFrom).and(builder.judgeEndTime.between(dateTo, dateFrom));
        }


        if (requestBody.getFilter().getFieldId() != null) {

        }


        if (requestBody.getFilter().getDeviceId() != null) {
            predicateDevice = builder.judgeDeviceId.eq(requestBody.getFilter().getDeviceId());
        }


        if (requestBody.getFilter().getUserCategory() != null && !requestBody.getFilter().getUserCategory().isEmpty()) {

        }


        if (requestBody.getFilter().getUserName() != null && !requestBody.getFilter().getUserName().isEmpty()) {
            predicateUsername = builder.judgeUser.userName.contains(requestBody.getFilter().getUserName());
        }


        BooleanBuilder predicateTotal = new BooleanBuilder(builder.isNotNull());
        predicateTotal.and(predicateDate);
        predicateTotal.and(predicateField);
        predicateTotal.and(predicateDevice);
        predicateTotal.and(predicateUsername);
        predicateTotal.and(predicateUserCategory);
        predicateTotal.and(predicateKeyDate);

        BooleanBuilder predicateNoSuspiction = new BooleanBuilder(builder.isNotNull());
        predicateNoSuspiction.and(builder.judgeResult.eq(SerJudgeGraph.Result.TRUE));
        predicateNoSuspiction.and(predicateDate);
        predicateNoSuspiction.and(predicateField);
        predicateNoSuspiction.and(predicateDevice);
        predicateNoSuspiction.and(predicateUsername);
        predicateNoSuspiction.and(predicateUserCategory);
        predicateNoSuspiction.and(predicateKeyDate);

        BooleanBuilder predicateSuspiction = new BooleanBuilder(builder.isNotNull());
        predicateSuspiction.and(builder.judgeResult.eq(SerJudgeGraph.Result.FALSE));
        predicateSuspiction.and(predicateDate);
        predicateSuspiction.and(predicateField);
        predicateSuspiction.and(predicateDevice);
        predicateSuspiction.and(predicateUsername);
        predicateSuspiction.and(predicateUserCategory);
        predicateSuspiction.and(predicateKeyDate);

        long totalJudge = serJudgeGraphRepository.count(predicateTotal);
        long noSuspictionJudge = serJudgeGraphRepository.count(predicateNoSuspiction);
        long suspictionJudge = serJudgeGraphRepository.count(predicateSuspiction);

        try {
            judgeStatistics.setTotalJudge(totalJudge);
            judgeStatistics.setNoSuspictionJudge(noSuspictionJudge);
            judgeStatistics.setSuspictionJudge(suspictionJudge);
            judgeStatistics.setNoSuspictionJudgeRate(noSuspictionJudge / (double) totalJudge);
            judgeStatistics.setNoSuspictionJudgeRate(suspictionJudge / (double) totalJudge);

        } catch (Exception e) {
            judgeStatistics.setNoSuspictionJudgeRate(0);
            judgeStatistics.setNoSuspictionJudgeRate(0);
        }

        return judgeStatistics;

    }


    private HandExaminationStatisticsResponse getHandExaminationStatistics(StatisticsRequestBody requestBody) {

        HandExaminationStatistics totalStatistics = new HandExaminationStatistics();

        HashMap<Integer, HandExaminationStatistics> detailedStatistics = new HashMap<Integer, HandExaminationStatistics>();

        totalStatistics = getHandExaminationStatisticsByDate(requestBody, null);

        HandExaminationStatisticsResponse response = new HandExaminationStatisticsResponse();


        int keyValueMin = 0, keyValueMax = -1;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case StatisticWidth.HOUR:

                    keyValueMin = 0;
                    keyValueMax = 23;
                    response.setTotal(24);
                    break;

                case StatisticWidth.DAY:

                    keyValueMin = 1;
                    keyValueMax = 31;
                    response.setTotal(31);
                    break;

                case StatisticWidth.WEEK:

                    keyValueMin = 1;
                    keyValueMax = 5;
                    response.setTotal(5);
                    break;

                case StatisticWidth.MONTH:

                    keyValueMin = 1;
                    keyValueMax = 12;
                    response.setTotal(12);

                    break;

                case StatisticWidth.QUARTER:

                    keyValueMin = 1;
                    keyValueMax = 4;
                    response.setTotal(4);
                    break;

                case StatisticWidth.YEAR:

                    Calendar calendar = Calendar.getInstance();
                    if (requestBody.getFilter().getStartTime() != null) {
                        calendar.setTime(requestBody.getFilter().getStartTime());
                        keyValueMin = calendar.get(Calendar.YEAR);
                    } else {
                        keyValueMin = Calendar.getInstance().get(Calendar.YEAR) - 10 + 1;
                    }
                    if (requestBody.getFilter().getEndTime() != null) {
                        calendar.setTime(requestBody.getFilter().getEndTime());
                        keyValueMax = calendar.get(Calendar.YEAR);
                    } else {
                        keyValueMax = Calendar.getInstance().get(Calendar.YEAR);

                    }
                    response.setTotal(keyValueMax - keyValueMin + 1);
                    break;

                default:
                    keyValueMin = 0;
                    keyValueMax = -1;
                    break;

            }
        }


        int curPage = requestBody.getCurrentPage();
        int perPage = requestBody.getPerPage();

        int startIndex = keyValueMin;
        int endIndex = keyValueMax;

        if (curPage != 0 && perPage != 0) {

            startIndex = (curPage - 1) * perPage + 1;
            endIndex = (curPage) * perPage;

            if (requestBody.getFilter().getStatWidth().equals(StatisticWidth.YEAR)) {
                startIndex = keyValueMin + startIndex - 1;
                endIndex = startIndex + perPage - 1;
            }

            if (startIndex < keyValueMin) {
                startIndex = keyValueMin;
            }
            if (endIndex > keyValueMax) {
                endIndex = keyValueMax;
            }

            if (requestBody.getFilter().getStatWidth().equals(StatisticWidth.YEAR)) {
                response.setFrom(startIndex - keyValueMin + 1);
                response.setTo(endIndex - keyValueMin + 1);
            } else {
                response.setFrom(startIndex);
                response.setTo(endIndex);
            }
        }

        int i = 0;
        for (i = startIndex; i <= endIndex; i++) {
            HandExaminationStatistics scanStat = getHandExaminationStatisticsByDate(requestBody, i);
            detailedStatistics.put(i, scanStat);
        }

        response.setTotalStatistics(totalStatistics);
        response.setDetailedStatistics(detailedStatistics);

        if (curPage != 0 && perPage != 0) {

            response.setCurrent_page(requestBody.getCurrentPage());
            response.setPer_page(requestBody.getPerPage());
            response.setLast_page(response.getTotal() / response.getPer_page() + 1);

        }


        return response;

    }

    private HandExaminationStatistics getHandExaminationStatisticsByDate(StatisticsRequestBody requestBody, Integer keyDate) {

        HandExaminationStatistics handExaminationStatistics = new HandExaminationStatistics();

        QSerHandExamination builder = QSerHandExamination.serHandExamination;

        Date dateFrom = requestBody.getFilter().getStartTime();
        Date dateTo = requestBody.getFilter().getEndTime();

        Predicate predicateDate;
        Predicate predicateField = null;
        Predicate predicateDevice = null;
        Predicate predicateUsername = null;
        Predicate predicateUserCategory = null;
        Predicate predicateKeyDate = null;


        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty() && keyDate != null) {
            switch (requestBody.getFilter().getStatWidth()) {
                case StatisticWidth.HOUR:
                    predicateKeyDate = builder.handStartTime.hour().eq(keyDate);
                    break;
                case StatisticWidth.DAY:
                    predicateKeyDate = builder.handStartTime.dayOfMonth().eq(keyDate);
                    break;
                case StatisticWidth.WEEK:
                    predicateKeyDate = builder.handStartTime.week().eq(keyDate);
                    break;
                case StatisticWidth.MONTH:
                    predicateKeyDate = builder.handStartTime.month().eq(keyDate);
                    break;
                case StatisticWidth.QUARTER:
                    predicateKeyDate = builder.handStartTime.month().eq(keyDate * 3);
                    break;
                case StatisticWidth.YEAR:
                    predicateKeyDate = builder.handStartTime.year().eq(keyDate);
                    break;
                default:
                    break;
            }
        }

        if (dateFrom == null && dateTo == null) {
            predicateDate = null;
        } else if (dateFrom != null && dateTo == null) {
            predicateDate = builder.handStartTime.after(dateFrom);
        } else if (dateFrom == null && dateTo != null) {
            predicateDate = builder.handEndTime.before(dateTo);
        } else {
            predicateDate = builder.handStartTime.between(dateTo, dateFrom).and(builder.handEndTime.between(dateTo, dateFrom));
        }


        if (requestBody.getFilter().getFieldId() != null) {

        }


        if (requestBody.getFilter().getDeviceId() != null) {
            predicateDevice = builder.handDeviceId.eq(requestBody.getFilter().getDeviceId());
        }


        if (requestBody.getFilter().getUserCategory() != null && !requestBody.getFilter().getUserCategory().isEmpty()) {

        }


        if (requestBody.getFilter().getUserName() != null && !requestBody.getFilter().getUserName().isEmpty()) {
            predicateUsername = builder.handUser.userName.contains(requestBody.getFilter().getUserName());
        }

        BooleanBuilder predicateTotal = new BooleanBuilder(builder.isNotNull());
        predicateTotal.and(predicateDate);
        predicateTotal.and(predicateField);
        predicateTotal.and(predicateDevice);
        predicateTotal.and(predicateUsername);
        predicateTotal.and(predicateUserCategory);
        predicateTotal.and(predicateKeyDate);

        BooleanBuilder predicteSeizure = new BooleanBuilder(builder.isNotNull());
        predicteSeizure.and(builder.handResult.eq(SerJudgeGraph.Result.TRUE));
        predicteSeizure.and(predicateDate);
        predicteSeizure.and(predicateField);
        predicteSeizure.and(predicateDevice);
        predicteSeizure.and(predicateUsername);
        predicteSeizure.and(predicateUserCategory);
        predicteSeizure.and(predicateKeyDate);

        BooleanBuilder predicteNoSeizure = new BooleanBuilder(builder.isNotNull());
        predicteNoSeizure.and(builder.handResult.eq(SerJudgeGraph.Result.FALSE));
        predicteNoSeizure.and(predicateDate);
        predicteNoSeizure.and(predicateField);
        predicteNoSeizure.and(predicateDevice);
        predicteNoSeizure.and(predicateUsername);
        predicteNoSeizure.and(predicateUserCategory);
        predicteNoSeizure.and(predicateKeyDate);

        long totalHandExam = serHandExaminationRepository.count(predicateTotal);
        long seizureHandExam = serHandExaminationRepository.count(predicteSeizure);
        long noSeizureHandExam = serHandExaminationRepository.count(predicteNoSeizure);

        List<SerHandExamination> listHandExamination = serHandExaminationRepository.findAll();


        try {

            handExaminationStatistics.setTotalHandExamination(totalHandExam);
            handExaminationStatistics.setSeizureHandExamination(seizureHandExam);
            handExaminationStatistics.setNoSeizureHandExamination(noSeizureHandExam);
            handExaminationStatistics.setSeizureHandExaminationRate(seizureHandExam / (double) totalHandExam);
            handExaminationStatistics.setNoSeizureHandExaminationRate(noSeizureHandExam / (double) totalHandExam);

        } catch (Exception e) {

            handExaminationStatistics.setSeizureHandExaminationRate(0.0);
            handExaminationStatistics.setNoSeizureHandExaminationRate(0.0);

        }

        return handExaminationStatistics;

    }

    private TotalStatisticsResponse getPreviewStatistics(StatisticsRequestBody requestBody) {

        TotalStatistics totalStatistics = new TotalStatistics();

        HashMap<Integer, TotalStatistics> detailedStatistics = new HashMap<Integer, TotalStatistics>();

        totalStatistics = getPreviewStatisticsByDate(requestBody, null);

        TotalStatisticsResponse response = new TotalStatisticsResponse();

        int keyValueMin = 0, keyValueMax = 0;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    response.setTotal(24);
                    break;
                case StatisticWidth.DAY:
                    keyValueMin = 1;
                    keyValueMax = 31;
                    response.setTotal(31);
                    break;
                case StatisticWidth.WEEK:
                    keyValueMin = 1;
                    keyValueMax = 5;
                    response.setTotal(5);
                    break;
                case StatisticWidth.MONTH:
                    keyValueMin = 1;
                    keyValueMax = 12;
                    response.setTotal(12);
                    break;
                case StatisticWidth.QUARTER:
                    keyValueMin = 1;
                    keyValueMax = 4;
                    response.setTotal(4);
                    break;
                case StatisticWidth.YEAR:
                    Calendar calendar = Calendar.getInstance();
                    if (requestBody.getFilter().getStartTime() != null) {
                        calendar.setTime(requestBody.getFilter().getStartTime());
                        keyValueMin = calendar.get(Calendar.YEAR);
                    } else {
                        keyValueMin = Calendar.getInstance().get(Calendar.YEAR) - 10 + 1;
                    }
                    if (requestBody.getFilter().getEndTime() != null) {
                        calendar.setTime(requestBody.getFilter().getEndTime());
                        keyValueMax = calendar.get(Calendar.YEAR);
                    } else {
                        keyValueMax = Calendar.getInstance().get(Calendar.YEAR);

                    }
                    response.setTotal(keyValueMax - keyValueMin + 1);
                    break;

                default:
                    keyValueMin = 0;
                    keyValueMax = -1;
                    break;
            }
        }

        int curPage = requestBody.getCurrentPage();
        int perPage = requestBody.getPerPage();

        int startIndex = keyValueMin;
        int endIndex = keyValueMax;

        if (curPage != 0 && perPage != 0) {

            startIndex = (curPage - 1) * perPage + 1;
            endIndex = (curPage) * perPage;

            if (requestBody.getFilter().getStatWidth().equals(StatisticWidth.YEAR)) {
                startIndex = keyValueMin + startIndex - 1;
                endIndex = startIndex + perPage - 1;
            }

            if (startIndex < keyValueMin) {
                startIndex = keyValueMin;
            }
            if (endIndex > keyValueMax) {
                endIndex = keyValueMax;
            }

            if (requestBody.getFilter().getStatWidth().equals(StatisticWidth.YEAR)) {
                response.setFrom(startIndex - keyValueMin + 1);
                response.setTo(endIndex - keyValueMin + 1);
            } else {
                response.setFrom(startIndex);
                response.setTo(endIndex);
            }
        }

        int i = 0;
        for (i = startIndex; i <= endIndex; i++) {
            TotalStatistics scanStat = getPreviewStatisticsByDate(requestBody, i);
            detailedStatistics.put(i, scanStat);
        }

        response.setTotalStatistics(totalStatistics);
        response.setDetailedStatistics(detailedStatistics);

        if (curPage != 0 && perPage != 0) {

            response.setCurrent_page(requestBody.getCurrentPage());
            response.setPer_page(requestBody.getPerPage());
            response.setLast_page(response.getTotal() / response.getPer_page() + 1);
            
        }

        return response;

    }


    private TotalStatistics getPreviewStatisticsByDate(StatisticsRequestBody requestBody, Integer keyDate) {

        ScanStatistics scanStatistics = getScanStatisticsByDate(requestBody, keyDate);
        JudgeStatistics judgeStatistics = getJudgeStatisticsByDate(requestBody, keyDate);
        HandExaminationStatistics handExaminationStatistics = getHandExaminationStatisticsByDate(requestBody, keyDate);

        TotalStatistics totalStatistics = new TotalStatistics();
        totalStatistics.setScanStatistics(scanStatistics);
        totalStatistics.setJudgeStatistics(judgeStatistics);
        totalStatistics.setHandExaminationStatistics(handExaminationStatistics);

        return totalStatistics;
    }


    @RequestMapping(value = "/statistics/handexamination", method = RequestMethod.POST)
    public Object handExaminationStatisticsGet(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Date dateFrom, dateTo;
        dateFrom = requestBody.getFilter().getStartTime();
        dateTo = requestBody.getFilter().getEndTime();


        //get Scan statistics
        HandExaminationStatisticsResponse handStatistics = getHandExaminationStatistics(requestBody);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, handStatistics));

        return value;

    }

    @RequestMapping(value = "/statistics/preview", method = RequestMethod.POST)
    public Object previewStatisticsGet(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Date dateFrom, dateTo;
        dateFrom = requestBody.getFilter().getStartTime();
        dateTo = requestBody.getFilter().getEndTime();

        TotalStatisticsResponse response = getPreviewStatistics(requestBody);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, response));

        return value;

    }

    @RequestMapping(value = "/statistics/scan", method = RequestMethod.POST)
    public Object scanStatisticsGet(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get Scan statistics
        ScanStatisticsResponse scanStatistics = getScanStatistics(requestBody);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, scanStatistics));

        return value;

    }

    @RequestMapping(value = "/statistics/judgegraph", method = RequestMethod.POST)
    public Object judgegraphStatisticsGet(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Date dateFrom, dateTo;
        dateFrom = requestBody.getFilter().getStartTime();
        dateTo = requestBody.getFilter().getEndTime();


        //get Scan statistics
        JudgeStatisticsResponse judgeStatistics = getJudgeStatistics(requestBody);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, judgeStatistics));

        return value;

    }

}
