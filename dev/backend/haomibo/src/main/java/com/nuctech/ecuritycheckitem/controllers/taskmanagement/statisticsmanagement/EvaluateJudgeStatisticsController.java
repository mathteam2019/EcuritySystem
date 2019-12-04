package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.TaskManagementController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeStatisticsPaginationResponse;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Query;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/task/statistics/")
public class EvaluateJudgeStatisticsController extends BaseController {


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

    @RequestMapping(value = "/get-evaluatejudge-statistics", method = RequestMethod.POST)
    public Object getEvaluateJudgeSummary(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();
        response = getEvaluateJudgeStatistics(requestBody);
        return new CommonResponseBody(ResponseMessage.OK, response);

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

    public EvaluateJudgeStatisticsPaginationResponse getEvaluateJudgeStatistics(StatisticsRequestBody requestBody) {

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

}
