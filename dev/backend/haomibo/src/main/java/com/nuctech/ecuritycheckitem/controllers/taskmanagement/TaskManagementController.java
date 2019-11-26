package com.nuctech.ecuritycheckitem.controllers.taskmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.StatisticWidth;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task")
public class TaskManagementController extends BaseController {


    /**
     * Statistics Response Body
     */
    @Getter
    @Setter
    class TotalStatistics {

        String axisLabel;
        long invalidScan;
        long validScan;
        long passedScan;
        long alarmScan;
        long totalScan;
        double invalidScanRate;
        long totalJudge;
        long totalHandExamination;
        long noSuspictionJudge;
        double noSuspictionJudgeRate;
        long seizureHandExamination;
        double seizureHandExaminationRate;
        long noSeizureHandExamination;
        double noSeizureHandExaminationRate;

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
            Date startTime;
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
            Date startTime;
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
    private static class PreviewStatisticsRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            Long fieldId;
            Long deviceId;
            String userCategory;
            String userName;
            Date startTime;
            Date endTime;
            String statWidth;

        }

        PreviewStatisticsRequestBody.Filter filter;

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

        if(!optionalTask.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(!optionalTask.get().getSerScan().getScanInvalid().equals(SerScan.Invalid.TRUE)) {
            return new CommonResponseBody(ResponseMessage.INVALID_SCANID);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, optionalTask.get()));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskId", "taskNumber", "taskStatus", "field", "serScan", "serJudgeGraph", "serHandExamination"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"))
                .addFilter(ModelJsonFilters.FILTER_SER_SCAN, SimpleBeanPropertyFilter.filterOutAllExcept("scanImage", "scanDevice", "scanAtrResult", "scanFootAlarm", "scanAssignTimeout", "scanPointsman", "scanStartTime", "scanEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_JUDGE_GRAPH, SimpleBeanPropertyFilter.filterOutAllExcept("judgeDevice", "judgeResult", "judgeTimeout","judgeUser", "judgeStartTime", "judgeEndTime"))
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
                .addFilter(ModelJsonFilters.FILTER_SER_JUDGE_GRAPH, SimpleBeanPropertyFilter.filterOutAllExcept("judgeDevice","judgeUser", "judgeStartTime", "judgeEndTime"))
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

        if(!optionalHistory.isPresent()) {
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

        if(!optionalTask.isPresent()) {
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
                .addFilter(ModelJsonFilters.FILTER_SER_JUDGE_GRAPH, SimpleBeanPropertyFilter.filterOutAllExcept("judgeDevice","judgeUser", "judgeStartTime", "judgeEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SER_HAND_EXAMINATION, SimpleBeanPropertyFilter.filterOutAllExcept("handDevice", "handUser", "handStartTime", "handEndTime"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW, SimpleBeanPropertyFilter.filterOutAllExcept("workMode"))
                .addFilter(ModelJsonFilters.FILTER_SER_IMAGE, SimpleBeanPropertyFilter.filterOutAllExcept("imageUrl", "imageLabel"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.filterOutAllExcept("userName"));
        value.setFilters(filters);

        return value;
    }

    private ScanStatistics getScanStatistics(PreviewStatisticsRequestBody requestBody) {

        ScanStatistics scanStatistics =  new ScanStatistics();

        QSerScan builder = QSerScan.serScan;

        Date dateFrom = requestBody.getFilter().getStartTime();
        Date dateTo = requestBody.getFilter().getEndTime();

        Predicate predicateDate;
        Predicate predicateField = null;
        Predicate predicateDevice = null;
        Predicate predicateUsername = null;
        Predicate predicateUserCategory = null;
        Predicate predicateStatisticWidth = null;


        if (dateFrom == null && dateTo == null) {
            predicateDate = null;
        }
        else if (dateFrom != null && dateTo == null) {
            predicateDate = builder.scanStartTime.after(dateFrom);
        }
        else if (dateFrom == null && dateTo != null) {
            predicateDate = builder.scanEndTime.before(dateTo);
        }
        else {
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

        BooleanBuilder predicateValid = new BooleanBuilder(builder.isNotNull());
        predicateValid.and(builder.scanInvalid.eq(SerScan.Invalid.TRUE));
        predicateValid.and(predicateDate);
        predicateValid.and(predicateField);
        predicateValid.and(predicateDevice);
        predicateValid.and(predicateUsername);
        predicateValid.and(predicateUserCategory);


        BooleanBuilder predicateInvalid = new BooleanBuilder(builder.isNotNull());
        predicateInvalid.and(builder.scanInvalid.eq(SerScan.Invalid.FALSE));
        predicateInvalid.and(predicateDate);
        predicateInvalid.and(predicateField);
        predicateInvalid.and(predicateDevice);
        predicateInvalid.and(predicateUsername);
        predicateInvalid.and(predicateUserCategory);


        BooleanBuilder predicatePassed = new BooleanBuilder(builder.isNotNull());
        predicatePassed.and(builder.scanAtrResult.eq(SerScan.ATRResult.TRUE));
        predicatePassed.and(predicateDate);
        predicatePassed.and(predicateField);
        predicatePassed.and(predicateDevice);
        predicatePassed.and(predicateUsername);
        predicatePassed.and(predicateUserCategory);

        BooleanBuilder predicateAlarm = new BooleanBuilder(builder.isNotNull());
        predicateAlarm.and(builder.scanAtrResult.eq(SerScan.FootAlarm.TRUE));
        predicateAlarm.and(predicateDate);
        predicateAlarm.and(predicateField);
        predicateAlarm.and(predicateDevice);
        predicateAlarm.and(predicateUsername);
        predicateAlarm.and(predicateUserCategory);

        long totalScan = serScanRepository.count(predicateTotal);
        long validScan = serScanRepository.count(predicateValid);
        long invalidScan = serScanRepository.count(predicateInvalid);
        long passedScan = serScanRepository.count(predicatePassed);
        long alarmScan = serScanRepository.count(predicateAlarm);

        scanStatistics.setTotalScan(totalScan);
        scanStatistics.setValidScan(validScan);
        scanStatistics.setValidScanRate(validScan / (double)totalScan);
        scanStatistics.setInvalidScan(invalidScan);
        scanStatistics.setInvalidScanRate(invalidScan / (double)totalScan);
        scanStatistics.setPassedScan(passedScan);
        scanStatistics.setPassedScanRate(passedScan / (double)totalScan);
        scanStatistics.setAlarmScan(alarmScan);
        scanStatistics.setAlarmScanRate(alarmScan/ (double)totalScan);

        return scanStatistics;

    }

    private JudgeStatistics getJudgeStatistics(PreviewStatisticsRequestBody requestBody) {

        JudgeStatistics judgeStatistics =  new JudgeStatistics();

        QSerJudgeGraph builder = QSerJudgeGraph.serJudgeGraph;

        Date dateFrom = requestBody.getFilter().getStartTime();
        Date dateTo = requestBody.getFilter().getEndTime();

        Predicate predicateDate;
        Predicate predicateField = null;
        Predicate predicateDevice = null;
        Predicate predicateUsername = null;
        Predicate predicateUserCategory = null;
        Predicate predicateStatisticWidth = null;

        if (dateFrom == null && dateTo == null) {
            predicateDate = null;
        }
        else if (dateFrom != null && dateTo == null) {
            predicateDate = builder.judgeStartTime.after(dateFrom);
        }
        else if (dateFrom == null && dateTo != null) {
            predicateDate = builder.judgeEndTime.before(dateTo);
        }
        else {
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


        BooleanBuilder predicateNoSuspiction = new BooleanBuilder(builder.isNotNull());
        predicateNoSuspiction.and(builder.judgeResult.eq(SerJudgeGraph.Result.TRUE));
        predicateNoSuspiction.and(predicateDate);
        predicateNoSuspiction.and(predicateField);
        predicateNoSuspiction.and(predicateDevice);
        predicateNoSuspiction.and(predicateUsername);
        predicateNoSuspiction.and(predicateUserCategory);

        BooleanBuilder predicateSuspiction = new BooleanBuilder(builder.isNotNull());
        predicateSuspiction.and(builder.judgeResult.eq(SerJudgeGraph.Result.FALSE));
        predicateSuspiction.and(predicateDate);
        predicateSuspiction.and(predicateField);
        predicateSuspiction.and(predicateDevice);
        predicateSuspiction.and(predicateUsername);
        predicateSuspiction.and(predicateUserCategory);

        long totalJudge = serJudgeGraphRepository.count(predicateTotal);
        long noSuspictionJudge = serJudgeGraphRepository.count(predicateNoSuspiction);
        long suspictionJudge = serJudgeGraphRepository.count(predicateSuspiction);

        judgeStatistics.setTotalJudge(totalJudge);
        judgeStatistics.setNoSuspictionJudge(noSuspictionJudge);
        judgeStatistics.setNoSuspictionJudgeRate(noSuspictionJudge / (double)totalJudge);
        judgeStatistics.setSuspictionJudge(suspictionJudge);
        judgeStatistics.setNoSuspictionJudgeRate(suspictionJudge/ (double)totalJudge);

        return judgeStatistics;

    }

    private HandExaminationStatistics getHandExaminationStatistics(PreviewStatisticsRequestBody requestBody) {

        HandExaminationStatistics handExaminationStatistics = new HandExaminationStatistics();

        QSerHandExamination builder = QSerHandExamination.serHandExamination;

        Date dateFrom = requestBody.getFilter().getStartTime();
        Date dateTo = requestBody.getFilter().getEndTime();

        Predicate predicateDate;
        Predicate predicateField = null;
        Predicate predicateDevice = null;
        Predicate predicateUsername = null;
        Predicate predicateUserCategory = null;
        Predicate predicateStatisticWidth = null;

        if (dateFrom == null && dateTo == null) {
            predicateDate = null;
        }
        else if (dateFrom != null && dateTo == null) {
            predicateDate = builder.handStartTime.after(dateFrom);
        }
        else if (dateFrom == null && dateTo != null) {
            predicateDate = builder.handEndTime.before(dateTo);
        }
        else {
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

        BooleanBuilder predicteSeizure = new BooleanBuilder(builder.isNotNull());
        predicteSeizure.and(builder.handResult.eq(SerJudgeGraph.Result.TRUE));
        predicteSeizure.and(predicateDate);
        predicteSeizure.and(predicateField);
        predicteSeizure.and(predicateDevice);
        predicteSeizure.and(predicateUsername);
        predicteSeizure.and(predicateUserCategory);

        BooleanBuilder predicteNoSeizure = new BooleanBuilder(builder.isNotNull());
        predicteNoSeizure.and(builder.handResult.eq(SerJudgeGraph.Result.FALSE));
        predicteNoSeizure.and(predicateDate);
        predicteNoSeizure.and(predicateField);
        predicteNoSeizure.and(predicateDevice);
        predicteNoSeizure.and(predicateUsername);
        predicteNoSeizure.and(predicateUserCategory);

        long totalHandExam = serHandExaminationRepository.count(predicateTotal);
        long seizureHandExam = serHandExaminationRepository.count(predicteSeizure);
        long noSeizureHandExam = serHandExaminationRepository.count(predicteNoSeizure);

        handExaminationStatistics.setTotalHandExamination(totalHandExam);
        handExaminationStatistics.setSeizureHandExamination(seizureHandExam);
        handExaminationStatistics.setSeizureHandExaminationRate(seizureHandExam / (double)totalHandExam);
        handExaminationStatistics.setNoSeizureHandExamination(noSeizureHandExam);
        handExaminationStatistics.setNoSeizureHandExaminationRate(noSeizureHandExam / (double)totalHandExam);

        return handExaminationStatistics;

    }

    @RequestMapping(value = "/statistics/preview", method = RequestMethod.POST)
    public Object previewStatisticsGet(
            @RequestBody @Valid TaskManagementController.PreviewStatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Date dateFrom, dateTo;
        dateFrom = requestBody.getFilter().getStartTime();
        dateTo = requestBody.getFilter().getEndTime();

        TotalStatistics totalStatistics = new TotalStatistics();

        //get Scan statistics
        ScanStatistics scanStatiscs = getScanStatistics(requestBody);

        totalStatistics.setTotalScan(scanStatiscs.getTotalScan());
        totalStatistics.setValidScan(scanStatiscs.getValidScan());
        totalStatistics.setPassedScan(scanStatiscs.getPassedScan());
        totalStatistics.setInvalidScan(scanStatiscs.getInvalidScan());
        totalStatistics.setAlarmScan(scanStatiscs.getAlarmScan());

        //get Judge statistics
        JudgeStatistics judgeStatistics = getJudgeStatistics(requestBody);
        totalStatistics.setTotalJudge(judgeStatistics.getTotalJudge());
        totalStatistics.setNoSuspictionJudge(judgeStatistics.getNoSuspictionJudge());

        //get Hand Examination Statistics
        HandExaminationStatistics handExaminationStatistics = getHandExaminationStatistics(requestBody);
        totalStatistics.setTotalHandExamination(handExaminationStatistics.getTotalHandExamination());
        totalStatistics.setSeizureHandExamination(handExaminationStatistics.getSeizureHandExamination());
        totalStatistics.setNoSeizureHandExamination(handExaminationStatistics.getNoSeizureHandExamination());

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, totalStatistics));

        return value;

    }

    @RequestMapping(value = "/statistics/scan", method = RequestMethod.POST)
    public Object scanStatisticsGet(
            @RequestBody @Valid TaskManagementController.PreviewStatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Date dateFrom, dateTo;
        dateFrom = requestBody.getFilter().getStartTime();
        dateTo = requestBody.getFilter().getEndTime();


        //get Scan statistics
        ScanStatistics scanStatiscs = getScanStatistics(requestBody);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, scanStatiscs));

        return value;

    }

    @RequestMapping(value = "/statistics/judgegraph", method = RequestMethod.POST)
    public Object judgegraphStatisticsGet(
            @RequestBody @Valid TaskManagementController.PreviewStatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Date dateFrom, dateTo;
        dateFrom = requestBody.getFilter().getStartTime();
        dateTo = requestBody.getFilter().getEndTime();


        //get Scan statistics
        JudgeStatistics judgeStatistics = getJudgeStatistics(requestBody);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, judgeStatistics));

        return value;

    }

    @RequestMapping(value = "/statistics/handexamination", method = RequestMethod.POST)
    public Object handexaminationStatisticsGet(
            @RequestBody @Valid TaskManagementController.PreviewStatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Date dateFrom, dateTo;
        dateFrom = requestBody.getFilter().getStartTime();
        dateTo = requestBody.getFilter().getEndTime();


        //get Scan statistics
        HandExaminationStatistics handExaminationStatistics = getHandExaminationStatistics(requestBody);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, handExaminationStatistics));

        return value;

    }
}
