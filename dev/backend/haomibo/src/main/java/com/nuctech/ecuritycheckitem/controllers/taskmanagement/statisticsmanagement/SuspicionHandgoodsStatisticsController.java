package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.TaskManagementController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.db.QHistory;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.SuspicionHandGoodsPaginationResponse;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    List<String> handGoodsIDList = Arrays.asList(new String[]{
            "1000001601",
            "1000001602",
            "1000001603",
            "1000001604",
            "1000001605"
    });


    @RequestMapping(value = "/get-suspicionhandgoods-statistics", method = RequestMethod.POST)
    public Object getSuspicionHandGoodsSummary(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SuspicionHandGoodsPaginationResponse response = new SuspicionHandGoodsPaginationResponse();
        response = getSuspicionHandGoodsStastistics(requestBody);
        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    public SuspicionHandGoodsPaginationResponse getSuspicionHandGoodsStastistics(StatisticsRequestBody requestBody) {

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

    private TreeMap<String, Long> getSuspicionHandGoodsByDate(StatisticsRequestBody requestBody, Integer byDate) {

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
                        case TaskManagementController.StatisticWidth.YEAR:
                            predicate.and(history.handStartTime.year().eq(byDate));
                            break;
                        case TaskManagementController.StatisticWidth.MONTH:
                            predicate.and(history.handStartTime.month().eq(byDate));
                            break;
                        case TaskManagementController.StatisticWidth.DAY:
                            predicate.and(history.handStartTime.dayOfMonth().eq(byDate));
                            break;
                        case TaskManagementController.StatisticWidth.HOUR:
                            predicate.and(history.handStartTime.hour().eq(byDate));
                            break;
                        case TaskManagementController.StatisticWidth.WEEK:
                            predicate.and( history.handStartTime.dayOfMonth().between((byDate - 1) * 7, byDate * 7));
                            break;
                        case TaskManagementController.StatisticWidth.QUARTER:
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

        Integer keyValueMin = 0, keyValueMax = 0;

        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case TaskManagementController.StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    break;
                case TaskManagementController.StatisticWidth.DAY:
                    keyValueMin = 1;
                    keyValueMax = 31;
                    break;
                case TaskManagementController.StatisticWidth.WEEK:
                    keyValueMin = 1;
                    keyValueMax = 5;
                    break;
                case TaskManagementController.StatisticWidth.MONTH:
                    keyValueMin = 1;
                    keyValueMax = 12;
                    break;
                case TaskManagementController.StatisticWidth.QUARTER:
                    keyValueMin = 1;
                    keyValueMax = 4;
                    break;
                case TaskManagementController.StatisticWidth.YEAR:

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

}
