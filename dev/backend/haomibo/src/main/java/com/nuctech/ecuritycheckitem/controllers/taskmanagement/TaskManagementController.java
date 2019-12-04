package com.nuctech.ecuritycheckitem.controllers.taskmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.PreviewStatisticsController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import io.swagger.models.auth.In;
import lombok.*;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Query;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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

    public static class StatisticWidth {
        public static final String HOUR = "hour";
        public static final String DAY = "day";
        public static final String WEEK = "week";
        public static final String MONTH = "month";
        public static final String QUARTER = "quarter";
        public static final String YEAR = "year";
    }

    List<String> handGoodsIDList = Arrays.asList(new String[]{
            "1000001601",
            "1000001602",
            "1000001603",
            "1000001604",
            "1000001605"
    });

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

    /**
     * Private purpose Only
     * Get Start KeyDate and End Key Date for statistics
     * <p>
     * Ex: In case of Hour - it returns [1, 24], In case of Month it returns [1, 12]
     *
     * @param requestBody
     * @return [startKeyDate, endKeyDate]
     */
    public List<Integer> getKeyValuesforStatistics(TaskManagementController.StatisticsRequestBody requestBody) {

        Integer keyValueMin = 0, keyValueMax = 0;

        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    break;
                case StatisticWidth.DAY:
                    keyValueMin = 1;
                    keyValueMax = 31;
                    break;
                case StatisticWidth.WEEK:
                    keyValueMin = 1;
                    keyValueMax = 5;
                    break;
                case StatisticWidth.MONTH:
                    keyValueMin = 1;
                    keyValueMax = 12;
                    break;
                case StatisticWidth.QUARTER:
                    keyValueMin = 1;
                    keyValueMax = 4;
                    break;
                case StatisticWidth.YEAR:

                    Integer yearMax = serJudgeGraphRepository.findMaxYear();
                    Integer yearMin = serJudgeGraphRepository.findMinYear();

//                    if (yearMax > Calendar.getInstance().get(Calendar.YEAR)) {
//                        yearMax = Calendar.getInstance().get(Calendar.YEAR);
//                    }

                    //if (yearMin < 1970) {
                    yearMin = 1970;
                    //}

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

                    if (keyValueMin < yearMin) {
                        keyValueMin = yearMin;
                    }

                    if (keyValueMax > yearMax) {
                        keyValueMax = yearMax;
                    }

                    break;
                default:
                    keyValueMin = 0;
                    keyValueMax = -1;
                    break;
            }
        }

        List<Integer> result = new ArrayList<>();
        result.add(keyValueMin);
        result.add(keyValueMax);

        return result;

    }

    public JudgeStatisticsPaginationResponse getJudgeStatistics(TaskManagementController.StatisticsRequestBody requestBody) {

        Map<String, Object> paramaterMap = new TreeMap<String, Object>();
        List<String> whereCause = new ArrayList<String>();

        StringBuilder queryBuilder = new StringBuilder();

        StatisticsRequestBody.Filter filter = requestBody.getFilter();

        String groupBy = "hour";
        if (requestBody.getFilter().getStatWidth() != null && requestBody.getFilter().getStatWidth().isEmpty()) {
            groupBy = requestBody.getFilter().getStatWidth();
        }


        JudgeStatisticsPaginationResponse response = new JudgeStatisticsPaginationResponse();

        queryBuilder.append("SELECT\n" +
                "\n" +
                groupBy +
                "\t( judge_start_time ) AS time,\n" +
                "\tsum( IF ( g.judge_user_id != l.USER_ID, 1, 0 ) ) AS artificialJudge,\n" +
                "\tsum( IF ( s.SCAN_INVALID LIKE 'true' AND a.ASSIGN_TIMEOUT LIKE 'true', 1, 0 ) ) AS assignResult,\n" +
                "\tsum( IF ( g.judge_user_id = l.USER_ID, 1, 0 ) ) AS judgeTimeout,\n" +
                "\tsum( IF ( s.SCAN_ATR_RESULT LIKE 'true', 1, 0 ) ) AS atrResult,\n" +
                "\tsum( IF ( s.SCAN_ATR_RESULT LIKE 'true' AND g.JUDGE_RESULT LIKE 'true', 1, 0 ) ) AS suspiction,\n" +
                "\tsum( IF ( s.SCAN_ATR_RESULT LIKE 'false' AND g.JUDGE_RESULT LIKE 'false', 1, 0 ) ) AS noSuspiction,\n" +
                "\tcount( JUDGE_ID ) AS total ,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS avgDuration,\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS minDuration,\n" +
                "\t\n" +
                "\tAVG( CASE WHEN g.JUDGE_USER_ID != l.USER_ID THEN TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ELSE NULL END ) \n" +
                "\t AS artificialAvgDuration,\n" +
                "\tMAX( CASE WHEN g.JUDGE_USER_ID != l.USER_ID THEN TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ELSE NULL END ) AS artificialMaxDuration,\n" +
                "\tMIN( CASE WHEN g.JUDGE_USER_ID != l.USER_ID THEN TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ELSE NULL END ) AS artificialMinDuration\n" +
                "FROM\n" +
                "\tser_judge_graph g\n" +
                "\tLEFT JOIN sys_user u ON g.JUDGE_USER_ID = u.USER_ID\n" +
                "\tLEFT JOIN ser_login_info l ON g.JUDGE_DEVICE_ID = l.DEVICE_ID\n" +
                "\tLEFT JOIN ser_task t ON g.TASK_ID = t.TASK_ID\n" +
                "\tLEFT JOIN sys_workflow wf ON t.WORKFLOW_ID = wf.WORKFLOW_ID\n" +
                "\tLEFT JOIN ser_scan s ON t.TASK_ID = s.TASK_ID\n" +
                "\tLEFT JOIN ser_assign a ON t.task_id = a.task_id \n");

        whereCause.add("s.SCAN_INVALID like 'true'");

        if (requestBody.getFilter().getFieldId() != null) {

            whereCause.add("t.SCENE = " + requestBody.getFilter().getFieldId());

        }
        if (requestBody.getFilter().getDeviceId() != null) {

            whereCause.add("g.JUDGE_DEVICE_ID = " + requestBody.getFilter().getDeviceId());

        }
        if (requestBody.getFilter().getUserName() != null && !requestBody.getFilter().getUserName().isEmpty()) {

            whereCause.add("u.USER_NAME like '%" + requestBody.getFilter().getUserName() + "%' ");

        }
        if (requestBody.getFilter().getStartTime() != null) {

            Date date = requestBody.getFilter().getStartTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);

            whereCause.add("g.JUDGE_START_TIME >= '" + strDate + "'");

        }
        if (requestBody.getFilter().getEndTime() != null) {

            Date date = requestBody.getFilter().getEndTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);

            whereCause.add("g.JUDGE_END_TIME <= '" + strDate + "'");

        }


        //.... Get Total Statistics
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        Query jpaQueryTotal = entityManager.createNativeQuery(queryBuilder.toString());

        List<Object> resultTotal = jpaQueryTotal.getResultList();

        SerPlatformCheckParams systemConstants = new SerPlatformCheckParams();
        try {
            systemConstants = serPlatformCheckParamRepository.getOne(0);
        } catch (Exception e) {

        }

        for (int i = 0; i < resultTotal.size(); i++) {

            Object[] item = (Object[]) resultTotal.get(i);

            JudgeStatisticsResponseModel record = new JudgeStatisticsResponseModel();
            try {

                record.setTime(Integer.parseInt(item[0].toString()));
                record.setArtificialJudge(Long.parseLong(item[1].toString()));
                record.setAssignTimeout(Long.parseLong(item[2].toString()));
                record.setJudgeTimeout(Long.parseLong(item[3].toString()));
                record.setAtrResult(Long.parseLong(item[4].toString()));
                record.setSuspiction(Long.parseLong(item[5].toString()));
                record.setNoSuspiction(Long.parseLong(item[6].toString()));
                record.setTotal(Long.parseLong(item[7].toString()));
                record.setAvgDuration(Double.parseDouble(item[8].toString()));
                record.setMaxDuration(Double.parseDouble(item[9].toString()));
                record.setMinDuration(Double.parseDouble(item[1].toString()));
                record.setAvgArtificialJudgeDuration(Long.parseLong(item[1].toString()));
                record.setMaxArtificialJudgeDuration(Long.parseLong(item[1].toString()));
                record.setMinArtificialJudgeDuration(Long.parseLong(item[1].toString()));


                record.setArtificialResultRate(record.getArtificialResult() / (double) record.getTotal());
                record.setAssignTimeoutResultRate(record.getAssignTimeout() / (double) record.getTotal());
                record.setJudgeTimeoutResultRate(record.getJudgeTimeout() / (double) record.getTotal());
                record.setSuspictionRate(record.getSuspiction() / (double) record.getTotal());
                record.setNoSuspictionRate(record.getNoSuspiction() / (double) record.getTotal());
                record.setScanResultRate(record.getAtrResult() / (double) record.getTotal());
                record.setLimitedArtificialDuration(systemConstants.getJudgeProcessingTime());

                TreeMap<String, Integer> handGoods = new TreeMap<>();

            } catch (Exception e) {

            }

            response.setTotalStatistics(record);

        }


        //.... Get Detailed Statistics

        queryBuilder.append(" GROUP BY  " + groupBy + "(g.JUDGE_START_TIME)");

        Query jpaQuery = entityManager.createNativeQuery(queryBuilder.toString());


        List<Object> result = jpaQuery.getResultList();

        TreeMap<Integer, JudgeStatisticsResponseModel> data = new TreeMap<>();

        //init hash map
        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) {

        }

        for (Integer i = keyValueMin; i <= keyValueMax; i++) {
            JudgeStatisticsResponseModel item = new JudgeStatisticsResponseModel();
            item.setTime(i);
            data.put(i, item);
        }




        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);

            JudgeStatisticsResponseModel record = new JudgeStatisticsResponseModel();
            try {

                record.setTime(Integer.parseInt(item[0].toString()));
                record.setArtificialJudge(Long.parseLong(item[1].toString()));
                record.setAssignTimeout(Long.parseLong(item[2].toString()));
                record.setJudgeTimeout(Long.parseLong(item[3].toString()));
                record.setAtrResult(Long.parseLong(item[4].toString()));
                record.setSuspiction(Long.parseLong(item[5].toString()));
                record.setNoSuspiction(Long.parseLong(item[6].toString()));
                record.setTotal(Long.parseLong(item[7].toString()));
                record.setAvgDuration(Double.parseDouble(item[8].toString()));
                record.setMaxDuration(Double.parseDouble(item[9].toString()));
                record.setMinDuration(Double.parseDouble(item[1].toString()));
                record.setAvgArtificialJudgeDuration(Long.parseLong(item[1].toString()));
                record.setMaxArtificialJudgeDuration(Long.parseLong(item[1].toString()));
                record.setMinArtificialJudgeDuration(Long.parseLong(item[1].toString()));


                record.setArtificialResultRate(record.getArtificialResult() / (double) record.getTotal());
                record.setAssignTimeoutResultRate(record.getAssignTimeout() / (double) record.getTotal());
                record.setJudgeTimeoutResultRate(record.getJudgeTimeout() / (double) record.getTotal());
                record.setSuspictionRate(record.getSuspiction() / (double) record.getTotal());
                record.setNoSuspictionRate(record.getNoSuspiction() / (double) record.getTotal());
                record.setScanResultRate(record.getAtrResult() / (double) record.getTotal());
                record.setLimitedArtificialDuration(systemConstants.getJudgeProcessingTime());

            } catch (Exception e) {

            }

            data.put(record.getTime(), record);

        }

        TreeMap<Integer, JudgeStatisticsResponseModel> sorted = new TreeMap<>();

        for (Integer i = keyValueMin; i <= keyValueMax; i ++) {

            sorted.put(i, data.get(i));

        }


        TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics = new TreeMap<>();

        if (requestBody.getCurrentPage() != null && requestBody.getCurrentPage() != null && requestBody.getCurrentPage() > 0 && requestBody.getPerPage() > 0) {

            Integer from, to;
            from = (requestBody.getCurrentPage() - 1) * requestBody.getPerPage() + keyValueMin;
            to = requestBody.getCurrentPage() * requestBody.getPerPage() - 1 + keyValueMin;

            if (from < keyValueMin) {
                from  = keyValueMin;
            }

            if (to > keyValueMax) {
                to = keyValueMax;
            }

            response.setFrom(from);
            response.setTo(to);
            response.setPer_page(requestBody.getPerPage());
            response.setCurrent_page(requestBody.getCurrentPage());

            for (Integer i = from ; i <= to; i ++) {

                detailedStatistics.put(i, sorted.get(i));

            }

            response.setDetailedStatistics(detailedStatistics);

        }
        else {

            response.setDetailedStatistics(sorted);

        }

        try {

            response.setTotal(sorted.size());
            if (response.getTotal() % response.getPer_page() == 0) {
                response.setLast_page(response.getTotal() / response.getPer_page());
            }
            else {
                response.setLast_page(response.getTotal() / response.getPer_page() + 1);
            }

        } catch (Exception e) {

        }

        return response;

    }

    private TreeMap<String, Long> getSuspicionHandGoodsByDate(TaskManagementController.StatisticsRequestBody requestBody, Integer byDate) {

        TreeMap<String, Long> suspicionResult = new TreeMap<>();

        QHistory history = QHistory.history;

        StatisticsRequestBody.Filter filter = requestBody.getFilter();

        for (int i = 0; i < handGoodsIDList.size(); i ++) {

            BooleanBuilder predicate = new BooleanBuilder(history.isNotNull());

            if (byDate != null) {

                String statWidth = "";
                try {
                    statWidth = requestBody.getFilter().getStatWidth();
                }
                catch (Exception e) {

                }

                if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {

                    switch (statWidth) {
                        case StatisticWidth.YEAR:
                            predicate.and(history.handStartTime.year().eq(byDate));
                            break;
                        case StatisticWidth.MONTH:
                            predicate.and(history.handStartTime.month().eq(byDate));
                            break;
                        case StatisticWidth.DAY:
                            predicate.and(history.handStartTime.dayOfMonth().eq(byDate));
                            break;
                        case StatisticWidth.HOUR:
                            predicate.and(history.handStartTime.hour().eq(byDate));
                            break;
                        case StatisticWidth.WEEK:
                            predicate.and( history.handStartTime.dayOfMonth().between((byDate - 1) * 7, byDate * 7));
                            break;
                        case StatisticWidth.QUARTER:
                            predicate.and(history.handStartTime.month().between((byDate - 1) * 3, (byDate) * 3));
                            break;
                    }
                }

            }

            if (requestBody.getFilter().getFieldId() != null) {

                predicate.and(history.task.fieldId.eq(requestBody.getFilter().getFieldId()));

            }
            if (requestBody.getFilter().getDeviceId() != null) {

                predicate.and(history.handDeviceId.eq(requestBody.getFilter().getDeviceId()));

            }
            if (requestBody.getFilter().getUserName() != null && !requestBody.getFilter().getUserName().isEmpty()) {

                predicate.and(history.handUser.userName.contains(requestBody.getFilter().getUserName()));

            }
            if (requestBody.getFilter().getStartTime() != null) {

                predicate.and(history.handStartTime.after(requestBody.getFilter().getStartTime()));

            }
            if (requestBody.getFilter().getEndTime() != null) {

                predicate.and(history.handEndTime.before(requestBody.getFilter().getEndTime()));

            }

            predicate.and(history.handGoods.eq(handGoodsIDList.get(i)));

            suspicionResult.put(handGoodsIDList.get(i), historyRespository.count(predicate));

        }

        return suspicionResult;
    }

    public SuspicionHandGoodsPaginationResponse getSuspicionHandGoodsStastistics(TaskManagementController.StatisticsRequestBody requestBody) {

        SuspicionHandGoodsPaginationResponse response = new SuspicionHandGoodsPaginationResponse();

        TreeMap<String, Long> totalStatistics = new TreeMap<>();

        totalStatistics = getSuspicionHandGoodsByDate(requestBody, null);

        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);


        Integer startIndex = 0, endIndex = 0;

        Integer keyValueMin = 0, keyValueMax = 0;

        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);

            startIndex = keyValueMin;
            endIndex = keyValueMax;

        }
        catch (Exception e) {

        }

        int curPage = 0;
        int perPage = 0;

        try {
            curPage = requestBody.getCurrentPage();
            perPage = requestBody.getPerPage();
        }
        catch (Exception e) {

        }

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

            try {

                response.setTotal(keyValueMax - keyValueMin + 1);
                response.setPer_page(requestBody.getPerPage());
                response.setCurrent_page(requestBody.getCurrentPage());

                if (response.getTotal() % response.getPer_page() == 0) {
                    response.setLast_page(response.getTotal() / response.getPer_page());
                }
                else {
                    response.setLast_page(response.getTotal() / response.getPer_page() + 1);
                }

            }
            catch (Exception e) {

            }
        }

        TreeMap<Integer, TreeMap<String, Long>> detailedStatistics = new TreeMap<>();

        for (Integer i = startIndex; i <= endIndex; i ++ ) {

            TreeMap<String, Long> suspictionStat = getSuspicionHandGoodsByDate(requestBody, i);
            suspictionStat.put("time", (long)i);
            detailedStatistics.put(i, suspictionStat);

        }


        response.setTotalStatistics(totalStatistics);
        response.setDetailedStatistics(detailedStatistics);
        return response;

    }

    public HandExaminationStatisticsPaginationResponse getHandStatistics(TaskManagementController.StatisticsRequestBody requestBody) {

        Map<String, Object> paramaterMap = new TreeMap<String, Object>();
        List<String> whereCause = new ArrayList<String>();

        StringBuilder queryBuilder = new StringBuilder();

        StatisticsRequestBody.Filter filter = requestBody.getFilter();

        String groupBy = "hour";
        if (requestBody.getFilter().getStatWidth() != null && requestBody.getFilter().getStatWidth().isEmpty()) {
            groupBy = requestBody.getFilter().getStatWidth();
        }


        HandExaminationStatisticsPaginationResponse response = new HandExaminationStatisticsPaginationResponse();

        queryBuilder.append("SELECT\n" +
                "\n" +
                groupBy +
                "\t (h.HAND_START_TIME) as time,\n" +
                "\tcount( HAND_EXAMINATION_ID ) AS total,\n" +
                "\tsum( IF ( h.HAND_RESULT LIKE 'true', 1, 0 ) ) AS seizure,\n" +
                "\tsum( IF ( h.HAND_RESULT LIKE 'false', 1, 0 ) ) AS noSeizure,\n" +
                "\tsum( IF ( s.SCAN_INVALID like 'true', 1, 0)) as totalJudge,\n" +
                "\tsum( IF ( c.HAND_APPRAISE LIKE 'missing', 1, 0 ) ) AS missingReport,\n" +
                "\tsum( IF ( c.HAND_APPRAISE LIKE 'mistake', 1, 0 ) ) AS falseReport,\n" +
                "\t\n" +
                "\tsum( IF ( ISNULL (j.JUDGE_TIMEOUT), 1, 0)) as artificialJudge,\n" +
                "\tsum( IF ( ISNULL (j.JUDGE_TIMEOUT) and c.HAND_APPRAISE like 'missing', 1, 0)) as artificialJudgeMissing,\n" +
                "\tsum( IF ( ISNULL( j.JUDGE_TIMEOUT) and c.HAND_APPRAISE like 'mistake', 1, 0)) as artificialJudgeMistake,\n" +
                "\t\n" +
                "\tsum( IF ( s.SCAN_INVALID like 'true' and (wm.MODE_NAME like '1000001301' OR wm.MODE_NAME like '1000001302') and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true', 1, 0)) as intelligenceJudge,\n" +
                "\tsum( IF ( s.SCAN_INVALID like 'true' and (wm.MODE_NAME like '1000001301' OR wm.MODE_NAME like '1000001302') and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true' and c.HAND_APPRAISE like 'missing', 1, 0)) as intelligenceJudgeMissing,\n" +
                "\tsum( IF ( s.SCAN_INVALID like 'true' and (wm.MODE_NAME like '1000001301' OR wm.MODE_NAME like '1000001302') and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true' and c.HAND_APPRAISE like 'mistake', 1, 0)) as intelligenceJudgeMistake,\n" +
                "\t\n" +
                "\t\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS minDuration,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS avgDuration \n" +
                "\t\n" +
                "FROM\n" +
                "\tser_hand_examination h\n" +
                "\tLEFT JOIN sys_user u ON h.HAND_USER_ID = u.USER_ID\n" +
                "\tLEFT join ser_login_info l on h.HAND_DEVICE_ID = l.DEVICE_ID\n" +
                "\tLEFT JOIN ser_task t ON h.TASK_ID = t.task_id\n" +
                "\tLEFT JOIN ser_check_result2 c ON t.TASK_ID = c.task_id\n" +
                "\tleft join ser_judge_graph j on t.TASK_ID = j.TASK_ID\n" +
                "\tleft join ser_scan s on t.TASK_ID = s.TASK_ID\n" +
                "\tleft join ser_assign a on t.task_id = a.task_id\n" +
                "\tleft join sys_workflow wf on t.WORKFLOW_ID = wf.workflow_id\n" +
                "\tleft join sys_work_mode wm on wf.MODE_ID = wm.MODE_ID\n");

        if (requestBody.getFilter().getFieldId() != null) {

            whereCause.add("t.SCENE = " + requestBody.getFilter().getFieldId());

        }
        if (requestBody.getFilter().getDeviceId() != null) {

            whereCause.add("h.HAND_DEVICE_ID = " + requestBody.getFilter().getDeviceId());

        }
        if (requestBody.getFilter().getUserName() != null && !requestBody.getFilter().getUserName().isEmpty()) {

            whereCause.add("u.USER_NAME like '%" + requestBody.getFilter().getUserName() + "%'");

        }
        if (requestBody.getFilter().getStartTime() != null) {

            Date date = requestBody.getFilter().getStartTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);

            whereCause.add("h.HAND_START_TIME >= '" + strDate + "'");

        }
        if (requestBody.getFilter().getEndTime() != null) {

            Date date = requestBody.getFilter().getEndTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);

            whereCause.add("h.HAND_END_TIME <= '" + strDate + "'");

        }


        //................. get total statistics ....
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        Query jpaQueryTotal = entityManager.createNativeQuery(queryBuilder.toString());

        List<Object> resultTotal = jpaQueryTotal.getResultList();

        for (int i = 0; i < resultTotal.size(); i++) {

            Object[] item = (Object[]) resultTotal.get(i);

            HandExaminationResponseModel record = new HandExaminationResponseModel();
            try {

                record.setTime(Integer.parseInt(item[0].toString()));
                record.setTotal(Long.parseLong(item[1].toString()));
                record.setSeizure(Long.parseLong(item[2].toString()));
                record.setNoSeizure(Long.parseLong(item[3].toString()));
                record.setTotalJudge(Long.parseLong(item[4].toString()));
                record.setMissingReport(Long.parseLong(item[5].toString()));
                record.setMistakeReport(Long.parseLong(item[6].toString()));
                record.setArtificialJudge(Long.parseLong(item[7].toString()));
                record.setArtificialJudgeMissing(Long.parseLong(item[8].toString()));
                record.setArtificialJudgeMistake(Long.parseLong(item[9].toString()));
                record.setIntelligenceJudge(Long.parseLong(item[10].toString()));
                record.setIntelligenceJudgeMissing(Long.parseLong(item[11].toString()));
                record.setIntelligenceJudgeMistake(Long.parseLong(item[12].toString()));

                record.setMaxDuration(Double.parseDouble(item[13].toString()));
                record.setMinDuration(Double.parseDouble(item[14].toString()));
                record.setAvgDuration(Double.parseDouble(item[15].toString()));


                record.setSeizureRate(record.getSeizure() / (double)record.getTotal());
                record.setNoSeizureRate(record.getNoSeizure() / (double)record.getTotal());
                record.setMissingReportRate(record.getMissingReport() / (double) record.getTotal());
                record.setMistakeReportRate(record.getMistakeReport() / (double) record.getTotal());
                record.setArtificialJudgeMissingRate(record.getArtificialJudgeMissing() / (double) record.getArtificialJudge());
                record.setArtificialJudgeMistakeRate(record.getArtificialJudgeMistake() / (double) record.getArtificialJudge());
                record.setIntelligenceJudgeMissingRate(record.getIntelligenceJudgeMissing() / (double) record.getIntelligenceJudge());
                record.setIntelligenceJudgeMistakeRate(record.getIntelligenceJudgeMistake() / (double) record.getIntelligenceJudge());

            } catch (Exception e) {

            }

            response.setTotalStatistics(record);

        }

        //.... Get Detailed Statistics ....
        queryBuilder.append(" GROUP BY  " + groupBy + "(h.HAND_START_TIME)");

        Query jpaQuery = entityManager.createNativeQuery(queryBuilder.toString());


        List<Object> result = jpaQuery.getResultList();

        TreeMap<Integer, HandExaminationResponseModel> data = new TreeMap<>();

        //init hash map
        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) {

        }

        for (Integer i = keyValueMin; i <= keyValueMax; i++) {
            HandExaminationResponseModel item = new HandExaminationResponseModel();
            item.setTime(i);
            data.put(i, item);
        }


        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);

            HandExaminationResponseModel record = new HandExaminationResponseModel();

            try {

                record.setTime(Integer.parseInt(item[0].toString()));
                record.setTotal(Long.parseLong(item[1].toString()));
                record.setSeizure(Long.parseLong(item[2].toString()));
                record.setNoSeizure(Long.parseLong(item[3].toString()));
                record.setTotalJudge(Long.parseLong(item[4].toString()));
                record.setMissingReport(Long.parseLong(item[5].toString()));
                record.setMistakeReport(Long.parseLong(item[6].toString()));
                record.setArtificialJudge(Long.parseLong(item[7].toString()));
                record.setArtificialJudgeMissing(Long.parseLong(item[8].toString()));
                record.setArtificialJudgeMistake(Long.parseLong(item[9].toString()));
                record.setIntelligenceJudge(Long.parseLong(item[10].toString()));
                record.setIntelligenceJudgeMissing(Long.parseLong(item[11].toString()));
                record.setIntelligenceJudgeMistake(Long.parseLong(item[12].toString()));

                record.setMaxDuration(Double.parseDouble(item[13].toString()));
                record.setMinDuration(Double.parseDouble(item[14].toString()));
                record.setAvgDuration(Double.parseDouble(item[15].toString()));

                record.setSeizureRate(record.getSeizure() / (double)record.getTotal());
                record.setNoSeizureRate(record.getNoSeizure() / (double)record.getTotal());
                record.setMissingReportRate(record.getMissingReport() / (double) record.getTotal());
                record.setMistakeReportRate(record.getMistakeReport() / (double) record.getTotal());
                record.setArtificialJudgeMissingRate(record.getArtificialJudgeMissing() / (double) record.getArtificialJudge());
                record.setArtificialJudgeMistakeRate(record.getArtificialJudgeMistake() / (double) record.getArtificialJudge());
                record.setIntelligenceJudgeMissingRate(record.getIntelligenceJudgeMissing() / (double) record.getIntelligenceJudge());
                record.setIntelligenceJudgeMistakeRate(record.getIntelligenceJudgeMistake() / (double) record.getIntelligenceJudge());

            } catch (Exception e) {

            }

            data.put(record.getTime(), record);

        }

        TreeMap<Integer, HandExaminationResponseModel> sorted = new TreeMap<>();

        for (Integer i = keyValueMin; i <= keyValueMax; i ++) {

            sorted.put(i, data.get(i));

        }

        TreeMap<Integer, HandExaminationResponseModel> detailedStatistics = new TreeMap<>();

        if (requestBody.getCurrentPage() != null && requestBody.getCurrentPage() != null && requestBody.getCurrentPage() > 0 && requestBody.getPerPage() > 0) {

            Integer from, to;
            from = (requestBody.getCurrentPage() - 1) * requestBody.getPerPage() + keyValueMin;
            to = requestBody.getCurrentPage() * requestBody.getPerPage() - 1 + keyValueMin;

            if (from < keyValueMin) {
                from  = keyValueMin;
            }

            if (to > keyValueMax) {
                to = keyValueMax;
            }

            response.setFrom(from);
            response.setTo(to);
            response.setPer_page(requestBody.getPerPage());
            response.setCurrent_page(requestBody.getCurrentPage());

            for (Integer i = from ; i <= to; i ++) {

                detailedStatistics.put(i, sorted.get(i));

            }

            response.setDetailedStatistics(detailedStatistics);

        }
        else {

            response.setDetailedStatistics(sorted);

        }


        try {

            response.setTotal(sorted.size());
            if (response.getTotal() % response.getPer_page() == 0) {
                response.setLast_page(response.getTotal() / response.getPer_page());
            }
            else {
                response.setLast_page(response.getTotal() / response.getPer_page() + 1);
            }

        } catch (Exception e) {

        }

        return response;

    }

    public EvaluateJudgeStatisticsPaginationResponse getEvaluateJudgeStatistics(TaskManagementController.StatisticsRequestBody requestBody) {

        Map<String, Object> paramaterMap = new TreeMap<String, Object>();
        List<String> whereCause = new ArrayList<String>();

        StringBuilder queryBuilder = new StringBuilder();

        StatisticsRequestBody.Filter filter = requestBody.getFilter();
        String groupBy = "hour";
        if (requestBody.getFilter().getStatWidth() != null && requestBody.getFilter().getStatWidth().isEmpty()) {
            groupBy = requestBody.getFilter().getStatWidth();
        }


        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();

        queryBuilder.append("SELECT\n" +
                "\n" +
                groupBy +
                "\t (h.HAND_START_TIME) as time,\n" +
                "\tcount( HAND_EXAMINATION_ID ) AS total,\n" +
                "\tsum( IF ( h.HAND_RESULT LIKE 'true', 1, 0 ) ) AS seizure,\n" +
                "\tsum( IF ( h.HAND_RESULT LIKE 'false', 1, 0 ) ) AS noSeizure,\n" +
                "\tsum( IF ( s.SCAN_INVALID like 'true', 1, 0)) as totalJudge,\n" +
                "\tsum( IF ( c.HAND_APPRAISE LIKE 'missing', 1, 0 ) ) AS missingReport,\n" +
                "\tsum( IF ( c.HAND_APPRAISE LIKE 'mistake', 1, 0 ) ) AS falseReport,\n" +
                "\t\n" +
                "\tsum( IF ( j.JUDGE_TIMEOUT like 'weikong', 1, 0)) as artificialJudge,\n" +
                "\tsum( IF ( j.JUDGE_TIMEOUT like 'weikong' and c.HAND_APPRAISE like 'missing', 1, 0)) as artificialJudgeMissing,\n" +
                "\tsum( IF ( j.JUDGE_TIMEOUT like 'weikong' and c.HAND_APPRAISE like 'mistake', 1, 0)) as artificialJudgeMistake,\n" +
                "\t\n" +
                "\tsum( IF ( s.SCAN_INVALID like 'true' and (wm.MODE_NAME like '1000001301' OR wm.MODE_NAME like '1000001302') and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true', 1, 0)) as intelligenceJudge,\n" +
                "\tsum( IF ( s.SCAN_INVALID like 'true' and (wm.MODE_NAME like '1000001301' OR wm.MODE_NAME like '1000001302') and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true' and c.HAND_APPRAISE like 'missing', 1, 0)) as intelligenceJudgeMissing,\n" +
                "\tsum( IF ( s.SCAN_INVALID like 'true' and (wm.MODE_NAME like '1000001301' OR wm.MODE_NAME like '1000001302') and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true' and c.HAND_APPRAISE like 'mistake', 1, 0)) as intelligenceJudgeMistake,\n" +
                "\t\n" +
                "\t\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS minDuration,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS avgDuration \n" +
                "\t\n" +
                "FROM\n" +
                "\tser_hand_examination h\n" +
                "\tLEFT join sys_user u on h.HAND_USER_ID = u.USER_ID\n" +
                "\tLEFT join ser_login_info l on h.HAND_DEVICE_ID = l.DEVICE_ID\n" +
                "\tLEFT JOIN ser_task t ON h.TASK_ID = t.task_id\n" +
                "\tLEFT JOIN ser_check_result2 c ON t.TASK_ID = c.task_id\n" +
                "\tleft join ser_judge_graph j on t.TASK_ID = j.TASK_ID\n" +
                "\tleft join ser_scan s on t.TASK_ID = s.TASK_ID\n" +
                "\tleft join ser_assign a on t.task_id = a.task_id\n" +
                "\tleft join sys_workflow wf on t.WORKFLOW_ID = wf.workflow_id\n" +
                "\tleft join sys_work_mode wm on wf.MODE_ID = wm.MODE_ID\n");

        if (requestBody.getFilter().getFieldId() != null) {

            whereCause.add("t.SCENE = " + requestBody.getFilter().getFieldId());

        }
        if (requestBody.getFilter().getDeviceId() != null) {

            whereCause.add("h.HAND_DEVICE_ID = " + requestBody.getFilter().getDeviceId());

        }
        if (requestBody.getFilter().getUserName() != null && !requestBody.getFilter().getUserName().isEmpty()) {

            whereCause.add("u.USER_NAME like '%" + requestBody.getFilter().getUserName() + "%'");

        }
        if (requestBody.getFilter().getStartTime() != null) {

            Date date = requestBody.getFilter().getStartTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);

            whereCause.add("h.HAND_START_TIME >= '" + strDate + "'");

        }
        if (requestBody.getFilter().getEndTime() != null) {

            Date date = requestBody.getFilter().getEndTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);

            whereCause.add("h.HAND_END_TIME <= '" + strDate + "'");

        }

//.... Get Total Statistics
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        Query jpaQueryTotal = entityManager.createNativeQuery(queryBuilder.toString());

        List<Object> resultTotal = jpaQueryTotal.getResultList();

        for (int i = 0; i < resultTotal.size(); i++) {

            Object[] item = (Object[]) resultTotal.get(i);

            EvaluateJudgeResponseModel record = new EvaluateJudgeResponseModel();
            try {

                record.setTime(Integer.parseInt(item[0].toString()));
                record.setTotal(Long.parseLong(item[1].toString()));
                record.setSeizure(Long.parseLong(item[2].toString()));
                record.setNoSeizure(Long.parseLong(item[3].toString()));
                record.setTotalJudge(Long.parseLong(item[4].toString()));
                record.setMissingReport(Long.parseLong(item[5].toString()));
                record.setMistakeReport(Long.parseLong(item[6].toString()));
                record.setArtificialJudge(Long.parseLong(item[7].toString()));
                record.setArtificialJudgeMissing(Long.parseLong(item[8].toString()));
                record.setArtificialJudgeMistake(Long.parseLong(item[9].toString()));
                record.setIntelligenceJudge(Long.parseLong(item[10].toString()));
                record.setIntelligenceJudgeMissing(Long.parseLong(item[11].toString()));
                record.setIntelligenceJudgeMistake(Long.parseLong(item[12].toString()));

                record.setMaxDuration(Double.parseDouble(item[13].toString()));
                record.setMinDuration(Double.parseDouble(item[14].toString()));
                record.setAvgDuration(Double.parseDouble(item[15].toString()));


                record.setMissingReportRate(record.getMissingReport() / (double) record.getTotal());
                record.setMistakeReportRate(record.getMistakeReport() / (double) record.getTotal());
                record.setArtificialJudgeMissingRate(record.getArtificialJudgeMissing() / (double) record.getArtificialJudge());
                record.setArtificialJudgeMistakeRate(record.getArtificialJudgeMistake() / (double) record.getArtificialJudge());
                record.setIntelligenceJudgeMissingRate(record.getIntelligenceJudgeMissing() / (double) record.getIntelligenceJudge());
                record.setIntelligenceJudgeMistakeRate(record.getIntelligenceJudgeMistake() / (double) record.getIntelligenceJudge());

            } catch (Exception e) {

            }

            response.setTotalStatistics(record);

        }

        //................. Get Detailed Statistics

        queryBuilder.append(" GROUP BY  " + groupBy + "(h.HAND_START_TIME)");

        Query jpaQuery = entityManager.createNativeQuery(queryBuilder.toString());


        List<Object> result = jpaQuery.getResultList();

        TreeMap<Integer, EvaluateJudgeResponseModel> data = new TreeMap<>();


        //init hash map
        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) {

        }

        for (Integer i = keyValueMin; i <= keyValueMax; i++) {
            EvaluateJudgeResponseModel item = new EvaluateJudgeResponseModel();
            item.setTime(i);
            data.put(i, item);
        }


        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);

            EvaluateJudgeResponseModel record = new EvaluateJudgeResponseModel();

            try {

                record.setTime(Integer.parseInt(item[0].toString()));
                record.setTotal(Long.parseLong(item[1].toString()));
                record.setSeizure(Long.parseLong(item[2].toString()));
                record.setNoSeizure(Long.parseLong(item[3].toString()));
                record.setTotalJudge(Long.parseLong(item[4].toString()));
                record.setMissingReport(Long.parseLong(item[5].toString()));
                record.setMistakeReport(Long.parseLong(item[6].toString()));
                record.setArtificialJudge(Long.parseLong(item[7].toString()));
                record.setArtificialJudgeMissing(Long.parseLong(item[8].toString()));
                record.setArtificialJudgeMistake(Long.parseLong(item[9].toString()));
                record.setIntelligenceJudge(Long.parseLong(item[10].toString()));
                record.setIntelligenceJudgeMissing(Long.parseLong(item[11].toString()));
                record.setIntelligenceJudgeMistake(Long.parseLong(item[12].toString()));

                record.setMaxDuration(Double.parseDouble(item[13].toString()));
                record.setMinDuration(Double.parseDouble(item[14].toString()));
                record.setAvgDuration(Double.parseDouble(item[15].toString()));


                record.setMissingReportRate(record.getMissingReport() / (double) record.getTotal());
                record.setMistakeReportRate(record.getMistakeReport() / (double) record.getTotal());
                record.setArtificialJudgeMissingRate(record.getArtificialJudgeMissing() / (double) record.getArtificialJudge());
                record.setArtificialJudgeMistakeRate(record.getArtificialJudgeMistake() / (double) record.getArtificialJudge());
                record.setIntelligenceJudgeMissingRate(record.getIntelligenceJudgeMissing() / (double) record.getIntelligenceJudge());
                record.setIntelligenceJudgeMistakeRate(record.getIntelligenceJudgeMistake() / (double) record.getIntelligenceJudge());

            } catch (Exception e) {

            }
            data.put(record.getTime(), record);

        }

        TreeMap<Integer, EvaluateJudgeResponseModel> sorted = new TreeMap<>();

//        data.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByKey())
//                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));

        for (Integer i = keyValueMin; i <= keyValueMax; i ++) {

            sorted.put(i, data.get(i));

        }


        TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics = new TreeMap<>();

        if (requestBody.getCurrentPage() != null && requestBody.getCurrentPage() != null && requestBody.getCurrentPage() > 0 && requestBody.getPerPage() > 0) {

            Integer from, to;
            from = (requestBody.getCurrentPage() - 1) * requestBody.getPerPage() + keyValueMin;
            to = requestBody.getCurrentPage() * requestBody.getPerPage() - 1 + keyValueMin;

            if (from < keyValueMin) {
                from  = keyValueMin;
            }

            if (to > keyValueMax) {
                to = keyValueMax;
            }

            response.setFrom(from);
            response.setTo(to);
            response.setPer_page(requestBody.getPerPage());
            response.setCurrent_page(requestBody.getCurrentPage());

            for (Integer i = from ; i <= to; i ++) {

                detailedStatistics.put(i, sorted.get(i));

            }

            response.setDetailedStatistics(detailedStatistics);

        }
        else {

            response.setDetailedStatistics(sorted);

        }

        try {
            response.setTotal(sorted.size());
            if (response.getTotal() % response.getPer_page() == 0) {
                response.setLast_page(response.getTotal() / response.getPer_page());
            }
            else {
                response.setLast_page(response.getTotal() / response.getPer_page() + 1);
            }

        } catch (Exception e) {  }

        return response;

    }

    @RequestMapping(value = "/statistics/get-judge-statistics", method = RequestMethod.POST)
    public Object getJudgeSummary(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, String> temp = new TreeMap<Integer, String>();

        JudgeStatisticsPaginationResponse response = new JudgeStatisticsPaginationResponse();
        response = getJudgeStatistics(requestBody);

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/statistics/get-suspicionhandgoods-statistics", method = RequestMethod.POST)
    public Object getSuspicionHandGoodsSummary(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SuspicionHandGoodsPaginationResponse response = new SuspicionHandGoodsPaginationResponse();
        response = getSuspicionHandGoodsStastistics(requestBody);
        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/statistics/get-handexamination-statistics", method = RequestMethod.POST)
    public Object getHandExaminationSummary(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        HandExaminationStatisticsPaginationResponse response = new HandExaminationStatisticsPaginationResponse();
        response = getHandStatistics(requestBody);
        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/statistics/get-evaluatejudge-statistics", method = RequestMethod.POST)
    public Object getEvaluateJudgeSummary(
            @RequestBody @Valid TaskManagementController.StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();
        response = getEvaluateJudgeStatistics(requestBody);
        return new CommonResponseBody(ResponseMessage.OK, response);

    }

}

