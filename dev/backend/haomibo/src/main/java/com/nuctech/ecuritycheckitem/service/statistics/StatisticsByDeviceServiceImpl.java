package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatisticsByDeviceServiceImpl implements StatisticsByDeviceService {

    @Autowired
    public EntityManager entityManager;


    @Override
    public TotalStatisticsResponse getStatistics(Long deviceCategoryId, Long deviceId, Date startTime, Date endTime, Integer currentPage, Integer perPage) {
        TotalStatisticsResponse response = new TotalStatisticsResponse();

        //.... Get Total Statistics

        String strQuery = makeQuery(deviceCategoryId, deviceId, startTime, endTime);

        TotalStatistics totalStatistics = getTotalStatistics(strQuery);
        response.setTotalStatistics(totalStatistics);

        //.... Get Detailed Statistics
        TreeMap<Long, TotalStatistics> detailedStatistics = getDetailedStatistics(strQuery, startTime, endTime);

        try {
            Map<String, Object> paginatedResult = getPaginatedList(detailedStatistics, startTime, endTime, currentPage, perPage);
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

    private TreeMap<Long, TotalStatistics> getDetailedStatistics(String query,  Date startTime, Date endTime) {

        String temp = query;
        temp = temp.replace(":scanGroupBy", "(SCAN_DEVICE_ID)");
        temp = temp.replace(":judgeGroupBy", "(JUDGE_DEVICE_ID)");
        temp = temp.replace(":handGroupBy", "(HAND_DEVICE_ID)");

        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> result = jpaQuery.getResultList();
        TreeMap<Long, TotalStatistics> data = new TreeMap<>();

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            TotalStatistics record = initModelFromObject(item);
            data.put(record.getId(), record);
        }

        return data;
    }

    private Map<String, Object> getPaginatedList(TreeMap<Long, TotalStatistics> list, Date starTime, Date endTime, Integer currentPage, Integer perPage) {

        HashMap<String, Object> paginationResult = new HashMap<String, Object>();
        TreeMap<Long, TotalStatistics> subList = new TreeMap<>();

        if (currentPage == null || perPage == null) {
            return null;
        }

        if (currentPage < 0 || perPage < 0) {
            return null;
        }

        Integer from = (currentPage - 1) * perPage + 1;
        Integer to = (currentPage) * perPage;

        if (from > list.size()) {
            return null;
        } else if (to > list.size()) {
            to = list.size();
        }

        paginationResult.put("from", from);
        paginationResult.put("to", to);
        paginationResult.put("total", list.size());

        if (list.size() % perPage == 0) {
            paginationResult.put("lastpage", list.size() / perPage);
        } else {
            paginationResult.put("lastpage", list.size() / perPage + 1);
        }

        int index = 0;
        for (Map.Entry<Long, TotalStatistics> entry : list.entrySet()) {
            if (index >= from - 1 && index <= to - 1) {
                subList.put(entry.getKey(), entry.getValue());
            }
            index++;
        }

        paginationResult.put("list", subList);

        return paginationResult;
    }

    private String getJoinWhereQueryForAvailableScanDeviceId(Long deviceId) {

        String strResult = "\t\t\twhere SCAN_DEVICE_ID = :deviceId\n";
        strResult = strResult.replace(":deviceId", deviceId.toString());

        return strResult;

    }

    private String getJoinWhereQueryForAvailableJudgeDeviceId(Long deviceId) {

        String strResult = "\t\t\twhere JUDGE_DEVICE_ID = :deviceId\n";
        strResult = strResult.replace(":deviceId", deviceId.toString());

        return strResult;

    }

    private String getJoinWhereQueryForAvailableHandDeviceId(Long deviceId) {

        String strResult = "\t\t\twhere HAND_DEVICE_ID = :deviceId\n";
        strResult = strResult.replace(":deviceId", deviceId.toString());

        return strResult;

    }

    private String makeQuery(Long deviceCategoryId, Long deviceId, Date startTime, Date endTime) {

        String strQuery =  getSelectQuery() + getJoinQuery();

        strQuery = strQuery.replace(":whereScan", getWhereCauseScan(deviceCategoryId, deviceId, startTime, endTime));
        strQuery = strQuery.replace(":whereJudge", getWhereCauseJudge(deviceCategoryId, deviceId, startTime, endTime));
        strQuery = strQuery.replace(":whereHand", getWhereCauseHand(deviceCategoryId, deviceId, startTime, endTime));

        if (deviceId != null) {
            strQuery = strQuery.replace(":selectScanDeviceIds", getJoinWhereQueryForAvailableScanDeviceId(deviceId));
            strQuery = strQuery.replace(":selectJudgeDeviceIds", getJoinWhereQueryForAvailableJudgeDeviceId(deviceId));
            strQuery = strQuery.replace(":selectHandDeviceIds", getJoinWhereQueryForAvailableHandDeviceId(deviceId));
        }
        else {
            strQuery = strQuery.replace(":selectScanDeviceIds", "");
            strQuery = strQuery.replace(":selectJudgeDeviceIds", "");
            strQuery = strQuery.replace(":selectHandDeviceIds", "");
        }

        return strQuery;

    }

    private String getWhereCauseScan(Long deviceCategoryId, Long deviceId, Date startTime, Date endTime) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

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
        if (deviceId != null) {
            whereCause.add("SCAN_DEVICE_ID = " + deviceId);
        }

        if (!whereCause.isEmpty()) {
            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        return stringBuilder.toString();
    }

    private String getWhereCauseJudge(Long deviceCategoryId, Long deviceId, Date startTime, Date endTime)  {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();


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
        if (deviceId != null) {
            whereCause.add("JUDGE_DEVICE_ID = " + deviceId);
        }
        if (!whereCause.isEmpty()) {
            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        return stringBuilder.toString();
    }

    private String getWhereCauseHand(Long deviceCategoryId, Long deviceId, Date startTime, Date endTime)  {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

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
        if (deviceId != null) {
            whereCause.add("HAND_DEVICE_ID = " + deviceId);
        }

        if (!whereCause.isEmpty()) {
            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        return stringBuilder.toString();
    }

    private String getSelectQuery() {

        return "SELECT\n" +
                "\tq,\n" +
                "\tIFNULL(totalScan, 0),\n" + "\tIFNULL(validScan, 0),\n" + "\tIFNULL(invalidScan, 0),\n" + "\tIFNULL(passedScan, 0),\n" + "\tIFNULL(alarmScan, 0),\n" +
                "\tIFNULL(totalJudge, 0),\n" + "\tIFNULL(suspictionJudge, 0),\n" + "\tIFNULL(noSuspictionJudge, 0),\n" +
                "\tIFNULL(totalHand, 0),\n" + "\tIFNULL(seizureHand, 0),\n" + "\tIFNULL(noSeizureHand, 0), \n" +
                "\tIFNULL(d.device_name, ''),\n" + "\t\tIFNULL(scanWorkingSeconds, 0),\n" + "\tIFNULL(judgeWorkingSeconds, 0),\n" + "\tIFNULL(handWorkingSeconds, 0)\n" +
                "\tFROM\n" + "\t(\n" +
                "\tSELECT\n" + "\t\tq \n" + "\tFROM\n" +
                "\t\t(\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:scanGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\tser_scan s \n"+
                "\t:selectScanDeviceIds\n" +
                "\t\tUNION\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:judgeGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\tser_judge_graph j " +
                "\t\t:selectJudgeDeviceIds\n" +
                "\tUNION\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:handGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\tser_hand_examination h \n" +
                "\t\t:selectHandDeviceIds\n" +
                "\t\t) AS t00 \n" +
                "\t) AS t0 \n" +
                "\tLEFT JOIN sys_device d ON t0.q = d.device_id\n";

    }

    private String getJoinQuery() {

        return getScanJoinQuery() + getJudgeJoinQuery() + getHandJoinQuery();

    }

    private String getScanJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( SCAN_ID ) AS totalScan,\n" +
                "\t\tsum( IF ( SCAN_INVALID LIKE '" + SerScan.Invalid.TRUE + "', 1, 0 ) ) AS validScan,\n" +
                "\t\tsum( IF ( SCAN_INVALID LIKE '" + SerScan.Invalid.FALSE + "', 1, 0 ) ) AS invalidScan,\n" +
                "\t\tsum( IF ( SCAN_ATR_RESULT LIKE '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS passedScan,\n" +
                "\t\tsum( IF ( SCAN_FOOT_ALARM LIKE '" + SerScan.FootAlarm.TRUE + "', 1, 0 ) ) AS alarmScan,\n" +
                "\t\tsum(TIMESTAMPDIFF(SECOND,SCAN_START_TIME,SCAN_END_TIME)) as scanWorkingSeconds,\n" +
                "\t\t:scanGroupBy AS q1 \n" +
                "\tFROM\n" +
                "\t\tser_scan s \n" +
                "\t:whereScan\t" +
                "\tGROUP BY\n" +
                "\t\tq1 \n" +
                "\t) AS t1 ON t0.q = t1.q1\t";
    }

    private String getJudgeJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( judge_id ) AS totalJudge,\n" +
                "\t\tsum( IF ( JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.TRUE + "', 1, 0 ) ) AS suspictionJudge,\n" +
                "\t\tsum( IF ( JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.FALSE + "', 1, 0 ) ) AS noSuspictionJudge,\n" +
                "\t\tsum(TIMESTAMPDIFF(SECOND,JUDGE_START_TIME,JUDGE_END_TIME)) as judgeWorkingSeconds,\t" +
                "\t\t:judgeGroupBy AS q2 \n" +
                "\tFROM\n" +
                "\t\tser_judge_graph j \n" +
                "\t:whereJudge\t" +
                "\tGROUP BY\n" +
                "\t\tq2 \n" +
                "\t) AS t2 ON t0.q = t2.q2\t";
    }

    private String getHandJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( HAND_EXAMINATION_ID ) AS totalHand,\n" +
                "\t\tsum( IF ( HAND_RESULT LIKE '" + SerHandExamination.Result.TRUE + "', 1, 0 ) ) AS seizureHand,\n" +
                "\t\tsum( IF ( HAND_RESULT LIKE '" + SerHandExamination.Result.FALSE + "', 1, 0 ) ) AS noSeizureHand,\n" +
                "\t\tsum(TIMESTAMPDIFF(SECOND,HAND_START_TIME,HAND_END_TIME)) as handWorkingSeconds,\n" +
                "\t\t:handGroupBy AS q3 \n" +
                "\tFROM\n" +
                "\t\tser_hand_examination h \n" +
                "\t:whereHand\t" +
                "\tGROUP BY\n" +
                "\t\tq3 \n" +
                "\t) AS t3 ON t0.q = t3.q3\t";
    }

    private TotalStatistics initModelFromObject(Object[] item) {

        TotalStatistics record = new TotalStatistics();
        try {
            record.setId(Integer.parseInt(item[0].toString()));
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
            record.setName(item[12].toString());
            scanStat.setWorkingSeconds(Double.parseDouble(item[13].toString()));
            judgeStat.setWorkingSeconds(Double.parseDouble(item[14].toString()));
            handStat.setWorkingSeconds(Double.parseDouble(item[15].toString()));
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