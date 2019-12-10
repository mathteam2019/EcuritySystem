package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.EvaluateJudgeStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.EvaluateJudgeStatisticsPdfView;
import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.db.SysWorkMode;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeStatisticsPaginationResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
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

import javax.persistence.Query;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
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

    /**
     * statistics generate request body.
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
     * Evaluate Statistics generate pdf file request.
     */
    @RequestMapping(value = "/evaluatejudge/generate/print", method = RequestMethod.POST)
    public Object evaluateJudgeStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, EvaluateJudgeResponseModel> totalStatistics = getEvaluateJudgeStatistics(requestBody.getFilter()).getDetailedStatistics();

        TreeMap<Integer, EvaluateJudgeResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        EvaluateJudgeStatisticsPdfView.setResource(res);
        InputStream inputStream = EvaluateJudgeStatisticsPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=evaluateJudgeStatistics.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * EvaluateJudge Statistics generate pdf file request.
     */
    @RequestMapping(value = "/evaluatejudge/generate/export", method = RequestMethod.POST)
    public Object evaluateJudgeGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, EvaluateJudgeResponseModel> judgeStatistics = getEvaluateJudgeStatistics(requestBody.getFilter()).getDetailedStatistics();
        TreeMap<Integer, EvaluateJudgeResponseModel> exportList = getExportList(judgeStatistics, requestBody.getIsAll(), requestBody.getIdList());
        InputStream inputStream = EvaluateJudgeStatisticsExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=evaluateJudgeStatistics.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    private TreeMap<Integer, EvaluateJudgeResponseModel> getExportList(TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, EvaluateJudgeResponseModel> exportList = new TreeMap<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (Map.Entry<Integer, EvaluateJudgeResponseModel> entry : detailedStatistics.entrySet()) {
                EvaluateJudgeResponseModel record = entry.getValue();
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

    /**
     * Private purpose Only
     * Get Start KeyDate and End Key Date for statistics
     * Ex: In case of Hour - it returns [0, 23], In case of Month it returns [1, 12]
     * @param requestBody
     * @return [startKeyDate, endKeyDate]
     */
    public List<Integer> getKeyValuesforStatistics(StatisticsRequestBody requestBody) {

        Integer keyValueMin = 1, keyValueMax = 0;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case ProcessTaskController.StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    break;
                case ProcessTaskController.StatisticWidth.DAY:
                    keyValueMax = 31;
                    break;
                case ProcessTaskController.StatisticWidth.WEEK:
                    keyValueMax = 5;
                    break;
                case ProcessTaskController.StatisticWidth.MONTH:
                    keyValueMax = 12;
                    break;
                case ProcessTaskController.StatisticWidth.QUARTER:
                    keyValueMax = 4;
                    break;
                case ProcessTaskController.StatisticWidth.YEAR:
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

    private String getQueryForSelectPart(String groupBy) {

        return "SELECT\n" +
                "\n" +
                groupBy +
                "\t (h.HAND_START_TIME) as time,\n" +
                "\tcount( HAND_EXAMINATION_ID ) AS total,\n" +
                "\tsum( IF ( h.HAND_RESULT LIKE '" + SerHandExamination.Result.TRUE + "' , 1, 0 ) ) AS seizure,\n" +
                "\tsum( IF ( h.HAND_RESULT LIKE '" + SerHandExamination.Result.FALSE + "' , 1, 0 ) ) AS noSeizure,\n" +
                "\tsum( IF ( s.SCAN_INVALID like '" + SerScan.Invalid.TRUE + "', 1, 0)) as totalJudge,\n" +
                "\tsum( IF ( c.HAND_APPRAISE LIKE '" + SerHandExamination.HandAppraise.MISSING + "', 1, 0 ) ) AS missingReport,\n" +
                "\tsum( IF ( c.HAND_APPRAISE LIKE '" + SerHandExamination.HandAppraise.MISTAKE + "', 1, 0 ) ) AS falseReport,\n" +
                "\t\n" +
                "\tsum( IF ( ISNULL (j.JUDGE_TIMEOUT), 1, 0)) as artificialJudge,\n" +
                "\tsum( IF ( ISNULL (j.JUDGE_TIMEOUT) and c.HAND_APPRAISE like '" + SerHandExamination.HandAppraise.MISSING + "', 1, 0)) as artificialJudgeMissing,\n" +
                "\tsum( IF ( ISNULL (j.JUDGE_TIMEOUT) and c.HAND_APPRAISE like '" + SerHandExamination.HandAppraise.MISTAKE + "', 1, 0)) as artificialJudgeMistake,\n" +
                "\t\n" +
                "\tsum( IF ( s.SCAN_INVALID like '" + SerScan.Invalid.TRUE + "' " +
                "and (wm.MODE_NAME like '" + SysWorkMode.WorkModeValue.MODE_1000001301 + "' " +
                "OR wm.MODE_NAME like '" + SysWorkMode.WorkModeValue.MODE_1000001301 + "')" +
                " and a.ASSIGN_TIMEOUT like '" + SerJudgeGraph.AssignTimeout.TRUE + "' " +
                " and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like '" + SerJudgeGraph.JudgeTimeout.TRUE + " ', 1, 0)) as intelligenceJudge,\n" +
                "\tsum( IF ( s.SCAN_INVALID like '" + SerScan.Invalid.TRUE + "' " +
                " and (wm.MODE_NAME like '" + SysWorkMode.WorkModeValue.MODE_1000001301 + "' " +
                " OR wm.MODE_NAME like '" + SysWorkMode.WorkModeValue.MODE_1000001302 + "') " +
                " and a.ASSIGN_TIMEOUT like '" + SerJudgeGraph.AssignTimeout.TRUE + "' " +
                " and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like '" + SerJudgeGraph.JudgeTimeout.TRUE + "' " +
                " and c.HAND_APPRAISE like '" + SerHandExamination.HandAppraise.MISSING + "', 1, 0)) as intelligenceJudgeMissing,\n" +
                "\tsum( IF ( s.SCAN_INVALID like '" + SerScan.Invalid.TRUE + "' " +
                " and (wm.MODE_NAME like '" + SysWorkMode.WorkModeValue.MODE_1000001301 + "' " +
                " OR wm.MODE_NAME like '" + SysWorkMode.WorkModeValue.MODE_1000001302 + "') " +
                " and a.ASSIGN_TIMEOUT like '" + SerJudgeGraph.AssignTimeout.TRUE + "' " +
                " and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like '" + SerJudgeGraph.JudgeTimeout.TRUE + "' " +
                " and c.HAND_APPRAISE like '" + SerHandExamination.HandAppraise.MISTAKE + "', 1, 0)) as intelligenceJudgeMistake,\n" +
                "\t\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS minDuration,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS avgDuration \n" +
                "\t\n";

    }

    private String getQueryForJoin() {
        return "\tser_hand_examination h\n" +
                "\tLEFT join sys_user u on h.HAND_USER_ID = u.USER_ID\n" +
                "\tLEFT join ser_login_info l on h.HAND_DEVICE_ID = l.DEVICE_ID\n" +
                "\tINNER JOIN ser_task t ON h.TASK_ID = t.task_id\n" +
                "\tLEFT JOIN ser_check_result2 c ON t.TASK_ID = c.task_id\n" +
                "\tleft join ser_judge_graph j on t.TASK_ID = j.TASK_ID\n" +
                "\tleft join ser_scan s on t.TASK_ID = s.TASK_ID\n" +
                "\tleft join ser_assign a on t.task_id = a.task_id\n" +
                "\tleft join sys_workflow wf on t.WORKFLOW_ID = wf.workflow_id\n" +
                "\tleft join sys_work_mode wm on wf.MODE_ID = wm.MODE_ID\n";
    }

    private List<String> getWhereCause(StatisticsRequestBody requestBody) {

        List<String> whereCause = new ArrayList<String>();

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

        return whereCause;
    }

    private EvaluateJudgeResponseModel getTotalStatistics(String query) {
        Query jpaQueryTotal = entityManager.createNativeQuery(query);
        EvaluateJudgeResponseModel record = new EvaluateJudgeResponseModel();

        List<Object> resultTotal = jpaQueryTotal.getResultList();
        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            record = initModelFromObject(item);
        }

        return record;
    }

    private TreeMap<Integer, EvaluateJudgeResponseModel> getDetailedStatistics(String query, StatisticsRequestBody requestBody) {
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();

        TreeMap<Integer, EvaluateJudgeResponseModel> data = new TreeMap<>();
        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }

        for (Integer i = keyValueMin;i <= keyValueMax; i++) {
            EvaluateJudgeResponseModel item = new EvaluateJudgeResponseModel();
            item.setTime(i);
            data.put(i, item);
        }

        for (int i = 0; i < result.size(); i++) {
            Object[] item = (Object[]) result.get(i);
            EvaluateJudgeResponseModel record = initModelFromObject(item);
            if (record.getTime() >= keyValueMin && record.getTime() <= keyValueMax) {
                data.put(record.getTime(), record);
            }
        }

        return data;
    }

    private Map<String, Object> getPaginatedList(TreeMap<Integer, EvaluateJudgeResponseModel> sorted, StatisticsRequestBody requestBody) {

        Map<String, Object> result = new HashMap<>();
        TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }

        if (requestBody.getCurrentPage() != null && requestBody.getCurrentPage() != null && requestBody.getCurrentPage() > 0 && requestBody.getPerPage() > 0) {
            Integer from, to;
            from = (requestBody.getCurrentPage() - 1) * requestBody.getPerPage() + keyValueMin;
            to = requestBody.getCurrentPage() * requestBody.getPerPage() - 1 + keyValueMin;

            if (from < keyValueMin) {
                from = keyValueMin;
            }

            if (to > keyValueMax) {
                to = keyValueMax;
            }

            result.put("from", from - keyValueMin + 1);
            result.put("to", to - keyValueMin + 1);

            for (Integer i = from; i <= to; i++) {
                detailedStatistics.put(i, sorted.get(i));
            }
        }

        result.put("list", detailedStatistics);
        return result;
    }

    public EvaluateJudgeStatisticsPaginationResponse getEvaluateJudgeStatistics(StatisticsRequestBody requestBody) {

        StringBuilder queryBuilder = new StringBuilder();
        String groupBy = "hour";
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            groupBy = requestBody.getFilter().getStatWidth();
        }

        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();

        queryBuilder.append(getQueryForSelectPart(groupBy) +"FROM\n" + getQueryForJoin());
        List<String> whereCause = getWhereCause(requestBody);
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        response.setTotalStatistics(getTotalStatistics(queryBuilder.toString()));

        queryBuilder.append(" GROUP BY  " + groupBy + "(h.HAND_START_TIME)");
        TreeMap<Integer, EvaluateJudgeResponseModel> sorted = getDetailedStatistics(queryBuilder.toString(), requestBody);
        try {
            Map<String, Object> paginatedResult = getPaginatedList(sorted, requestBody);
            response.setFrom(Long.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Long.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Integer, EvaluateJudgeResponseModel>)getPaginatedList(sorted, requestBody).get("list"));
        }
        catch (Exception e) {
            response.setDetailedStatistics(sorted);
        }

        if (requestBody.getPerPage() != null && requestBody.getCurrentPage() != null) {
            response.setPer_page(requestBody.getPerPage());
            response.setCurrent_page(requestBody.getCurrentPage());
            try {
                response.setTotal(sorted.size());
                if (response.getTotal() % response.getPer_page() == 0) {
                    response.setLast_page(response.getTotal() / response.getPer_page());
                } else {
                    response.setLast_page(response.getTotal() / response.getPer_page() + 1);
                }
            } catch (Exception e) { }
        }
        return response;
    }

    private EvaluateJudgeResponseModel initModelFromObject(Object[] item) {
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
            record.setMissingReportRate(0);
            record.setMistakeReportRate(0);
            record.setArtificialJudgeMissingRate(0);
            record.setArtificialJudgeMistakeRate(0);
            record.setIntelligenceJudgeMissingRate(0);
            record.setIntelligenceJudgeMistakeRate(0);

            if (record.getTotal() > 0) {
                record.setMissingReportRate(record.getMissingReport() * 100 / (double) record.getTotal());
                record.setMistakeReportRate(record.getMistakeReport() * 100 / (double) record.getTotal());
            }
            if (record.getArtificialJudge() > 0) {
                record.setArtificialJudgeMissingRate(record.getArtificialJudgeMissing() * 100 / (double) record.getArtificialJudge());
                record.setArtificialJudgeMistakeRate(record.getArtificialJudgeMistake() * 100 / (double) record.getArtificialJudge());
            }
            if (record.getIntelligenceJudge() > 0) {
                record.setIntelligenceJudgeMissingRate(record.getIntelligenceJudgeMissing() * 100 / (double) record.getIntelligenceJudge());
                record.setIntelligenceJudgeMistakeRate(record.getIntelligenceJudgeMistake() * 100 / (double) record.getIntelligenceJudge());
            }
        } catch (Exception e) { }

        return record;
    }

}
