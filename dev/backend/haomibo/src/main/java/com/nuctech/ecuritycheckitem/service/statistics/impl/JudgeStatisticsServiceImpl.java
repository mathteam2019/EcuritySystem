package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.JudgeStatisticsController;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationStatisticsPaginationResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsPaginationResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformCheckParamRepository;
import com.nuctech.ecuritycheckitem.service.statistics.HandExaminationStatisticsService;
import com.nuctech.ecuritycheckitem.service.statistics.JudgeStatisticsService;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class JudgeStatisticsServiceImpl implements JudgeStatisticsService {

    @Autowired
    public EntityManager entityManager;

    @Autowired
    SerPlatformCheckParamRepository serPlatformCheckParamRepository;

    /**
     * Get Statistics to show on Preview Page
     *
     * @param fieldId
     * @param deviceId
     * @param userCategory
     * @param userName
     * @param startTime
     * @param endTime
     * @param statWidth
     * @return
     */
    @Override
    public JudgeStatisticsPaginationResponse getStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {

        StringBuilder queryBuilder = new StringBuilder();

        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        JudgeStatisticsPaginationResponse response = new JudgeStatisticsPaginationResponse();

        //.... Get Total Statistics
        queryBuilder.append(getSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth);
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        JudgeStatisticsResponseModel totalStatistics = getTotalStatistics(queryBuilder.toString());
        response.setTotalStatistics(totalStatistics);

        //.... Get Detailed Statistics
        queryBuilder.append(" GROUP BY  " + groupBy + "(g.JUDGE_START_TIME)");
        TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), statWidth, startTime, endTime);

        try {
            Map<String, Object> paginatedResult = getPaginatedList(detailedStatistics, statWidth, startTime, endTime, currentPage, perPage);
            response.setFrom(Long.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Long.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Integer, JudgeStatisticsResponseModel>)paginatedResult.get("list"));
        }
        catch (Exception e) {
            response.setDetailedStatistics(detailedStatistics);
        }

        if (perPage != null && currentPage != null) {
            response.setPer_page(perPage);
            response.setCurrent_page(currentPage);
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

    private TreeMap<Integer, JudgeStatisticsResponseModel> getDetailedStatistics(String query, String statWidth, Date startTime, Date endTime) {
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();
        TreeMap<Integer, JudgeStatisticsResponseModel> data = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = Utils.getKeyValuesforStatistics(statWidth, startTime, endTime);
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

    private Map<String, Object> getPaginatedList(TreeMap<Integer, JudgeStatisticsResponseModel> sorted,  String statWidth, Date startTime, Date endTime, Integer currentPage, Integer perPage) {
        Map<String, Object> result = new HashMap<>();
        TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = Utils.getKeyValuesforStatistics(statWidth, startTime, endTime);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }

        if (currentPage != null && currentPage != null && currentPage > 0 && perPage > 0) {
            Integer from, to;
            from = (currentPage - 1) * perPage + keyValueMin;
            to = currentPage * perPage - 1 + keyValueMin;

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

    private List<String> getWhereCause(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {
        List<String> whereCause = new ArrayList<String>();
        whereCause.add("s.SCAN_INVALID like 'true'");

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("g.JUDGE_DEVICE_ID = " + deviceId);
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + userName + "%' ");
        }
        if (startTime != null) {
            Date date = startTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("g.JUDGE_START_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            Date date = endTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("g.JUDGE_END_TIME <= '" + strDate + "'");
        }
        return whereCause;
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