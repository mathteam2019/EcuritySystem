package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.HandExaminationStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.SuspictionHandgoodsStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.SuspictionHandgoodsStatisticsPdfView;
import com.nuctech.ecuritycheckitem.models.db.QHistory;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.SuspicionHandGoodsPaginationResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.*;
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
public class SuspicionHandgoodsStatisticsController extends BaseController {

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

    public static final List<String> handGoodsIDList = Arrays.asList(new String[]{
            "1000001601",
            "1000001602",
            "1000001603",
            "1000001604",
            "1000001605"
    });


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

    @RequestMapping(value = "/get-suspicionhandgoods-statistics", method = RequestMethod.POST)
    public Object getSuspicionHandGoodsSummary(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SuspicionHandGoodsPaginationResponse response = new SuspicionHandGoodsPaginationResponse();
        //response = getSuspicionHandGoodsStastistics(requestBody);

        response = suspictionHandgoodsStatisticsService.getStatistics(
                requestBody.getFilter().getFieldId(),
                requestBody.getFilter().getDeviceId(),
                requestBody.getFilter().getUserCategory(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getStartTime(),
                requestBody.getFilter().getEndTime(),
                requestBody.getFilter().getStatWidth(),
                requestBody.getCurrentPage(),
                requestBody.getPerPage());

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * Preview Statistics generate pdf file request.
     */
    @RequestMapping(value = "/suspiciongoods/generate/print", method = RequestMethod.POST)
    public Object suspicionGoodsStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, TreeMap<String, Long>> totalStatistics = suspictionHandgoodsStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, TreeMap<String, Long>> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        HandExaminationStatisticsPdfView.setResource(res);
        InputStream inputStream = SuspictionHandgoodsStatisticsPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=suspicionGoodsStatistics.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Scan Statistics generate pdf file request.
     */
    @RequestMapping(value = "/suspiciongoods/generate/export", method = RequestMethod.POST)
    public Object suspicioGoodsStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, TreeMap<String, Long>> totalStatistics = suspictionHandgoodsStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, TreeMap<String, Long>> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = SuspictionHandgoodsStatisticsExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=suspicionGoodsStatistics.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    private TreeMap<Integer, TreeMap<String, Long>> getExportList(TreeMap<Integer, TreeMap<String, Long>> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, TreeMap<String, Long>> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(",");

            for (Map.Entry<Integer, TreeMap<String, Long>> entry : detailedStatistics.entrySet()) {

                TreeMap<String, Long> record = entry.getValue();

                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(Long.toString(record.get("time")))) {
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


    public SuspicionHandGoodsPaginationResponse getSuspicionHandGoodsStastistics(StatisticsRequestBody requestBody) {
        SuspicionHandGoodsPaginationResponse response = new SuspicionHandGoodsPaginationResponse();

        TreeMap<String, Long> totalStatistics = new TreeMap<>();
        totalStatistics = getSuspicionHandGoodsByDate(requestBody, null);
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        Integer startIndex = 0, endIndex = 0, keyValueMin = 0, keyValueMax = 0;
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }

        int curPage = 0;
        int perPage = 0;
        try {
            curPage = requestBody.getCurrentPage();
            perPage = requestBody.getPerPage();
        } catch (Exception e) { }

        if (curPage != 0 && perPage != 0) {
            startIndex = (curPage - 1) * perPage + keyValueMin;
            endIndex = (curPage) * perPage - 1 + keyValueMin;

            if (startIndex < keyValueMin) {
                startIndex = keyValueMin;
            }
            if (endIndex > keyValueMax) {
                endIndex = keyValueMax;
            }
            response.setFrom((curPage - 1) * perPage + 1);
            response.setTo((curPage) * perPage);
            try {
                response.setTotal(keyValueMax - keyValueMin + 1);
                response.setPer_page(requestBody.getPerPage());
                response.setCurrent_page(requestBody.getCurrentPage());

                if (response.getTotal() % response.getPer_page() == 0) {
                    response.setLast_page(response.getTotal() / response.getPer_page());
                } else {
                    response.setLast_page(response.getTotal() / response.getPer_page() + 1);
                }
            } catch (Exception e) { }
        }
        else {
            startIndex = keyValueMin;
            endIndex = keyValueMax;
        }

        TreeMap<Integer, TreeMap<String, Long>> detailedStatistics = new TreeMap<>();
        for (Integer i = startIndex; i <= endIndex; i++) {
            TreeMap<String, Long> suspictionStat = getSuspicionHandGoodsByDate(requestBody, i);
            suspictionStat.put("time", (long) i);
            detailedStatistics.put(i, suspictionStat);
        }

        response.setTotalStatistics(totalStatistics);
        response.setDetailedStatistics(detailedStatistics);
        return response;
    }

    private Predicate getTimePredicate(StatisticsRequestBody requestBody, Integer byDate) {
        Predicate predicateTime = null;
        QHistory history = QHistory.history;

        if (byDate != null) {
            String statWidth = "";
            try {
                statWidth = requestBody.getFilter().getStatWidth();
            } catch (Exception e) { }

            if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {

                switch (statWidth) {
                    case Constants.StatisticWidth.YEAR:
                        predicateTime = (history.handStartTime.year().eq(byDate));
                        break;
                    case Constants.StatisticWidth.MONTH:
                        predicateTime = (history.handStartTime.month().eq(byDate));
                        break;
                    case Constants.StatisticWidth.DAY:
                        predicateTime = (history.handStartTime.dayOfMonth().eq(byDate));
                        break;
                    case Constants.StatisticWidth.HOUR:
                        predicateTime = (history.handStartTime.hour().eq(byDate));
                        break;
                    case Constants.StatisticWidth.WEEK:
                        predicateTime = (history.handStartTime.dayOfMonth().between((byDate - 1) * 7, byDate * 7));
                        break;
                    case Constants.StatisticWidth.QUARTER:
                        predicateTime = (history.handStartTime.month().between((byDate - 1) * 3, (byDate) * 3));
                        break;
                }
            }
        }
        return predicateTime;
    }

    private TreeMap<String, Long> getSuspicionHandGoodsByDate(StatisticsRequestBody requestBody, Integer byDate) {

        TreeMap<String, Long> suspicionResult = new TreeMap<>();
        QHistory history = QHistory.history;
        StatisticsRequestBody.Filter filter = requestBody.getFilter();

        for (int i = 0; i < handGoodsIDList.size(); i++) {
            BooleanBuilder predicate = new BooleanBuilder(history.isNotNull());
            predicate.and(getTimePredicate(requestBody, byDate));
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

    /**
     * Private purpose Only
     * Get Start KeyDate and End Key Date for statistics
     * <p>
     * Ex: In case of Hour - it returns [1, 24], In case of Month it returns [1, 12]
     *
     * @param requestBody
     * @return [startKeyDate, endKeyDate]
     */
    public List<Integer> getKeyValuesforStatistics(StatisticsRequestBody requestBody) {

        Integer keyValueMin = 1, keyValueMax = 0;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case Constants.StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    break;
                case Constants.StatisticWidth.DAY:
                    keyValueMax = 31;
                    break;
                case Constants.StatisticWidth.WEEK:
                    keyValueMax = 5;
                    break;
                case Constants.StatisticWidth.MONTH:
                    keyValueMax = 12;
                    break;
                case Constants.StatisticWidth.QUARTER:
                    keyValueMax = 4;
                    break;
                case Constants.StatisticWidth.YEAR:
                    Map<String, Integer> availableYearRage = getAvailableYearRange(requestBody);
                    keyValueMax = availableYearRage.get("max");
                    keyValueMin = availableYearRage.get("min");
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

    private Map<String, Integer> getAvailableYearRange(StatisticsRequestBody requestBody) {

        Integer keyValueMin = 0, keyValueMax = 0;

        Integer yearMax = serJudgeGraphRepository.findMaxYear();
        Integer yearMin = 1970;
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

        Map<String, Integer> result = new HashMap<String, Integer>();

        result.put("min", keyValueMin);
        result.put("max", keyValueMax);

        return result;
    }

}
