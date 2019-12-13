package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Service
public class StatisticsByDeviceServiceImpl implements StatisticsByDeviceService {

    @Autowired
    public EntityManager entityManager;


    private List<String> getWhereCause(Long deviceCategoryId, Long deviceId, Date startDate, Date endDate) {

        List<String> whereCause = new ArrayList<String>();

        if (deviceCategoryId != null) {
            whereCause.add("d.CATEGORY_ID = " + deviceCategoryId);
        }
        if (deviceId != null) {
            whereCause.add("d.DEVICE_ID = " + deviceId);
        }
        if (startDate != null) {
            whereCause.add("d.DEVICE_ID = " + deviceId);
        }
        if (endDate != null) {

        }

        return whereCause;

    }

    private String makeQuery() {

        return getSelectQuery() + getJoinQuery();

    }

    private String  getSelectQuery() {

        return "";
    }

    private String getJoinQuery() {

        return getScanJoinQuery() + getJudgeJoinQuery() + getHandJoinQuery();

    }
    private String getScanJoinQuery() {

        return "";
    }

    private String getJudgeJoinQuery() {

        return "";
    }

    private String getHandJoinQuery() {

        return "";
    }

    public List<Integer> getKeyValuesforStatistics(String statisticsWidth, Date startTime, Date endTime) {

        Integer keyValueMin = 1, keyValueMax = 0;
        if (statisticsWidth != null) {
            if (Constants.StatisticWidth.HOUR.equals(statisticsWidth)) {
                keyValueMin = 0;
                keyValueMax = 23;
            } else if (Constants.StatisticWidth.DAY.equals(statisticsWidth)) {
                keyValueMax = 31;
            } else if (Constants.StatisticWidth.WEEK.equals(statisticsWidth)) {
                keyValueMax = 5;
            } else if (Constants.StatisticWidth.MONTH.equals(statisticsWidth)) {
                keyValueMax = 12;
            } else if (Constants.StatisticWidth.QUARTER.equals(statisticsWidth)) {
                keyValueMax = 4;
            } else if (Constants.StatisticWidth.YEAR.equals(statisticsWidth)) {
                Map<String, Integer> availableYearRage = getAvailableYearRange(startTime, endTime);
                keyValueMax = availableYearRage.get("max");
                keyValueMin = availableYearRage.get("min");
            } else {
                keyValueMin = 0;
                keyValueMax = -1;
            }
        }

        List<Integer> result = new ArrayList<>();
        result.add(keyValueMin);
        result.add(keyValueMax);

        return result;
    }

    private Map<String, Integer> getAvailableYearRange(Date startTime, Date endTime) {

        Integer keyValueMin = 0, keyValueMax = 0;

        Integer yearMax = Calendar.getInstance().get(Calendar.YEAR);
        Integer yearMin = 1970;
        Calendar calendar = Calendar.getInstance();

        if (startTime != null) {
            keyValueMin = calendar.get(Calendar.YEAR);
        } else {
            keyValueMin = Calendar.getInstance().get(Calendar.YEAR) - 10 + 1;
        }
        if (endTime != null) {
            calendar.setTime(endTime);
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
            scanStat.setValidScanRate(0);
            scanStat.setInvalidScanRate(0);
            scanStat.setPassedScanRate(0);
            scanStat.setAlarmScanRate(0);
            judgeStat.setSuspictionJudgeRate(0);
            judgeStat.setNoSuspictionJudgeRate(0);
            handStat.setSeizureHandExaminationRate(0);
            handStat.setNoSeizureHandExaminationRate(0);
            if (scanStat.getTotalScan() > 0) {
                scanStat.setValidScanRate(scanStat.getValidScan() * 100 / (double)scanStat.getTotalScan());
                scanStat.setInvalidScanRate(scanStat.getInvalidScan() * 100 / (double)scanStat.getTotalScan());
                scanStat.setPassedScanRate(scanStat.getPassedScan() * 100 / (double)scanStat.getTotalScan());
                scanStat.setAlarmScanRate(scanStat.getAlarmScan() * 100 / (double)scanStat.getTotalScan());
            }
            if (judgeStat.getTotalJudge() > 0 ) {
                judgeStat.setSuspictionJudgeRate(judgeStat.getSuspictionJudge() * 100 / (double)judgeStat.getTotalJudge());
                judgeStat.setNoSuspictionJudgeRate(judgeStat.getNoSuspictionJudge() * 100 / (double)judgeStat.getTotalJudge());
            }
            if (handStat.getTotalHandExamination() > 0) {
                handStat.setSeizureHandExaminationRate(handStat.getSeizureHandExamination() * 100 / (double)handStat.getTotalHandExamination());
                handStat.setNoSeizureHandExaminationRate(handStat.getNoSeizureHandExamination() * 100 / (double)handStat.getTotalHandExamination());
            }
            record.setScanStatistics(scanStat);
            record.setJudgeStatistics(judgeStat);
            record.setHandExaminationStatistics(handStat);

        } catch (Exception e) {
        }

        return record;
    }


    private TreeMap<Long, TotalStatistics> getDetailedStatistics(String query, String statisticsWidth, Date startDate, Date endDate) {

        String groupBy = Constants.StatisticWidth.HOUR;
        groupBy = statisticsWidth;

        String temp = query;
        temp = temp.replace(":scanGroupBy", groupBy + "(SCAN_START_TIME)");
        temp = temp.replace(":judgeGroupBy", groupBy + "(JUDGE_START_TIME)");
        temp = temp.replace(":handGroupBy", groupBy + "(HAND_START_TIME)");

        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> result = jpaQuery.getResultList();
        TreeMap<Long, TotalStatistics> data = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(groupBy, startDate, endDate);
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
            data.put((long)i, item);
        }

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            TotalStatistics record = initModelFromObject(item);
            data.put(record.getTime(), record);
        }

        return  data;
    }

    Map<String, Object> getPaginatedList(TreeMap<Long, TotalStatistics> list, Integer currentPage, Integer perPage) {

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

    public TotalStatisticsResponse getStatistics(Long deviceCategoryId, Long deviceId, Date startDate, Date endDate, Integer currentPage, Integer perPage) {

        StringBuilder whereBuilder = new StringBuilder();

        TotalStatisticsResponse response = new TotalStatisticsResponse();
        List<String> whereCause = getWhereCause(deviceCategoryId, deviceId, startDate, endDate);

        if (!whereCause.isEmpty()) {
            whereBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        StringBuilder queryBuilder = new StringBuilder();
        //.... Get Total Statistics
        queryBuilder.append(makeQuery().replace(":where", whereBuilder.toString()));
        String strOriginalQuery = queryBuilder.toString();

        TotalStatistics totalStatistics = getTotalStatistics(strOriginalQuery);
        response.setTotalStatistics(totalStatistics);

        //.... Get Detailed Statistics
        TreeMap<Long, TotalStatistics> detailedStatistics = getDetailedStatistics(strOriginalQuery, "hour", startDate, endDate);

        try {
            Map<String, Object> paginatedResult = getPaginatedList(detailedStatistics, currentPage, perPage);
            response.setFrom(Long.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Long.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Long, TotalStatistics>)getPaginatedList(detailedStatistics, currentPage, perPage).get("list"));
        }
        catch (Exception e) {
            response.setDetailedStatistics(detailedStatistics);
        }

        if (currentPage != null && perPage != null) {
            response.setPer_page(currentPage);
            response.setCurrent_page(perPage);
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

}