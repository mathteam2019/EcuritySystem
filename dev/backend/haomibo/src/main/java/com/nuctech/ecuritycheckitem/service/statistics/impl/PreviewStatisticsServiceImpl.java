package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
import com.nuctech.ecuritycheckitem.service.statistics.PreviewStatisticsService;
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
public class PreviewStatisticsServiceImpl implements PreviewStatisticsService {

    @Autowired
    public EntityManager entityManager;


    /**
     * get judge statistcs
     * @param fieldId : scene id
     * @param deviceId : device id
     * @param userCategory : user category
     * @param userName : user name
     * @param startTime : start time
     * @param endTime : end time
     * @param statWidth : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    @Override
    public TotalStatisticsResponse getStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {

        TotalStatisticsResponse response = new TotalStatisticsResponse();

        //.... Get Total Statistics
        String strQuery = makeQuery().replace(":whereScan", getWhereCauseScan(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":whereJudge", getWhereCauseJudge(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":whereHand", getWhereCauseHand(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));

        TotalStatistics totalStatistics = getTotalStatistics(strQuery);
        response.setTotalStatistics(totalStatistics);

        //.... Get Detailed Statistics
        TreeMap<Long, TotalStatistics> detailedStatistics = getDetailedStatistics(strQuery, statWidth, startTime, endTime);

        try {
            Map<String, Object> paginatedResult = getPaginatedList(detailedStatistics, statWidth, startTime, endTime, currentPage, perPage);
            response.setFrom(Long.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Long.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Long, TotalStatistics>) paginatedResult.get("list"));
        } catch (Exception e) {
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
            } catch (Exception e) {
            }
        }

        return response;

    }

    /**
     * Get total statistics amount
     * @param query
     * @return
     */
    private TotalStatistics getTotalStatistics(String query) {

        String temp = query.replace(":scanGroupBy", "1");
        temp = temp.replace(":handGroupBy", "1");
        temp = temp.replace(":judgeGroupBy", "1");

        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> resultTotal = jpaQuery.getResultList();

        TotalStatistics record = new TotalStatistics();
        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            record = initModelFromObject(item);
        }

        return record;
    }

    /**
     * Get statistics by statistics width
     * @param query
     * @param statWidth : (hour, day, week, month, quarter, year)
     * @param startTime : start time
     * @param endTime : endtime
     * @return
     */
    private TreeMap<Long, TotalStatistics> getDetailedStatistics(String query, String statisticsWidth, Date startTime, Date endTime) {

        String groupBy = "hour";
        if (statisticsWidth != null && !statisticsWidth.isEmpty()) {
            groupBy = statisticsWidth;
        }

        String temp = query;
        temp = temp.replace(":scanGroupBy", groupBy + "(SCAN_START_TIME)");
        temp = temp.replace(":judgeGroupBy", groupBy + "(JUDGE_START_TIME)");
        temp = temp.replace(":handGroupBy", groupBy + "(HAND_START_TIME)");

        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> result = jpaQuery.getResultList();
        TreeMap<Long, TotalStatistics> data = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = Utils.getKeyValuesforStatistics(statisticsWidth, startTime, endTime);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }

        for (Integer i = keyValueMin; i <= keyValueMax; i++) {
            TotalStatistics item = new TotalStatistics();
            item.setScanStatistics(new ScanStatistics());
            item.setJudgeStatistics(new JudgeStatisticsModelForPreview());
            item.setHandExaminationStatistics(new HandExaminationStatisticsForPreview());
            item.setTime(i);
            data.put((long) i, item);
        }

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            TotalStatistics record = initModelFromObject(item);
            data.put(record.getTime(), record);
        }

        return data;
    }

    /**
     * Get paginated list using current pang and per page
     * @param sorted
     * @param statWidth
     * @param startTime
     * @param endTime
     * @param currentPage
     * @param perPage
     * @return
     */
    private Map<String, Object> getPaginatedList(TreeMap<Long, TotalStatistics> sorted, String statWidth, Date startTime, Date endTime, Integer currentPage, Integer perPage) {
        Map<String, Object> result = new HashMap<>();
        TreeMap<Long, TotalStatistics> detailedStatistics = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = Utils.getKeyValuesforStatistics(statWidth, startTime, endTime);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) {
        }

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
                detailedStatistics.put((long) i, sorted.get((long) i));
            }
        }

        result.put("list", detailedStatistics);
        return result;
    }

    /**
     * Get scan statistics where condition  list
     * @param fieldId : field id
     * @param deviceId : device id
     * @param userCategory : user category
     * @param userName : user name
     * @param startTime : start time
     * @param endTime : end time
     * @param statWidth : (hour, day, week, month, quarter, year)
     * @return
     */
    private String getWhereCauseScan(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("SCAN_DEVICE_ID = " + deviceId);
        }
        if (startTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(startTime);
            whereCause.add("SCAN_START_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(endTime);
            whereCause.add("SCAN_END_TIME <= '" + strDate + "'");
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + userName + "%' ");
        }

        if (!whereCause.isEmpty()) {
            stringBuilder.append("\t\tLEFT JOIN ser_task t ON s.task_id = t.task_id\n" +
                    "\t\tLEFT JOIN sys_user u ON s.SCAN_POINTSMAN_ID = u.user_id ");
            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        return stringBuilder.toString();
    }

    /**
     * Get judge statistics where condition  list
     * @param fieldId : field id
     * @param deviceId : device id
     * @param userCategory : user category
     * @param userName : user name
     * @param startTime : start time
     * @param endTime : end time
     * @param statWidth : (hour, day, week, month, quarter, year)
     * @return
     */
    private String getWhereCauseJudge(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("JUDGE_DEVICE_ID = " + deviceId);
        }
        if (startTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(startTime);
            whereCause.add("JUDGE_START_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(endTime);
            whereCause.add("JUDGE_END_TIME <= '" + strDate + "'");
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + userName + "%' ");
        }

        if (!whereCause.isEmpty()) {
            stringBuilder.append("\t\tLEFT JOIN ser_task t ON j.task_id = t.task_id\n" +
                    "\t\tLEFT JOIN sys_user u ON j.JUDGE_USER_ID = u.user_id ");
            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        return stringBuilder.toString();
    }

    /**
     * Get hand statistics where condition  list
     * @param fieldId : field id
     * @param deviceId : device id
     * @param userCategory : user category
     * @param userName : user name
     * @param startTime : start time
     * @param endTime : end time
     * @param statWidth : (hour, day, week, month, quarter, year)
     * @return
     */
    private String getWhereCauseHand(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("HAND_DEVICE_ID = " + deviceId);
        }
        if (startTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(startTime);
            whereCause.add("HAND_START_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(endTime);
            whereCause.add("HAND_END_TIME <= '" + strDate + "'");
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + userName + "%' ");
        }

        if (!whereCause.isEmpty()) {
            stringBuilder.append("\t\tLEFT JOIN ser_task t ON h.task_id = t.task_id\n" +
                    "\t\tLEFT JOIN sys_user u ON h.HAND_USER_ID = u.user_id ");
            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        return stringBuilder.toString();
    }

    /**
     * build whole query with select query and join query
     * @return
     */
    private String makeQuery() {

        return getSelectQuery() + getJoinQuery();

    }

    /**
     * query of select part
     * @return
     */
    private String getSelectQuery() {

        return "SELECT\n" +
                "\tq,\n" +
                "\tIFNULL(totalScan, 0),\n" + "\tIFNULL(validScan, 0),\n" + "\tIFNULL(invalidScan, 0),\n" + "\tIFNULL(passedScan, 0),\n" + "\tIFNULL(alarmScan, 0),\n" +
                "\tIFNULL(totalJudge, 0),\n" + "\tIFNULL(suspictionJudge, 0),\n" + "\tIFNULL(noSuspictionJudge, 0),\n" +
                "\tIFNULL(totalHand, 0),\n" + "\tIFNULL(seizureHand, 0),\n" + "\tIFNULL(noSeizureHand, 0) \n" +
                "\tFROM\n" + "\t(\n" +
                "\tSELECT\n" + "\t\tq \n" + "\tFROM\n" +
                "\t\t(\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:scanGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\tser_scan s UNION\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:judgeGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\tser_judge_graph j UNION\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:handGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\tser_hand_examination h \n" +
                "\t\t) AS t00 \n" +
                "\t) AS t0 ";
    }

    /**
     * Get query for whole join
     * @return
     */
    private String getJoinQuery() {

        return getScanJoinQuery() + getJudgeJoinQuery() + getHandJoinQuery();

    }

    /**
     * get query for scan join query
     * @return
     */
    private String getScanJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( SCAN_ID ) AS totalScan,\n" +
                "\t\tsum( IF ( SCAN_INVALID LIKE '" + SerScan.Invalid.TRUE + "', 1, 0 ) ) AS validScan,\n" +
                "\t\tsum( IF ( SCAN_INVALID LIKE '" + SerScan.Invalid.FALSE + "', 1, 0 ) ) AS invalidScan,\n" +
                "\t\tsum( IF ( SCAN_ATR_RESULT LIKE '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS passedScan,\n" +
                "\t\tsum( IF ( SCAN_FOOT_ALARM LIKE '" + SerScan.FootAlarm.TRUE + "', 1, 0 ) ) AS alarmScan,\n" +
                "\t\t:scanGroupBy AS q1 \n" +
                "\tFROM\n" +
                "\t\tser_scan s \n" +
                "\t:whereScan\t" +
                "\tGROUP BY\n" +
                "\t\tq1 \n" +
                "\t) AS t1 ON t0.q = t1.q1\t";
    }

    /**
     * get query for judge join query
     * @return
     */
    private String getJudgeJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( judge_id ) AS totalJudge,\n" +
                "\t\tsum( IF ( JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.TRUE + "', 1, 0 ) ) AS suspictionJudge,\n" +
                "\t\tsum( IF ( JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.FALSE + "', 1, 0 ) ) AS noSuspictionJudge,\n" +
                "\t\t:judgeGroupBy AS q2 \n" +
                "\tFROM\n" +
                "\t\tser_judge_graph j \n" +
                "\t:whereJudge\t" +
                "\tGROUP BY\n" +
                "\t\tq2 \n" +
                "\t) AS t2 ON t0.q = t2.q2\t";
    }

    /**
     * get query for hand join query
     * @return
     */
    private String getHandJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( HAND_EXAMINATION_ID ) AS totalHand,\n" +
                "\t\tsum( IF ( HAND_RESULT LIKE '" + SerHandExamination.Result.TRUE + "', 1, 0 ) ) AS seizureHand,\n" +
                "\t\tsum( IF ( HAND_RESULT LIKE '" + SerHandExamination.Result.FALSE + "', 1, 0 ) ) AS noSeizureHand,\n" +
                "\t\t:handGroupBy AS q3 \n" +
                "\tFROM\n" +
                "\t\tser_hand_examination h \n" +
                "\t:whereHand\t" +
                "\tGROUP BY\n" +
                "\t\tq3 \n" +
                "\t) AS t3 ON t0.q = t3.q3\t";
    }

    /**
     * return a Total statistics record from a record of a query
     * @param item
     * @return
     */
    private TotalStatistics initModelFromObject(Object[] item) {

        TotalStatistics record = new TotalStatistics();
        try {
            record.setTime(Integer.parseInt(item[0].toString()));
            ScanStatistics scanStat = new ScanStatistics();
            JudgeStatisticsModelForPreview judgeStat = new JudgeStatisticsModelForPreview();
            HandExaminationStatisticsForPreview handStat = new HandExaminationStatisticsForPreview();

            scanStat.setTotalScan(Long.parseLong(item[1].toString()));
            scanStat.setValidScan(Long.parseLong(item[2].toString()));
            scanStat.setInvalidScan(Long.parseLong(item[3].toString()));
            scanStat.setPassedScan(Long.parseLong(item[4].toString()));
            scanStat.setAlarmScan(Long.parseLong(item[5].toString()));
            judgeStat.setTotalJudge(Long.parseLong(item[6].toString()));
            judgeStat.setSuspictionJudge(Long.parseLong(item[7].toString()));
            judgeStat.setNoSuspictionJudge(Long.parseLong(item[8].toString()));
            handStat.setTotalHandExamination(Long.parseLong(item[9].toString()));
            handStat.setSeizureHandExamination(Long.parseLong(item[10].toString()));
            handStat.setNoSeizureHandExamination(Long.parseLong(item[11].toString()));

            if (scanStat.getTotalScan() > 0) {
                scanStat.setValidScanRate(scanStat.getValidScan() * 100 / (double) scanStat.getTotalScan());
                scanStat.setInvalidScanRate(scanStat.getInvalidScan() * 100 / (double) scanStat.getTotalScan());
                scanStat.setPassedScanRate(scanStat.getPassedScan() * 100 / (double) scanStat.getTotalScan());
                scanStat.setAlarmScanRate(scanStat.getAlarmScan() * 100 / (double) scanStat.getTotalScan());
            }
            if (judgeStat.getTotalJudge() > 0) {
                judgeStat.setSuspictionJudgeRate(judgeStat.getSuspictionJudge() * 100 / (double) judgeStat.getTotalJudge());
                judgeStat.setNoSuspictionJudgeRate(judgeStat.getNoSuspictionJudge() * 100 / (double) judgeStat.getTotalJudge());
            }
            if (handStat.getTotalHandExamination() > 0) {
                handStat.setSeizureHandExaminationRate(handStat.getSeizureHandExamination() * 100 / (double) handStat.getTotalHandExamination());
                handStat.setNoSeizureHandExaminationRate(handStat.getNoSeizureHandExamination() * 100 / (double) handStat.getTotalHandExamination());
            }
            record.setScanStatistics(scanStat);
            record.setJudgeStatistics(judgeStat);
            record.setHandExaminationStatistics(handStat);

        } catch (Exception e) { }

        return record;
    }
}
