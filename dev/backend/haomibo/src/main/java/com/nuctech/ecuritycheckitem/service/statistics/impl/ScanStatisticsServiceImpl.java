/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ScanStatisticsServiceImpl）
 * 文件名：	ScanStatisticsServiceImpl.java
 * 描述：	ScanStatisticsService implement
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatisticsResponse;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.statistics.ScanStatisticsService;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;

@Service
public class ScanStatisticsServiceImpl implements ScanStatisticsService {

    @Autowired
    public EntityManager entityManager;

    @Autowired
    AuthService authService;

    /**
     * get hand statistcs
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
    public ScanStatisticsResponse getStatistics(String sortBy, String order, Long fieldId, Long deviceId, Long userCategory, String userName, String workMode,
                                                Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {



        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, workMode, startTime, endTime, statWidth);

        StringBuilder queryBuilderCount = new StringBuilder();
        queryBuilderCount.append(getCountSelectQuery(statWidth) + "\tFROM\n" + getJoinQuery());

        if (!whereCause.isEmpty()) {
            queryBuilderCount.append(" where " + StringUtils.join(whereCause, " and "));
        }
        queryBuilderCount.append(" GROUP BY time ");
        String preCountQuery = queryBuilderCount.toString();
        String countQueryStr = "SELECT count(time) FROM (" + preCountQuery + ") as t";
        Query countQuery = entityManager.createNativeQuery(countQueryStr);
        List<Object> countResult = countQuery.getResultList();

        Long count = Utils.parseLong(countResult.get(0).toString());

        StringBuilder queryBuilder = new StringBuilder();
        ScanStatisticsResponse response = new ScanStatisticsResponse();

        //.... Get Total Statistics
        queryBuilder.append(getDetailSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());

        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        //.... Get Detailed Statistics
        queryBuilder.append(" GROUP BY time ");


        currentPage = currentPage - 1;
        int start = currentPage * perPage;
        queryBuilder.append(" LIMIT " + start + ", " + perPage);
        List<ScanStatistics> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), statWidth, false);
        response.setDetailedStatistics(detailedStatistics);
        response.setTotal(count);
        response.setCurrent_page(currentPage + 1);
        response.setPer_page(perPage);
        response.setLast_page((int) Math.ceil(((double) count) / perPage));
        response.setFrom(perPage * currentPage + 1);
        response.setTo(perPage * currentPage + detailedStatistics.size());

        return response;

    }

    /**
     * get hand statistcs
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
    public ScanStatisticsResponse getChartStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, String workMode,
                                                Date startTime, Date endTime, String statWidth) {

        StringBuilder queryBuilder = new StringBuilder();

        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        ScanStatisticsResponse response = new ScanStatisticsResponse();

        //.... Get Total Statistics
        queryBuilder.append(getSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, workMode, startTime, endTime, statWidth);
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        //.... Get Detailed Statistics
        queryBuilder.append(" GROUP BY " + groupBy + "(s.SCAN_START_TIME)");
        List<ScanStatistics> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), statWidth, true);
        response.setDetailedStatistics(detailedStatistics);


        return response;

    }

    @Override
    public ScanStatistics getTotalStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, String workMode,
                                      Date startTime, Date endTime, String statWidth) {
        StringBuilder queryBuilder = new StringBuilder();

        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        ScanStatisticsResponse response = new ScanStatisticsResponse();

        //.... Get Total Statistics
        queryBuilder.append(getSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, workMode, startTime, endTime, statWidth);
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        ScanStatistics totalStatistics = getTotalStatistics(queryBuilder.toString());
        return totalStatistics;
    }

    /**
     * Get total statistics amount
     * @param query
     * @return
     */
    private ScanStatistics getTotalStatistics(String query) {
        query = query.replace("(s.SCAN_START_TIME)", "( '0000:01:01' )");
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> resultTotal = jpaQuery.getResultList();

        ScanStatistics record = new ScanStatistics();
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
     * @param isChart : start time
     *
     * @return
     */
    private List<ScanStatistics> getDetailedStatistics(String query, String statWidth, boolean isChart) {
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();
        List<ScanStatistics> data = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            ScanStatistics record = initModelFromObject(item);
            if(isChart == false) {
                record.setTime(Utils.formatDateByStatisticWidth(statWidth, record.getTime()));
            }
            data.add(record);
        }
        return  data;
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
    private Map<String, Object> getPaginatedList(TreeMap<Integer, ScanStatistics> sorted,  String statWidth, Date startTime, Date endTime, Integer currentPage, Integer perPage) {
        Map<String, Object> result = new HashMap<>();
        TreeMap<Integer, ScanStatistics> detailedStatistics = new TreeMap<>();

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

    /**
     * query of select part
     * @param groupBy : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getSelectQuery(String groupBy) {
        return "SELECT " +
                groupBy +
                "\n (s.SCAN_START_TIME)," +
                "\tcount( s.SCAN_ID ) AS total,\n" +
                "\tsum( IF ( s.scan_invalid = '" + SerScan.Invalid.FALSE + "', 1, 0 ) ) AS valid,\n" +
                "\tsum( IF ( s.scan_invalid = '" + SerScan.Invalid.TRUE + "', 1, 0 ) ) AS invalid,\n" +
                "\tsum( IF ( s.scan_invalid = '" + SerScan.Invalid.FALSE + "' AND s.scan_atr_result = '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS passed,\n" +
                "\tsum( IF ( s.scan_invalid = '" + SerScan.Invalid.FALSE + "' AND s.scan_atr_result = '" + SerScan.ATRResult.FALSE + "', 1, 0 ) ) AS alarm\n";

    }


    /**
     * query of select part
     * @param statWidth : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getDetailSelectQuery(String statWidth) {
        String groupBy = Utils.getGroupByTime(statWidth);
        String scanGroupBy = groupBy.replace("groupby", "s.SCAN_START_TIME");
        return "SELECT " +
                scanGroupBy + " as time " +
                "\t, count( s.SCAN_ID ) AS total,\n" +
                "\tsum( IF ( s.scan_invalid = '" + SerScan.Invalid.FALSE + "', 1, 0 ) ) AS valid,\n" +
                "\tsum( IF ( s.scan_invalid = '" + SerScan.Invalid.TRUE + "', 1, 0 ) ) AS invalid,\n" +
                "\tsum( IF ( s.scan_invalid = '" + SerScan.Invalid.FALSE + "' AND s.scan_atr_result = '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS passed,\n" +
                "\tsum( IF ( s.scan_invalid = '" + SerScan.Invalid.FALSE + "' AND s.scan_atr_result = '" + SerScan.ATRResult.FALSE + "', 1, 0 ) ) AS alarm\n";

    }

    /**
     * query of select part
     * @param  : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getCountSelectQuery(String statWidth) {
        String groupBy = Utils.getGroupByTime(statWidth);
        String scanGroupBy = groupBy.replace("groupby", "s.SCAN_START_TIME");
        return "SELECT " +
                scanGroupBy + " as time ";

    }

    /**
     * Get query for join
     * @return
     */
    private String getJoinQuery() {

        return "\tser_scan s\n" +
                "\tLEFT JOIN ser_task t ON s.task_id = t.task_id\n" +
                "\tLEFT JOIN sys_user u ON s.SCAN_POINTSMAN_ID = u.user_id\n";

    }

    /**
     * Get where condition list
     * @param fieldId : field id
     * @param deviceId : device id
     * @param userCategory : user category
     * @param userName : user name
     * @param startTime : start time
     * @param endTime : end time
     * @param statWidth : (hour, day, week, month, quarter, year)
     * @return
     */
    private List<String> getWhereCause(Long fieldId, Long deviceId, Long userCategory, String userName, String workMode, Date startTime, Date endTime, String statWidth) {
        
        List<String> whereCause = new ArrayList<String>();

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("s.SCAN_DEVICE_ID = " + deviceId);
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + userName + "%' ");
        }
        if (workMode != null && !workMode.isEmpty()) {
            //whereCause.add("t.MODE_NAME like '%" + workMode + "%' ");
        }
        if (startTime != null) {
            Date date = startTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("s.SCAN_START_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            Date date = endTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("s.SCAN_END_TIME <= '" + strDate + "'");
        }
//        CategoryUser categoryUser = authService.getDataCategoryUserList();
//        if(categoryUser.isAll() == false) {
//            List<Long> idList = categoryUser.getUserIdList();
//            String idListStr = StringUtils.join(idList, ",");
//            whereCause.add("s.CREATEDBY in (" + idListStr + ") ");
//        }
        return whereCause;
    }



    /**
     * return a scan statistics record from a record of a query
     * @param item
     * @return
     */
    private ScanStatistics initModelFromObject(Object[] item) {

        ScanStatistics record = new ScanStatistics();

        try {
            record.setTime(item[0].toString());
            record.setTotalScan(Utils.parseLong(item[1].toString()));
            record.setValidScan(Utils.parseLong(item[2].toString()));
            record.setInvalidScan(Utils.parseLong(item[3].toString()));
            record.setPassedScan(Utils.parseLong(item[4].toString()));
            record.setAlarmScan(Utils.parseLong(item[5].toString()));
            record.setValidScanRate(0);
            record.setInvalidScanRate(0);
            record.setPassedScanRate(0);
            record.setAlarmScanRate(0);
            if (record.getTotalScan() > 0 && record.getValidScan() > 0) {
                record.setValidScanRate(record.getValidScan() * 100 / (double)record.getTotalScan());
                record.setInvalidScanRate(record.getInvalidScan() * 100 / (double)record.getTotalScan());
                record.setPassedScanRate(record.getPassedScan() * 100 / (double)record.getValidScan());
                record.setAlarmScanRate(record.getAlarmScan() * 100 / (double)record.getValidScan());
            }
        } catch (Exception e) {
        }

        return record;
    }

}
