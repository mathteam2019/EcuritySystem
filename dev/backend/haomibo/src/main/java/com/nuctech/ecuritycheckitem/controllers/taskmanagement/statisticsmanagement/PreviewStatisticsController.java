package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.TaskManagementController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

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

    @RequestMapping(value = "/preview", method = RequestMethod.POST)
    public Object previewStatisticsGet(
            @RequestBody @Valid StatisticsRequestBody requestBody,
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

    @RequestMapping(value = "/scan", method = RequestMethod.POST)
    public Object scanStatisticsGet(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get Scan statistics
        ScanStatisticsResponse scanStatistics = getScanStatisticsForPreview(requestBody);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, scanStatistics));

        return value;

    }

    private ScanStatistics getScanStatisticsByDateForPreview(StatisticsRequestBody requestBody, Integer keyDate) {

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
                case TaskManagementController.StatisticWidth.HOUR:
                    predicateKeyDate = builder.scanStartTime.hour().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.DAY:
                    predicateKeyDate = builder.scanStartTime.dayOfMonth().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.WEEK:
                    predicateKeyDate = builder.scanStartTime.week().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.MONTH:
                    predicateKeyDate = builder.scanStartTime.month().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.QUARTER:
                    predicateKeyDate = builder.scanStartTime.month().eq(keyDate * 3);
                    break;
                case TaskManagementController.StatisticWidth.YEAR:
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

    private ScanStatisticsResponse getScanStatisticsForPreview(StatisticsRequestBody requestBody) {

        ScanStatistics totalStatistics = new ScanStatistics();

        TreeMap<Integer, ScanStatistics> detailedStatistics = new TreeMap<Integer, ScanStatistics>();

        totalStatistics = getScanStatisticsByDateForPreview(requestBody, null);

        ScanStatisticsResponse response = new ScanStatisticsResponse();

        int keyValueMin = 0, keyValueMax = -1;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case TaskManagementController.StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    response.setTotal(24);
                    break;
                case TaskManagementController.StatisticWidth.DAY:
                    keyValueMin = 1;
                    keyValueMax = 31;
                    response.setTotal(31);
                    break;
                case TaskManagementController.StatisticWidth.WEEK:
                    keyValueMin = 1;
                    keyValueMax = 5;
                    response.setTotal(5);
                    break;
                case TaskManagementController.StatisticWidth.MONTH:
                    keyValueMin = 1;
                    keyValueMax = 12;
                    response.setTotal(12);
                    break;
                case TaskManagementController.StatisticWidth.QUARTER:
                    keyValueMin = 1;
                    keyValueMax = 4;
                    response.setTotal(4);
                    break;
                case TaskManagementController.StatisticWidth.YEAR:
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

        int curPage = 0;
        int perPage = 0;

        try {
            curPage = requestBody.getCurrentPage();
            perPage = requestBody.getPerPage();
        }
        catch (Exception e) {

        }

        int startIndex = keyValueMin;
        int endIndex = keyValueMax;

        if (curPage != 0 && perPage != 0) {
            startIndex = (curPage - 1) * perPage + keyValueMin;
            endIndex = (curPage) * perPage + keyValueMin - 1;

            if (requestBody.getFilter().getStatWidth().equals(TaskManagementController.StatisticWidth.YEAR)) {
                startIndex = keyValueMin + startIndex - 1;
                endIndex = startIndex + perPage - 1;
            }

            if (startIndex < keyValueMin) {
                startIndex = keyValueMin;
            }
            if (endIndex > keyValueMax) {
                endIndex = keyValueMax;
            }


            if (requestBody.getFilter().getStatWidth().equals(TaskManagementController.StatisticWidth.YEAR)) {
                response.setFrom(startIndex - keyValueMin + 1);
                response.setTo(endIndex - keyValueMin + 1);
            } else {
                response.setFrom(startIndex);
                response.setTo(endIndex);
            }
        }

        int i = 0;
        for (i = startIndex; i <= endIndex; i++) {
            ScanStatistics scanStat = getScanStatisticsByDateForPreview(requestBody, i);

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


    private JudgeStatisticsResponse getJudgeStatisticsForPreview(StatisticsRequestBody requestBody) {

        JudgeStatisticsModelForPreview totalStatistics = new JudgeStatisticsModelForPreview();

        TreeMap<Integer, JudgeStatisticsModelForPreview> detailedStatistics = new TreeMap<Integer, JudgeStatisticsModelForPreview>();

        totalStatistics = getJudgeStatisticsByDateForPreview(requestBody, null);

        JudgeStatisticsResponse response = new JudgeStatisticsResponse();

        int keyValueMin = 0, keyValueMax = -1;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case TaskManagementController.StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    response.setTotal(24);
                    break;
                case TaskManagementController.StatisticWidth.DAY:
                    keyValueMin = 1;
                    keyValueMax = 31;
                    response.setTotal(31);
                    break;
                case TaskManagementController.StatisticWidth.WEEK:
                    keyValueMin = 1;
                    keyValueMax = 5;
                    response.setTotal(5);
                    break;
                case TaskManagementController.StatisticWidth.MONTH:
                    keyValueMin = 1;
                    keyValueMax = 12;
                    response.setTotal(12);
                    break;
                case TaskManagementController.StatisticWidth.QUARTER:
                    keyValueMin = 1;
                    keyValueMax = 4;
                    response.setTotal(4);
                    break;
                case TaskManagementController.StatisticWidth.YEAR:
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


        int curPage = 0;
        int perPage = 0;

        try {
            curPage = requestBody.getCurrentPage();
            perPage = requestBody.getPerPage();
        }
        catch (Exception e) {

        }

        int startIndex = keyValueMin;
        int endIndex = keyValueMax;

        if (curPage != 0 && perPage != 0) {

            startIndex = (curPage - 1) * perPage + 1;
            endIndex = (curPage) * perPage;

            if (requestBody.getFilter().getStatWidth().equals(TaskManagementController.StatisticWidth.YEAR)) {
                startIndex = keyValueMin + startIndex - 1;
                endIndex = startIndex + perPage - 1;
            }

            if (startIndex < keyValueMin) {
                startIndex = keyValueMin;
            }
            if (endIndex > keyValueMax) {
                endIndex = keyValueMax;
            }

            if (requestBody.getFilter().getStatWidth().equals(TaskManagementController.StatisticWidth.YEAR)) {
                response.setFrom(startIndex - keyValueMin + 1);
                response.setTo(endIndex - keyValueMin + 1);
            } else {
                response.setFrom(startIndex);
                response.setTo(endIndex);
            }
        }

        int i = 0;
        for (i = startIndex; i <= endIndex; i++) {
            JudgeStatisticsModelForPreview judgeStat = getJudgeStatisticsByDateForPreview(requestBody, i);

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

    private JudgeStatisticsModelForPreview getJudgeStatisticsByDateForPreview(StatisticsRequestBody requestBody, Integer keyDate) {

        JudgeStatisticsModelForPreview judgeStatistics = new JudgeStatisticsModelForPreview();

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
                case TaskManagementController.StatisticWidth.HOUR:
                    predicateKeyDate = builder.judgeStartTime.hour().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.DAY:
                    predicateKeyDate = builder.judgeStartTime.dayOfMonth().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.WEEK:
                    predicateKeyDate = builder.judgeStartTime.week().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.MONTH:
                    predicateKeyDate = builder.judgeStartTime.month().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.QUARTER:
                    predicateKeyDate = builder.judgeStartTime.month().eq(keyDate * 3);
                    break;
                case TaskManagementController.StatisticWidth.YEAR:
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


    private HandExaminationStatisticsResponse getHandExaminationStatisticsForPreview(StatisticsRequestBody requestBody) {

        HandExaminationStatisticsForPreview totalStatistics = new HandExaminationStatisticsForPreview();

        TreeMap<Integer, HandExaminationStatisticsForPreview> detailedStatistics = new TreeMap<Integer, HandExaminationStatisticsForPreview>();

        totalStatistics = getHandExaminationStatisticsByDateForPreview(requestBody, null);

        HandExaminationStatisticsResponse response = new HandExaminationStatisticsResponse();


        int keyValueMin = 0, keyValueMax = -1;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case TaskManagementController.StatisticWidth.HOUR:

                    keyValueMin = 0;
                    keyValueMax = 23;
                    response.setTotal(24);
                    break;

                case TaskManagementController.StatisticWidth.DAY:

                    keyValueMin = 1;
                    keyValueMax = 31;
                    response.setTotal(31);
                    break;

                case TaskManagementController.StatisticWidth.WEEK:

                    keyValueMin = 1;
                    keyValueMax = 5;
                    response.setTotal(5);
                    break;

                case TaskManagementController.StatisticWidth.MONTH:

                    keyValueMin = 1;
                    keyValueMax = 12;
                    response.setTotal(12);

                    break;

                case TaskManagementController.StatisticWidth.QUARTER:

                    keyValueMin = 1;
                    keyValueMax = 4;
                    response.setTotal(4);
                    break;

                case TaskManagementController.StatisticWidth.YEAR:

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


        int curPage = 0;
        int perPage = 0;

        try {
            curPage = requestBody.getCurrentPage();
            perPage = requestBody.getPerPage();
        }
        catch (Exception e) {

        }

        int startIndex = keyValueMin;
        int endIndex = keyValueMax;

        if (curPage != 0 && perPage != 0) {

            startIndex = (curPage - 1) * perPage + 1;
            endIndex = (curPage) * perPage;

            if (requestBody.getFilter().getStatWidth().equals(TaskManagementController.StatisticWidth.YEAR)) {
                startIndex = keyValueMin + startIndex - 1;
                endIndex = startIndex + perPage - 1;
            }

            if (startIndex < keyValueMin) {
                startIndex = keyValueMin;
            }
            if (endIndex > keyValueMax) {
                endIndex = keyValueMax;
            }

            if (requestBody.getFilter().getStatWidth().equals(TaskManagementController.StatisticWidth.YEAR)) {
                response.setFrom(startIndex - keyValueMin + 1);
                response.setTo(endIndex - keyValueMin + 1);
            } else {
                response.setFrom(startIndex);
                response.setTo(endIndex);
            }
        }

        int i = 0;
        for (i = startIndex; i <= endIndex; i++) {
            HandExaminationStatisticsForPreview handExaminationStat = getHandExaminationStatisticsByDateForPreview(requestBody, i);

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

    private HandExaminationStatisticsForPreview getHandExaminationStatisticsByDateForPreview(StatisticsRequestBody requestBody, Integer keyDate) {

        HandExaminationStatisticsForPreview handExaminationStatistics = new HandExaminationStatisticsForPreview();

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
                case TaskManagementController.StatisticWidth.HOUR:
                    predicateKeyDate = builder.handStartTime.hour().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.DAY:
                    predicateKeyDate = builder.handStartTime.dayOfMonth().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.WEEK:
                    predicateKeyDate = builder.handStartTime.week().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.MONTH:
                    predicateKeyDate = builder.handStartTime.month().eq(keyDate);
                    break;
                case TaskManagementController.StatisticWidth.QUARTER:
                    predicateKeyDate = builder.handStartTime.month().eq(keyDate * 3);
                    break;
                case TaskManagementController.StatisticWidth.YEAR:
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


    private TotalStatistics getPreviewStatisticsByDate(StatisticsRequestBody requestBody, Integer keyDate) {

        ScanStatistics scanStatistics = getScanStatisticsByDateForPreview(requestBody, keyDate);
        JudgeStatisticsModelForPreview judgeStatistics = getJudgeStatisticsByDateForPreview(requestBody, keyDate);
        HandExaminationStatisticsForPreview handExaminationStatistics = getHandExaminationStatisticsByDateForPreview(requestBody, keyDate);

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

    private TotalStatisticsResponse getPreviewStatistics(StatisticsRequestBody requestBody) {

        TotalStatistics totalStatistics = new TotalStatistics();

        TreeMap<Long, TotalStatistics> detailedStatistics = new TreeMap<Long, TotalStatistics>();

        totalStatistics = getPreviewStatisticsByDate(requestBody, null);

        TotalStatisticsResponse response = new TotalStatisticsResponse();

        int keyValueMin = 0, keyValueMax = 0;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case TaskManagementController.StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    response.setTotal(24);
                    break;
                case TaskManagementController.StatisticWidth.DAY:
                    keyValueMin = 1;
                    keyValueMax = 31;
                    response.setTotal(31);
                    break;
                case TaskManagementController.StatisticWidth.WEEK:
                    keyValueMin = 1;
                    keyValueMax = 5;
                    response.setTotal(5);
                    break;
                case TaskManagementController.StatisticWidth.MONTH:
                    keyValueMin = 1;
                    keyValueMax = 12;
                    response.setTotal(12);
                    break;
                case TaskManagementController.StatisticWidth.QUARTER:
                    keyValueMin = 1;
                    keyValueMax = 4;
                    response.setTotal(4);
                    break;
                case TaskManagementController.StatisticWidth.YEAR:
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

        int curPage = 0;
        int perPage = 0;

        try {

            curPage = requestBody.getCurrentPage();
            perPage = requestBody.getPerPage();

        }
        catch(Exception e) {

        }


        int startIndex = keyValueMin;
        int endIndex = keyValueMax;

        if (curPage != 0 && perPage != 0) {

            startIndex = (curPage - 1) * perPage + keyValueMin;
            endIndex = (curPage) * perPage + keyValueMin - 1;

            if (requestBody.getFilter().getStatWidth().equals(TaskManagementController.StatisticWidth.YEAR)) {
                startIndex = keyValueMin + startIndex - 1;
                endIndex = startIndex + perPage - 1;
            }

            if (startIndex < keyValueMin) {
                startIndex = keyValueMin;
            }
            if (endIndex > keyValueMax) {
                endIndex = keyValueMax;
            }

            if (requestBody.getFilter().getStatWidth().equals(TaskManagementController.StatisticWidth.YEAR)) {
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
            detailedStatistics.put((long) i, totalStat);
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
}
