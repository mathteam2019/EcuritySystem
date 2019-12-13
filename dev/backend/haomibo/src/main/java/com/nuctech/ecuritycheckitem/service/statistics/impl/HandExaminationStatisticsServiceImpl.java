package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.HandExaminationStatisticsController;
import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.db.SysWorkMode;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeStatisticsPaginationResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationStatisticsPaginationResponse;
import com.nuctech.ecuritycheckitem.service.statistics.EvaluateJudgeStatisticsService;
import com.nuctech.ecuritycheckitem.service.statistics.HandExaminationStatisticsService;
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
public class HandExaminationStatisticsServiceImpl implements HandExaminationStatisticsService {

    @Autowired
    public EntityManager entityManager;


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
    public HandExaminationStatisticsPaginationResponse getStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {

        Map<String, Object> paramaterMap = new TreeMap<String, Object>();
        List<String> whereCause = new ArrayList<String>();

        StringBuilder queryBuilder = new StringBuilder();
        
        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        HandExaminationStatisticsPaginationResponse response = new HandExaminationStatisticsPaginationResponse();
        queryBuilder.append(getSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());

        //................. get total statistics ....

        whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth);

        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        response.setTotalStatistics(getTotalStatistics(queryBuilder.toString()));

        //.... Get Detailed Statistics ....
        queryBuilder.append(" GROUP BY  " + groupBy + "(h.HAND_START_TIME)");
        TreeMap<Integer, HandExaminationResponseModel> sorted = getDetailedStatistics(queryBuilder.toString(), statWidth, startTime, endTime);

        try {
            Map<String, Object> paginatedResult = getPaginatedList(sorted, statWidth, startTime, endTime, currentPage, perPage);
            response.setFrom(Long.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Long.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Integer, HandExaminationResponseModel>)paginatedResult.get("list"));
        }
        catch (Exception e) {
            response.setDetailedStatistics(sorted);
        }

        if (perPage != null && currentPage != null) {
            response.setPer_page(perPage);
            response.setCurrent_page(currentPage);
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

    private HandExaminationResponseModel getTotalStatistics(String query) {
        HandExaminationResponseModel record = new HandExaminationResponseModel();
        Query jpaQueryTotal = entityManager.createNativeQuery(query);

        List<Object> resultTotal = jpaQueryTotal.getResultList();
        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            record = initModelFromObject(item);
        }
        return record;
    }

    private TreeMap<Integer, HandExaminationResponseModel> getDetailedStatistics(String query, String statWidth, Date startTime, Date endTime) {

        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();
        TreeMap<Integer, HandExaminationResponseModel> data = new TreeMap<>();

        //init hash map
        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = Utils.getKeyValuesforStatistics(statWidth, startTime, endTime);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }

        for (Integer i = keyValueMin;i <= keyValueMax; i++) {
            HandExaminationResponseModel item = new HandExaminationResponseModel();
            item.setTime(i);
            data.put(i, item);
        }

        for (int i = 0; i < result.size(); i++) {
            Object[] item = (Object[]) result.get(i);
            HandExaminationResponseModel record = initModelFromObject(item);
            if (record.getTime() >= keyValueMin && record.getTime() <= keyValueMax) {
                data.put(record.getTime(), record);
            }
        }

        return data;
    }

    private Map<String, Object> getPaginatedList(TreeMap<Integer, HandExaminationResponseModel> sorted,  String statWidth, Date startTime, Date endTime, Integer currentPage, Integer perPage) {
        Map<String, Object> result = new HashMap<>();
        TreeMap<Integer, HandExaminationResponseModel> detailedStatistics = new TreeMap<>();

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
                "OR wm.MODE_NAME like '" + SysWorkMode.WorkModeValue.MODE_1000001302 + "')" +
                " and a.ASSIGN_TIMEOUT like '" + SerJudgeGraph.AssignTimeout.TRUE + "' " +
                " and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like '" + SerJudgeGraph.JudgeTimeout.TRUE + "', 1, 0)) as intelligenceJudge,\n" +
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
                "\tMAX( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS minDuration,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS avgDuration \n";
    }

    private String getJoinQuery() {
        return "\tser_hand_examination h\n" +
                "\tLEFT join sys_user u on h.HAND_USER_ID = u.USER_ID\n" +
                "\tINNER JOIN ser_task t ON h.TASK_ID = t.task_id\n" +
                "\tLEFT JOIN ser_check_result c ON t.TASK_ID = c.task_id\n" +
                "\tLEFT join ser_judge_graph j on t.TASK_ID = j.TASK_ID\n" +
                "\tLEFT join ser_login_info l on j.JUDGE_DEVICE_ID = l.DEVICE_ID\n" +
                "\tLEFT join ser_scan s on t.TASK_ID = s.TASK_ID\n" +
                "\tLEFT join ser_assign a on t.task_id = a.task_id\n" +
                "\tleft join sys_workflow wf on t.WORKFLOW_ID = wf.workflow_id\n" +
                "\tleft join sys_work_mode wm on wf.MODE_ID = wm.MODE_ID\n";
    }

    private List<String> getWhereCause(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {
        List<String> whereCause = new ArrayList<>();

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("h.HAND_DEVICE_ID = " + deviceId);
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + userName + "%'");
        }
        if (startTime != null) {
            Date date = startTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("h.HAND_START_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            Date date = endTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("h.HAND_END_TIME <= '" + strDate + "'");
        }

        whereCause.add("s.SCAN_INVALID like '" + SerScan.Invalid.TRUE + "' ");
        return whereCause;
    }

    private HandExaminationResponseModel initModelFromObject(Object[] item) {
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
