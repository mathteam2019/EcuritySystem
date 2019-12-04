package com.nuctech.ecuritycheckitem.controllers.taskmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.knowledgemanagement.KnowledgeDealManagementController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.PreviewStatisticsController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPersonalExcelView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPersonalPdfView;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
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

import javax.persistence.Query;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    /**
     * Statistics period width
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
     * Normal Statistics RequestBody
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

    /**
     * Get detailed info of task request body
     */
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

    /**
     * Get detailed info of a history task request body
     */
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
     * process task generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class TaskGenerateRequestBody {

        String idList;
        @NotNull
        Boolean isAll;

        TaskGenerateRequestBody.Filter filter;
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
     * Task table generate excel file request.
     */
    @RequestMapping(value = "/generate/process-task/export", method = RequestMethod.POST)
    public Object knowledgeCasePersonalGenerateExcelFile(@RequestBody @Valid KnowledgeDealManagementController.KnowledgeCaseGenerateRequestBody requestBody,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());


        //get all pending case deal list
        List<SerKnowledgeCaseDeal> dealList = StreamSupport
                .stream(serKnowledgeCaseDealRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SerKnowledgeCaseDeal> exportList = getExportList(dealList, requestBody.getIsAll(), requestBody.getIdList());


        InputStream inputStream = KnowledgeDealPersonalExcelView.buildExcelDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case personal generate pdf file request.
     */
    @RequestMapping(value = "/generate/personal/print", method = RequestMethod.POST)
    public Object knowledgeCasePersonalGenerateFile(@RequestBody @Valid KnowledgeDealManagementController.KnowledgeCaseGenerateRequestBody requestBody,
                                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        BooleanBuilder predicate = getPredicate(requestBody.getFilter());


        //get all pending case deal list
        List<SerKnowledgeCaseDeal> dealList = StreamSupport
                .stream(serKnowledgeCaseDealRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SerKnowledgeCaseDeal> exportList = getExportList(dealList, requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = KnowledgeDealPersonalPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
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
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.FALSE));

        TaskGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
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

}

