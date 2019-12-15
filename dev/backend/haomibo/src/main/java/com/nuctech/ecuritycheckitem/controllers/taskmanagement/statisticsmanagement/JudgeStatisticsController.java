package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.JudgeStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.JudgeStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.JudgeStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
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
public class JudgeStatisticsController extends BaseController {

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


    @RequestMapping(value = "/get-judge-statistics", method = RequestMethod.POST)
    public Object getJudgeSummary(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, String> temp = new TreeMap<Integer, String>();

        JudgeStatisticsPaginationResponse response = new JudgeStatisticsPaginationResponse();
        //response = getJudgeStatistics(requestBody);

        response = judgeStatisticsService.getStatistics(
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
     * Judge Statistics generate pdf file request.
     */
    @RequestMapping(value = "/judge/generate/print", method = RequestMethod.POST)
    public Object judgeStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, JudgeStatisticsResponseModel> totalStatistics = judgeStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, JudgeStatisticsResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        JudgeStatisticsPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = JudgeStatisticsPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=judgeStatistics.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Scan Statistics generate excel file request.
     */
    @RequestMapping(value = "/judge/generate/export", method = RequestMethod.POST)
    public Object judgeStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, JudgeStatisticsResponseModel> totalStatistics = judgeStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, JudgeStatisticsResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = JudgeStatisticsExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=judgeStatistics.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Scan Statistics generate word file request.
     */
    @RequestMapping(value = "/judge/generate/word", method = RequestMethod.POST)
    public Object judgeStatisticsGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, JudgeStatisticsResponseModel> totalStatistics = judgeStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, JudgeStatisticsResponseModel> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = JudgeStatisticsWordView.buildWordDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=judgeStatistics.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }


    private TreeMap<Integer, JudgeStatisticsResponseModel> getExportList(TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, JudgeStatisticsResponseModel> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(",");

            for (Map.Entry<Integer, JudgeStatisticsResponseModel> entry : detailedStatistics.entrySet()) {

                JudgeStatisticsResponseModel record = entry.getValue();

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

    private String getSelectQuery(String groupBy) {
        return "SELECT\n" +
                "\n" +
                groupBy +
                "\t( judge_start_time ) AS time,\n" +
                "\tsum( IF ( g.judge_user_id != l.USER_ID, 1, 0 ) ) AS artificialJudge,\n" +
                "\tsum( IF ( s.SCAN_INVALID LIKE '" + SerScan.Invalid.TRUE + "' AND a.ASSIGN_TIMEOUT LIKE 'true', 1, 0 ) ) AS assignResult,\n" +
                "\tsum( IF ( g.judge_user_id = l.USER_ID, 1, 0 ) ) AS judgeTimeout,\n" +
                "\tsum( IF ( s.SCAN_ATR_RESULT LIKE '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS atrResult,\n" +
                "\tsum( IF ( s.SCAN_ATR_RESULT LIKE '" + SerScan.ATRResult.TRUE + "' " +
                " AND g.JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.TRUE + "', 1, 0 ) ) AS suspiction,\n" +
                "\tsum( IF ( s.SCAN_ATR_RESULT LIKE '" + SerScan.ATRResult.FALSE + "' " +
                " AND g.JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.FALSE + "', 1, 0 ) ) AS noSuspiction,\n" +
                "\tcount( JUDGE_ID ) AS total ,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS avgDuration,\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS minDuration,\n" +
                "\t\n" +
                "\tAVG( CASE WHEN g.JUDGE_USER_ID != l.USER_ID THEN TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ELSE NULL END ) \n" +
                "\t AS artificialAvgDuration,\n" +
                "\tMAX( CASE WHEN g.JUDGE_USER_ID != l.USER_ID THEN TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ELSE NULL END ) AS artificialMaxDuration,\n" +
                "\tMIN( CASE WHEN g.JUDGE_USER_ID != l.USER_ID THEN TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ELSE NULL END ) AS artificialMinDuration\n";

    }

    private String getJoinQuery() {
        return "\tser_judge_graph g\n" +
                "\tLEFT JOIN sys_user u ON g.JUDGE_USER_ID = u.USER_ID\n" +
                "\tLEFT JOIN ser_login_info l ON g.JUDGE_DEVICE_ID = l.DEVICE_ID\n" +
                "\tINNER JOIN ser_task t ON g.TASK_ID = t.TASK_ID\n" +
                "\tLEFT JOIN sys_workflow wf ON t.WORKFLOW_ID = wf.WORKFLOW_ID\n" +
                "\tLEFT JOIN ser_scan s ON t.TASK_ID = s.TASK_ID\n" +
                "\tLEFT JOIN ser_assign a ON t.task_id = a.task_id \n";
    }

    private List<String> getWhereCause(StatisticsRequestBody requestBody) {
        List<String> whereCause = new ArrayList<String>();
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
        return whereCause;
    }

    private JudgeStatisticsResponseModel getTotalStatistics(String query) {
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> resultTotal = jpaQuery.getResultList();

        SerPlatformCheckParams systemConstants = new SerPlatformCheckParams();
        try {
            List<SerPlatformCheckParams> list = serPlatformCheckParamRepository.findAll();
            systemConstants = list.get(0);

        } catch (Exception e) {

        }
        if (systemConstants.getJudgeProcessingTime() == null) {
           systemConstants.setJudgeProcessingTime((long) 0);
        }

        JudgeStatisticsResponseModel record = new JudgeStatisticsResponseModel();
        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            record = initModelFromObject(item);
            record.setLimitedArtificialDuration(systemConstants.getJudgeProcessingTime());
        }

        return record;
    }

    private TreeMap<Integer, JudgeStatisticsResponseModel> getDetailedStatistics(String query, StatisticsRequestBody requestBody) {
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();
        TreeMap<Integer, JudgeStatisticsResponseModel> data = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }

        SerPlatformCheckParams systemConstants = new SerPlatformCheckParams();
        try {
            List<SerPlatformCheckParams> list = serPlatformCheckParamRepository.findAll();
            systemConstants = list.get(0);

        } catch (Exception e) {

        }
        if (systemConstants.getJudgeProcessingTime() == null) {
            systemConstants.setJudgeProcessingTime((long) 0);
        }

        for (Integer i = keyValueMin; i <= keyValueMax; i++) {
            JudgeStatisticsResponseModel item = new JudgeStatisticsResponseModel();
            item.setTime(i);
            data.put(i, item);
        }

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            JudgeStatisticsResponseModel record = initModelFromObject(item);
            record.setLimitedArtificialDuration(systemConstants.getJudgeProcessingTime());
            data.put(record.getTime(), record);
        }

        return  data;
    }

    private Map<String, Object> getPaginatedList(TreeMap<Integer, JudgeStatisticsResponseModel> sorted, StatisticsRequestBody requestBody) {
        Map<String, Object> result = new HashMap<>();
        TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics = new TreeMap<>();

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


    public JudgeStatisticsPaginationResponse getJudgeStatistics(StatisticsRequestBody requestBody) {

        StringBuilder queryBuilder = new StringBuilder();

        String groupBy = "hour";
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            groupBy = requestBody.getFilter().getStatWidth();
        }

        JudgeStatisticsPaginationResponse response = new JudgeStatisticsPaginationResponse();

        //.... Get Total Statistics
        queryBuilder.append(getSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());
        List<String> whereCause = getWhereCause(requestBody);
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        JudgeStatisticsResponseModel totalStatistics = getTotalStatistics(queryBuilder.toString());
        response.setTotalStatistics(totalStatistics);

        //.... Get Detailed Statistics
        queryBuilder.append(" GROUP BY  " + groupBy + "(g.JUDGE_START_TIME)");
        TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), requestBody);

        try {
            Map<String, Object> paginatedResult = getPaginatedList(detailedStatistics, requestBody);
            response.setFrom(Long.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Long.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Integer, JudgeStatisticsResponseModel>)getPaginatedList(detailedStatistics, requestBody).get("list"));
        }
        catch (Exception e) {
            response.setDetailedStatistics(detailedStatistics);
        }

        if (requestBody.getPerPage() != null && requestBody.getCurrentPage() != null) {
            response.setPer_page(requestBody.getPerPage());
            response.setCurrent_page(requestBody.getCurrentPage());
            try {
                response.setTotal(detailedStatistics.size());
                if (response.getTotal() % response.getPer_page() == 0) {
                    response.setLast_page(response.getTotal() / response.getPer_page());
                } else {
                    response.setLast_page(response.getTotal() / response.getPer_page() + 1);
                }
            } catch (Exception e) { }
        }

        return response;
    }

    private JudgeStatisticsResponseModel initModelFromObject(Object[] item) {

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
            record.setArtificialResultRate(0);
            record.setAssignTimeoutResultRate(0);
            record.setJudgeTimeoutResultRate(0);
            record.setSuspictionRate(0);
            record.setNoSuspictionRate(0);
            record.setScanResultRate(0);
            if (record.getTotal() > 0) {
                record.setArtificialResultRate(record.getArtificialResult() * 100 / (double) record.getTotal());
                record.setAssignTimeoutResultRate(record.getAssignTimeout() * 100 / (double) record.getTotal());
                record.setJudgeTimeoutResultRate(record.getJudgeTimeout() * 100 / (double) record.getTotal());
                record.setSuspictionRate(record.getSuspiction() * 100 / (double) record.getTotal());
                record.setNoSuspictionRate(record.getNoSuspiction() * 100 / (double) record.getTotal());
                record.setScanResultRate(record.getAtrResult() * 100 / (double) record.getTotal());
            }
        } catch (Exception e) {
        }

        return record;
    }

}
