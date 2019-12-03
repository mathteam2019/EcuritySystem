package com.nuctech.ecuritycheckitem.controllers.taskmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.HandExaminationResponseModel;
import com.nuctech.ecuritycheckitem.models.response.JudgeStatisticsResponseModel;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.repositories.SerScanRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Predicate;

import lombok.*;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task")
public class TaskManagementController extends BaseController {


    /**
     * Table type
     */
    @AllArgsConstructor
    @Getter
    public enum TableType {

        SER_SCAN(1),
        SER_JUDGE_GRAPH(2),
        SER_HAND_EXAMINATION(3);

        private final Integer value;
    }

    @Getter
    @Setter
    public class StatisticsByUserResponse {

        long totalSeconds;
        long scanSeconds;
        long judgeSeconds;
        long handSeconds;

    }

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

        long id;
        long time;

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
        Map<Long, TotalStatistics> detailedStatistics;

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
        long id;
        long time;
        long workingSeconds;
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
    public class JudgeStatistics {

        String axisLabel;
        long id;
        long time;
        long workingSeconds;
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
        long id;
        long time;
        long workingSeconds;
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
            Long userCategory;
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
            String statWidth;

        }

        StatisticsByDeviceRequestBody.Filter filter;

    }


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
            String statWidth;

        }

        StatisticsByUserRequestBody.Filter filter;

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
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {

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
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {

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
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {

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

            scanStat.setId(i - startIndex + 1);

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
            predicateDate = builder.scanStartTime.between(dateFrom, dateTo).and(builder.scanEndTime.between(dateFrom, dateTo));
        }


        if (requestBody.getFilter().getFieldId() != null) {

        }


        if (requestBody.getFilter().getDeviceId() != null) {
            predicateDevice = builder.scanDeviceId.eq(requestBody.getFilter().getDeviceId());
        }


        if (requestBody.getFilter().getUserCategory() != null) {

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

        try {

            long totalScan = serScanRepository.count(predicateTotal);
            long validScan = serScanRepository.count(predicateValid);
            long invalidScan = serScanRepository.count(predicateInvalid);
            long passedScan = serScanRepository.count(predicatePassed);
            long alarmScan = serScanRepository.count(predicateAlarm);

            Iterable<SerScan> listScans = serScanRepository.findAll(predicateTotal);

            long workingSecs = 0;

            for (SerScan item : listScans) {

                workingSecs += (item.getScanEndTime().getTime() - item.getScanStartTime().getTime()) / 1000;

            }

            scanStatistics.setWorkingSeconds(workingSecs);
            scanStatistics.setId(keyDate);
            scanStatistics.setTime(keyDate);
            scanStatistics.setTotalScan(totalScan);
            scanStatistics.setValidScan(validScan);
            scanStatistics.setInvalidScan(invalidScan);
            scanStatistics.setPassedScan(passedScan);
            scanStatistics.setAlarmScan(alarmScan);

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
            JudgeStatistics judgeStat = getJudgeStatisticsByDate(requestBody, i);

            judgeStat.setId(i - startIndex + 1);

            detailedStatistics.put(i, judgeStat);
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
            predicateDate = builder.judgeStartTime.between(dateFrom, dateTo).and(builder.judgeEndTime.between(dateFrom, dateTo));
        }


        if (requestBody.getFilter().getFieldId() != null) {

        }


        if (requestBody.getFilter().getDeviceId() != null) {
            predicateDevice = builder.judgeDeviceId.eq(requestBody.getFilter().getDeviceId());
        }


        if (requestBody.getFilter().getUserCategory() != null) {

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

            Iterable<SerJudgeGraph> listScans = serJudgeGraphRepository.findAll(predicateTotal);

            long workingSecs = 0;

            for (SerJudgeGraph item : listScans) {

                workingSecs += (item.getJudgeEndTime().getTime() - item.getJudgeStartTime().getTime()) / 1000;

            }

            judgeStatistics.setWorkingSeconds(workingSecs);

            judgeStatistics.setId(keyDate);
            judgeStatistics.setTime(keyDate);
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
            HandExaminationStatistics handExaminationStat = getHandExaminationStatisticsByDate(requestBody, i);

            handExaminationStat.setId(i - startIndex + 1);

            detailedStatistics.put(i, handExaminationStat);
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
            predicateDate = builder.handStartTime.between(dateFrom, dateTo).and(builder.handEndTime.between(dateFrom, dateTo));
        }


        if (requestBody.getFilter().getFieldId() != null) {

        }


        if (requestBody.getFilter().getDeviceId() != null) {
            predicateDevice = builder.handDeviceId.eq(requestBody.getFilter().getDeviceId());
        }


        if (requestBody.getFilter().getUserCategory() != null) {

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

        //List<SerHandExamination> listHandExamination = serHandExaminationRepository.findAll();

        try {

            Iterable<SerHandExamination> listScans = serHandExaminationRepository.findAll(predicateTotal);

            long workingSecs = 0;

            for (SerHandExamination item : listScans) {

                workingSecs += (item.getHandEndTime().getTime() - item.getHandStartTime().getTime()) / 1000;

            }

            handExaminationStatistics.setWorkingSeconds(workingSecs);

            handExaminationStatistics.setId(keyDate);
            handExaminationStatistics.setTime(keyDate);
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

        HashMap<Long, TotalStatistics> detailedStatistics = new HashMap<Long, TotalStatistics>();

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
            TotalStatistics totalStat = getPreviewStatisticsByDate(requestBody, i);
            totalStat.setId(i - startIndex + 1);
            detailedStatistics.put((long)i, totalStat);
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

        try {
            totalStatistics.setId(keyDate);
            totalStatistics.setTime(keyDate);

        } catch (Exception e) {


        }
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


    public long getWorkingSecondsByUser(TaskManagementController.StatisticsByUserRequestBody requestBody, TableType tbType) {

        QSerTask serTask = QSerTask.serTask;

        BooleanBuilder predicate = new BooleanBuilder(serTask.isNotNull());

        StatisticsByUserRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {

            if (filter.getModeId() != null) {
                predicate.and(serTask.workFlow.workMode.modeId.eq(filter.getModeId()));
            }
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {

                if (tbType == TableType.SER_SCAN) {

                    Predicate scanUserName = serTask.serScan.scanPointsman.userName.contains(filter.getUserName());
                    predicate.and(scanUserName);

                } else if (tbType == TableType.SER_JUDGE_GRAPH) {

                    Predicate judgeUserName = serTask.serJudgeGraph.judgeUser.userName.contains(filter.getUserName());
                    predicate.and(judgeUserName);

                } else if (tbType == TableType.SER_HAND_EXAMINATION) {

                    Predicate handUserName = serTask.serHandExamination.handUser.userName.contains(filter.getUserName());
                    predicate.and(handUserName);

                }

            }
            if (filter.getStartTime() != null) {

                if (tbType == TableType.SER_SCAN) {

                    predicate.and(serTask.serScan.scanStartTime.after(filter.getStartTime()));

                } else if (tbType == TableType.SER_JUDGE_GRAPH) {

                    predicate.and(serTask.serJudgeGraph.judgeStartTime.after(filter.getStartTime()));

                } else {

                    predicate.and(serTask.serHandExamination.handStartTime.after(filter.getStartTime()));

                }


            }
            if (filter.getEndTime() != null) {

                if (tbType == TableType.SER_SCAN) {

                    predicate.and(serTask.serScan.scanEndTime.before(filter.getEndTime()));

                } else if (tbType == TableType.SER_JUDGE_GRAPH) {

                    predicate.and(serTask.serJudgeGraph.judgeEndTime.before(filter.getEndTime()));

                } else {

                    predicate.and(serTask.serHandExamination.handEndTime.before(filter.getEndTime()));

                }

            }
        }

        Iterable<SerTask> listTasks = serTaskRespository.findAll(predicate);

        long workingSeconds = 0;

        for (SerTask item : listTasks) {

            if (tbType == TableType.SER_SCAN) {

                workingSeconds += (item.getSerScan().getScanEndTime().getTime() - item.getSerScan().getScanStartTime().getTime()) / 1000;

            } else if (tbType == TableType.SER_JUDGE_GRAPH) {

                workingSeconds += (item.getSerJudgeGraph().getJudgeEndTime().getTime() - item.getSerJudgeGraph().getJudgeStartTime().getTime()) / 1000;

            } else {

                workingSeconds += (item.getSerHandExamination().getHandEndTime().getTime() - item.getSerHandExamination().getHandStartTime().getTime()) / 1000;

            }


        }

        return workingSeconds;

    }

    public long getWorkingSecondsByDevice(TaskManagementController.StatisticsByDeviceRequestBody requestBody, TableType tbType) {

        QSerTask serTask = QSerTask.serTask;

        BooleanBuilder predicate = new BooleanBuilder(serTask.isNotNull());

        StatisticsByDeviceRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {

            if (filter.getDeviceCategoryId() != null) {

                if (tbType == TableType.SER_SCAN) {

                    Predicate scanDeviceCategory = serTask.serScan.scanDevice.categoryId.eq(filter.getDeviceCategoryId());
                    predicate.and(scanDeviceCategory);

                } else if (tbType == TableType.SER_JUDGE_GRAPH) {

                    Predicate judgeDeviceCategory = serTask.serJudgeGraph.judgeDevice.categoryId.eq(filter.getDeviceCategoryId());
                    predicate.and(judgeDeviceCategory);

                } else if (tbType == TableType.SER_HAND_EXAMINATION) {

                    Predicate handDeviceCategory = serTask.serHandExamination.handDevice.categoryId.eq(filter.getDeviceCategoryId());
                    predicate.and(handDeviceCategory);

                }
            }
            if (filter.getDeviceId() != null) {

                if (tbType == TableType.SER_SCAN) {

                    Predicate scanDeviceId = serTask.serScan.scanDevice.deviceId.eq(filter.getDeviceId());
                    predicate.and(scanDeviceId);

                } else if (tbType == TableType.SER_JUDGE_GRAPH) {

                    Predicate judgeDeviceId = serTask.serJudgeGraph.judgeDevice.deviceId.eq(filter.getDeviceId());
                    predicate.and(judgeDeviceId);

                } else if (tbType == TableType.SER_HAND_EXAMINATION) {

                    Predicate handDeviceId = serTask.serHandExamination.handDevice.deviceId.eq(filter.getDeviceId());
                    predicate.and(handDeviceId);

                }

            }

            if (filter.getStartTime() != null) {

                if (tbType == TableType.SER_SCAN) {

                    predicate.and(serTask.serScan.scanStartTime.after(filter.getStartTime()));

                } else if (tbType == TableType.SER_JUDGE_GRAPH) {

                    predicate.and(serTask.serJudgeGraph.judgeStartTime.after(filter.getStartTime()));

                } else {

                    predicate.and(serTask.serHandExamination.handStartTime.after(filter.getStartTime()));

                }

            }

            if (filter.getEndTime() != null) {

                if (tbType == TableType.SER_SCAN) {

                    predicate.and(serTask.serScan.scanEndTime.before(filter.getEndTime()));

                } else if (tbType == TableType.SER_JUDGE_GRAPH) {

                    predicate.and(serTask.serJudgeGraph.judgeEndTime.before(filter.getEndTime()));

                } else {

                    predicate.and(serTask.serHandExamination.handEndTime.before(filter.getEndTime()));

                }

            }
        }

        Iterable<SerTask> listTasks = serTaskRespository.findAll(predicate);

        long workingSeconds = 0;

        for (SerTask item : listTasks) {

            if (tbType == TableType.SER_SCAN) {

                workingSeconds += (item.getSerScan().getScanEndTime().getTime() - item.getSerScan().getScanStartTime().getTime()) / 1000;

            } else if (tbType == TableType.SER_JUDGE_GRAPH) {

                workingSeconds += (item.getSerJudgeGraph().getJudgeEndTime().getTime() - item.getSerJudgeGraph().getJudgeStartTime().getTime()) / 1000;

            } else {

                workingSeconds += (item.getSerHandExamination().getHandEndTime().getTime() - item.getSerHandExamination().getHandStartTime().getTime()) / 1000;

            }

        }

        return workingSeconds;

    }

    @RequestMapping(value = "/statistics/get-by-user-sum", method = RequestMethod.POST)
    public Object statisticsByUserGet(
            @RequestBody @Valid TaskManagementController.StatisticsByUserRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        long totalSecs = 0;
        long scanSecs = 0;
        long judgeSecs = 0;
        long handSecs = 0;

        scanSecs = getWorkingSecondsByUser(requestBody, TableType.SER_SCAN);
        judgeSecs = getWorkingSecondsByUser(requestBody, TableType.SER_JUDGE_GRAPH);
        handSecs = getWorkingSecondsByUser(requestBody, TableType.SER_HAND_EXAMINATION);

        totalSecs = scanSecs + judgeSecs + handSecs;

        StatisticsByUserResponse response = new StatisticsByUserResponse();
        response.setTotalSeconds(totalSecs);
        response.setScanSeconds(scanSecs);
        response.setJudgeSeconds(judgeSecs);
        response.setHandSeconds(handSecs);

        return  new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/statistics/get-by-device-sum", method = RequestMethod.POST)
    public Object statisticsByDeviceGet(
            @RequestBody @Valid TaskManagementController.StatisticsByDeviceRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        long totalSecs = 0;
        long scanSecs = 0;
        long judgeSecs = 0;
        long handSecs = 0;

        scanSecs = getWorkingSecondsByDevice(requestBody, TableType.SER_SCAN);
        judgeSecs = getWorkingSecondsByDevice(requestBody, TableType.SER_JUDGE_GRAPH);
        handSecs = getWorkingSecondsByDevice(requestBody, TableType.SER_HAND_EXAMINATION);

        totalSecs = scanSecs + judgeSecs + handSecs;

        StatisticsByUserResponse response = new StatisticsByUserResponse();
        response.setTotalSeconds(totalSecs);
        response.setScanSeconds(scanSecs);
        response.setJudgeSeconds(judgeSecs);
        response.setHandSeconds(handSecs);

        return  new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/statistics/get-judge-statistics", method = RequestMethod.POST)
    public Object getJudgeSummary(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<JudgeStatisticsResponseModel> response = new ArrayList<>();
        List<Object> result = new ArrayList<>();

        if (requestBody.getFilter().getStatWidth().equals(StatisticWidth.MONTH)) {

            result = serJudgeGraphRepository.getStatisticsAllByMonth(requestBody.getFilter().getFieldId(), requestBody.getFilter().getDeviceId());

        }

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);

            JudgeStatisticsResponseModel record = new JudgeStatisticsResponseModel();

            int t = Integer.parseInt(item[0].toString());
            record.setTime(Integer.parseInt(item[0].toString()));
            record.setTotal(Long.parseLong(item[1].toString()));
            record.setSuspiction(Long.parseLong(item[2].toString()));
            record.setNoSuspiction(Long.parseLong(item[3].toString()));
            record.setAtrResult(Long.parseLong(item[4].toString()));
            record.setAssignResult(Long.parseLong(item[5].toString()));
            record.setArtificialResult(Long.parseLong(item[6].toString()));
            record.setMaxDuration(Double.parseDouble(item[7].toString()));
            record.setMinDuration(Double.parseDouble(item[8].toString()));
            record.setAvgDuration(Double.parseDouble(item[9].toString()));

            response.add(record);

        }

        return  new CommonResponseBody(ResponseMessage.OK, response);

    }


    @RequestMapping(value = "/statistics/get-handexamination-statistics", method = RequestMethod.POST)
    public Object getHandExaminationSummary(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<HandExaminationResponseModel> response = new ArrayList<>();

        List<Object> result = serHandExaminationRepository.getStatistics(

        );


        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);

            HandExaminationResponseModel record = new HandExaminationResponseModel();

            int t = Integer.parseInt(item[0].toString());
            record.setTime(Integer.parseInt(item[0].toString()));
            record.setTotal(Long.parseLong(item[1].toString()));
            record.setSeizure(Long.parseLong(item[2].toString()));
            record.setNoSeizure(Long.parseLong(item[3].toString()));
            record.setTotalJudge(Long.parseLong(item[4].toString()));
            record.setMissingReport(Long.parseLong(item[5].toString()));
            record.setFalseReport(Long.parseLong(item[6].toString()));
            record.setArtificialJudge(Long.parseLong(item[7].toString()));
            record.setArtificialJudgeMissing(Long.parseLong(item[8].toString()));
            record.setArtificialJudgeMistake(Long.parseLong(item[9].toString()));
            record.setIntelligenceJudge(Long.parseLong(item[10].toString()));
            record.setIntelligenceJudgeMissing(Long.parseLong(item[11].toString()));
            record.setIntelligenceJudgeMistake(Long.parseLong(item[12].toString()));

            record.setMaxDuration(Double.parseDouble(item[13].toString()));
            record.setMinDuration(Double.parseDouble(item[14].toString()));
            record.setAvgDuration(Double.parseDouble(item[15].toString()));

            response.add(record);

        }

        return  new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/statistics/get-evaluatejudge-statistics", method = RequestMethod.POST)
    public Object geEvaluateJudgeSummary(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<HandExaminationResponseModel> response = new ArrayList<>();

        List<Object> result = serHandExaminationRepository.getEvaluateStatistics(

        );


        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);

            HandExaminationResponseModel record = new HandExaminationResponseModel();

            int t = Integer.parseInt(item[0].toString());
            record.setTime(Integer.parseInt(item[0].toString()));
            record.setTotal(Long.parseLong(item[1].toString()));
            record.setSeizure(Long.parseLong(item[2].toString()));
            record.setNoSeizure(Long.parseLong(item[3].toString()));
            record.setTotalJudge(Long.parseLong(item[4].toString()));
            record.setMissingReport(Long.parseLong(item[5].toString()));
            record.setFalseReport(Long.parseLong(item[6].toString()));
            record.setArtificialJudge(Long.parseLong(item[7].toString()));
            record.setArtificialJudgeMissing(Long.parseLong(item[8].toString()));
            record.setArtificialJudgeMistake(Long.parseLong(item[9].toString()));
            record.setIntelligenceJudge(Long.parseLong(item[10].toString()));
            record.setIntelligenceJudgeMissing(Long.parseLong(item[11].toString()));
            record.setIntelligenceJudgeMistake(Long.parseLong(item[12].toString()));

            record.setMaxDuration(Double.parseDouble(item[13].toString()));
            record.setMinDuration(Double.parseDouble(item[14].toString()));
            record.setAvgDuration(Double.parseDouble(item[15].toString()));

            response.add(record);

        }

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/statistics/get-statistics-by-user-all-pagination", method = RequestMethod.POST)
    public Object getStatisticsByUser(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Map<Long, List<SerScan>> scans =  serScanRepository.findAll().stream().collect(Collectors.groupingBy(SerScan::getScanPointsmanId, Collectors.toList() ));
        Map<Long, List<SerJudgeGraph>> judges = serJudgeGraphRepository.findAll().stream().collect(Collectors.groupingBy(SerJudgeGraph::getJudgeUserId, Collectors.toList() ));
        Map<Long, List<SerHandExamination>> handExaminations = serHandExaminationRepository.findAll().stream().collect(Collectors.groupingBy(SerHandExamination::getHandUserId, Collectors.toList() ));


        HashMap<Long, TotalStatistics> listTotalStatistics = new HashMap<Long, TotalStatistics>();

        for (Map.Entry<Long, List<SerScan>> entry : scans.entrySet()) {
            Long userId = entry.getKey();
            List<SerScan> listScans = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            ScanStatistics scanStat = new ScanStatistics();

            long totalScan = 0;
            long validScan = 0;
            long invalidScan = 0;

            for (int i = 0; i < listScans.size(); i ++) {

                if (listScans.get(i).getScanInvalid().equals(SerScan.Invalid.TRUE)) {

                    validScan ++;

                }

                if (listScans.get(i).getScanInvalid().equals(SerScan.Invalid.FALSE)) {

                    invalidScan ++;

                }
            }

            scanStat.setValidScan(validScan);
            scanStat.setInvalidScan(invalidScan);
            scanStat.setTotalScan(listScans.size());

            totalStat.setScanStatistics(scanStat);
            listTotalStatistics.put(userId, totalStat);

        }

        for (Map.Entry<Long, List<SerJudgeGraph>> entry : judges.entrySet()) {

            Long userId = entry.getKey();
            List<SerJudgeGraph> listJudge = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            JudgeStatistics judgeStat = new JudgeStatistics();

            long suspiction = 0;
            long noSuspiction = 0;

            for (int i = 0; i < listJudge.size(); i ++) {

                if (listJudge.get(i).getJudgeResult().equals(SerJudgeGraph.Result.TRUE)) {

                    suspiction ++;

                }

                if (listJudge.get(i).getJudgeResult().equals(SerJudgeGraph.Result.TRUE)) {

                    noSuspiction ++;

                }
            }

            judgeStat.setSuspictionJudge(suspiction);
            judgeStat.setNoSuspictionJudge(noSuspiction);
            judgeStat.setTotalJudge(listJudge.size());

            if (listTotalStatistics.containsKey(userId)) {

                listTotalStatistics.get(userId).setJudgeStatistics(judgeStat);

            }
            else {

                totalStat.setJudgeStatistics(judgeStat);
                listTotalStatistics.put(userId, totalStat);

            }

        }

        for (Map.Entry<Long, List<SerHandExamination>> entry : handExaminations.entrySet()) {

            Long userId = entry.getKey();
            List<SerHandExamination> listHand = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            HandExaminationStatistics handStat = new HandExaminationStatistics();

            long seizure = 0;
            long noSeizure = 0;

            for (int i = 0; i < listHand.size(); i ++) {

                if (listHand.get(i).getHandResult().equals(SerHandExamination.Result.TRUE)) {

                    seizure ++;

                }

                if (listHand.get(i).getHandResult().equals(SerHandExamination.Result.TRUE)) {

                    noSeizure ++;

                }
            }

            handStat.setSeizureHandExamination(seizure);
            handStat.setNoSeizureHandExamination(noSeizure);
            handStat.setTotalHandExamination(listHand.size());

            if (listTotalStatistics.containsKey(userId)) {

                listTotalStatistics.get(userId).setHandExaminationStatistics(handStat);

            }
            else {

                totalStat.setHandExaminationStatistics(handStat);
                listTotalStatistics.put(userId, totalStat);

            }

        }

        TotalStatisticsResponse response = new TotalStatisticsResponse();
        response.setDetailedStatistics(listTotalStatistics);
        response.setTotal(listTotalStatistics.keySet().size());
        response.setPer_page(requestBody.getPerPage());
        response.setCurrent_page(requestBody.getCurrentPage());
        response.setLast_page(listTotalStatistics.keySet().size() / requestBody.getPerPage() + 1);
        response.setFrom((requestBody.getCurrentPage() - 1 )* requestBody.getPerPage() + 1);
        response.setTo((requestBody.getCurrentPage())* requestBody.getPerPage());



        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/statistics/get-statistics-by-device-all-pagination", method = RequestMethod.POST)
    public Object getStatisticsByDevice(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Map<Long, List<SerScan>> scans =  serScanRepository.findAll().stream().collect(Collectors.groupingBy(SerScan::getScanDeviceId, Collectors.toList() ));
        Map<Long, List<SerJudgeGraph>> judges = serJudgeGraphRepository.findAll().stream().collect(Collectors.groupingBy(SerJudgeGraph::getJudgeDeviceId, Collectors.toList() ));
        Map<Long, List<SerHandExamination>> handExaminations = serHandExaminationRepository.findAll().stream().collect(Collectors.groupingBy(SerHandExamination::getHandDeviceId, Collectors.toList() ));


        HashMap<Long, TotalStatistics> listTotalStatistics = new HashMap<Long, TotalStatistics>();

        for (Map.Entry<Long, List<SerScan>> entry : scans.entrySet()) {
            Long deviceId = entry.getKey();
            List<SerScan> listScans = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            ScanStatistics scanStat = new ScanStatistics();

            long totalScan = 0;
            long validScan = 0;
            long invalidScan = 0;

            for (int i = 0; i < listScans.size(); i ++) {

                if (listScans.get(i).getScanInvalid().equals(SerScan.Invalid.TRUE)) {

                    validScan ++;

                }

                if (listScans.get(i).getScanInvalid().equals(SerScan.Invalid.FALSE)) {

                    invalidScan ++;

                }
            }

            scanStat.setValidScan(validScan);
            scanStat.setInvalidScan(invalidScan);
            scanStat.setTotalScan(listScans.size());

            totalStat.setScanStatistics(scanStat);
            listTotalStatistics.put(deviceId, totalStat);

        }

        for (Map.Entry<Long, List<SerJudgeGraph>> entry : judges.entrySet()) {

            Long deviceId = entry.getKey();
            List<SerJudgeGraph> listJudge = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            JudgeStatistics judgeStat = new JudgeStatistics();

            long suspiction = 0;
            long noSuspiction = 0;

            for (int i = 0; i < listJudge.size(); i ++) {

                if (listJudge.get(i).getJudgeResult().equals(SerJudgeGraph.Result.TRUE)) {

                    suspiction ++;

                }

                if (listJudge.get(i).getJudgeResult().equals(SerJudgeGraph.Result.TRUE)) {

                    noSuspiction ++;

                }
            }

            judgeStat.setSuspictionJudge(suspiction);
            judgeStat.setNoSuspictionJudge(noSuspiction);
            judgeStat.setTotalJudge(listJudge.size());

            if (listTotalStatistics.containsKey(deviceId)) {

                listTotalStatistics.get(deviceId).setJudgeStatistics(judgeStat);

            }
            else {

                totalStat.setJudgeStatistics(judgeStat);
                listTotalStatistics.put(deviceId, totalStat);

            }

        }

        for (Map.Entry<Long, List<SerHandExamination>> entry : handExaminations.entrySet()) {

            Long deviceId = entry.getKey();
            List<SerHandExamination> listHand = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            HandExaminationStatistics handStat = new HandExaminationStatistics();

            long seizure = 0;
            long noSeizure = 0;

            for (int i = 0; i < listHand.size(); i ++) {

                if (listHand.get(i).getHandResult().equals(SerHandExamination.Result.TRUE)) {

                    seizure ++;

                }

                if (listHand.get(i).getHandResult().equals(SerHandExamination.Result.TRUE)) {

                    noSeizure ++;

                }
            }

            handStat.setSeizureHandExamination(seizure);
            handStat.setNoSeizureHandExamination(noSeizure);
            handStat.setTotalHandExamination(listHand.size());

            if (listTotalStatistics.containsKey(deviceId)) {

                listTotalStatistics.get(deviceId).setHandExaminationStatistics(handStat);

            }
            else {

                totalStat.setHandExaminationStatistics(handStat);
                listTotalStatistics.put(deviceId, totalStat);

            }

        }

        TotalStatisticsResponse response = new TotalStatisticsResponse();
        response.setDetailedStatistics(listTotalStatistics);
        response.setTotal(listTotalStatistics.keySet().size());
        response.setPer_page(requestBody.getPerPage());
        response.setCurrent_page(requestBody.getCurrentPage());
        response.setLast_page(listTotalStatistics.keySet().size() / requestBody.getPerPage() + 1);
        response.setFrom((requestBody.getCurrentPage() - 1 )* requestBody.getPerPage() + 1);
        response.setTo((requestBody.getCurrentPage())* requestBody.getPerPage());



        return new CommonResponseBody(ResponseMessage.OK, response);

    }

}

