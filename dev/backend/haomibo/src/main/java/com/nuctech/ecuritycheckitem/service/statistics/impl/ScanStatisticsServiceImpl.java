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

import com.nuctech.ecuritycheckitem.config.Constants;
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


        ScanStatisticsResponse response = new ScanStatisticsResponse();

        String strQuery = makeQuery().replace(":whereScan", getWhereCauseScan(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));


        List<String> timeKeyList = Utils.getTimeKeyByStatistics(statWidth);

        String groupBy = timeKeyList.get(0) + "(groupby)";
        if(timeKeyList.size() > 1) {
            for(int i = 1; i < timeKeyList.size(); i ++) {
                groupBy = groupBy + ", SPACE(1)," +  "LPAD(" + timeKeyList.get(i) + "(groupby), 2, 0)";
            }
            groupBy = "CONCAT(" + groupBy + ")";
        }

        String scanGroupBy = groupBy.replace("groupby", "SCAN_START_TIME");


        String temp = strQuery;
        temp = temp.replace(":scanGroupBy", scanGroupBy);

        temp = temp + " \tWHERE (IFNULL(totalScanProcess, 0) + IFNULL(totalScanFinish, 0)  + IFNULL(totalScanInvalid, 0)) > 0 ";

//        temp = temp + " WHERE (IFNULL(totalScan, 0) + IFNULL(validScan, 0) + IFNULL(passedScan, 0) + " +
//                "IFNULL(alarmScan, 0) + IFNULL(totalJudge, 0) + IFNULL(suspictionJudge, 0) + " +
//                "IFNULL(noSuspictionJudge, 0) + IFNULL(totalHand, 0) + " +
//                "IFNULL(seizureHand, 0) + IFNULL(noSeizureHand, 0)) > 0";

        String tempCount = temp.replace(":selectQuery", "\tcount(q)\n");
        Query countQuery = entityManager.createNativeQuery(tempCount);
        List<Object> countResult = countQuery.getResultList();

        Long count = Utils.parseLong(countResult.get(0).toString());

        currentPage = currentPage - 1;
        //.... Get Detailed Statistics
        List<ScanStatistics> detailedStatistics = getDetailedStatistics(temp, statWidth, currentPage, perPage, false);

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



        ScanStatisticsResponse response = new ScanStatisticsResponse();

        String strQuery = makeQuery().replace(":whereScan", getWhereCauseScan(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));


        String groupBy = statWidth + "(groupby)";

        String scanGroupBy = groupBy.replace("groupby", "SCAN_START_TIME");


        String temp = strQuery;
        temp = temp.replace(":scanGroupBy", scanGroupBy);

//        temp = temp + " WHERE (IFNULL(totalScan, 0) + IFNULL(validScan, 0) + IFNULL(passedScan, 0) + " +
//                "IFNULL(alarmScan, 0) + IFNULL(totalJudge, 0) + IFNULL(suspictionJudge, 0) + " +
//                "IFNULL(noSuspictionJudge, 0) + IFNULL(totalHand, 0) + " +
//                "IFNULL(seizureHand, 0) + IFNULL(noSeizureHand, 0)) > 0";


        //.... Get Detailed Statistics
        List<ScanStatistics> detailedStatistics = getDetailedStatistics(temp, statWidth, 0, 0, true);

        response.setDetailedStatistics(detailedStatistics);


        return response;

    }

    @Override
    public ScanStatistics getTotalStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, String workMode,
                                      Date startTime, Date endTime, String statWidth) {

        String strQuery = makeQuery().replace(":whereScan", getWhereCauseScan(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":selectQuery", getMainSelect());

        ScanStatistics totalStatistics = getTotalStatistics(strQuery);
        return totalStatistics;
    }

    /**
     * Get total statistics amount
     *
     * @param query
     * @return
     */
    private ScanStatistics getTotalStatistics(String query) {

        String temp = query.replace(":scanGroupBy", "1");

        Query jpaQuery = entityManager.createNativeQuery(temp);
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
     *
     * @param query
     * @return
     */
    private List<ScanStatistics> getDetailedStatistics(String query, String statWidth, Integer currentPage, Integer perPage, boolean isChart) {


        String temp = query;

        temp = temp + " ORDER BY t0.q";
        if(isChart == false) {
            int start = currentPage * perPage;
            temp = temp + " LIMIT " + start +", " + perPage;
        }



        temp = temp.replace(":selectQuery", getMainSelect());


        Query jpaQuery = entityManager.createNativeQuery(temp);
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

        return data;
    }



    /**
     * Get scan statistics where condition  list
     *
     * @param fieldId      : field id
     * @param deviceId     : device id
     * @param userCategory : user category
     * @param userName     : user name
     * @param startTime    : start time
     * @param endTime      : end time
     * @param statWidth    : (hour, day, week, month, quarter, year)
     * @return
     */
    private String getWhereCauseScan(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if (fieldId != null) {
            whereCause.add("SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("SCAN_DEVICE_ID = " + deviceId);
        }
        if (startTime != null) {
            DateFormat dateFormat = new SimpleDateFormat(Constants.SQL_DATETIME_FORMAT);
            String strDate = dateFormat.format(startTime);
            whereCause.add("SCAN_START_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            DateFormat dateFormat = new SimpleDateFormat(Constants.SQL_DATETIME_FORMAT);
            String strDate = dateFormat.format(endTime);
            whereCause.add("SCAN_END_TIME <= '" + strDate + "'");
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("SCAN_POINTSMAN_NAME like '%" + userName + "%' ");
        }

        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> idList = categoryUser.getUserIdList();
            String idListStr = StringUtils.join(idList, ",");
            whereCause.add("SCAN_POINTSMAN_ID in (" + idListStr + ") ");
        }



        if (!whereCause.isEmpty()) {

            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }


        return stringBuilder.toString();
    }


    /**
     * build whole query with select query and join query
     *
     * @return
     */
    private String makeQuery() {

        return getSelectQuery() + getJoinQuery();

    }

    private String getMainSelect() {
        return "\t*\n";
    }
//        return "\tq,\n" +
//                "\tIFNULL(totalScan, 0) as totalScan,\n" + "\tIFNULL(validScan, 0) as validScan,\n" + "\tIFNULL(invalidScan, 0) as invalidScan,\n"
//                + "\tIFNULL(passedScan, 0) as passedScan,\n" + "\tIFNULL(alarmScan, 0) as alarmScan,\n" +
//                "\tIFNULL(totalJudge, 0) as totalJudge,\n" + "\tIFNULL(suspictionJudge, 0) as suspictionJudge,\n" + "\tIFNULL(noSuspictionJudge, 0) as noSuspictionJudge,\n" +
//                "\tIFNULL(totalHand, 0) as totalHand,\n" + "\tIFNULL(seizureHand, 0) as seizureHand,\n" + "\tIFNULL(noSeizureHand, 0) as noSeizureHand\n";
//    }

    /**
     * query of select part
     *
     * @return
     */
    private String getSelectQuery() {

        return "SELECT\n" +
                ":selectQuery" +
                "\tFROM\n" + "\t(\n" +
                "\tSELECT\n" + "\t\tq \n" + "\tFROM\n" +
                "\t\t(\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:scanGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\thistory_process  UNION\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:scanGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\thistory_finish  UNION\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:scanGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\thistory_invalid\n" +
                "\t\t) AS t00 \n" +
                "\t) AS t0 ";
    }

    /**
     * Get query for whole join
     *
     * @return
     */
    private String getJoinQuery() {

        return getScanJoinQuery();

    }

    /**
     * get query for scan join query
     *
     * @return
     */
    private String getScanJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( SCAN_ID ) AS totalScanProcess,\n" +
                "\t\tsum( IF ( SCAN_ATR_RESULT = '" + SerScan.ATRResult.FALSE + "', 1, 0 ) ) AS passedScanProcess,\n" +
                "\t\tsum( IF ( SCAN_ATR_RESULT = '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS alarmScanProcess,\n" +
                "\t\t:scanGroupBy AS q11 \n" +
                "\tFROM\n" +
                "\t\thistory_process  \n" +
                "\t:whereScan\t" +
                "\tGROUP BY\n" +
                "\t\tq11 \n" +
                "\t) AS t11 ON t0.q = t11.q11\t" +
                "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( SCAN_ID ) AS totalScanFinish,\n" +
                "\t\tsum( IF ( SCAN_ATR_RESULT = '" + SerScan.ATRResult.FALSE + "', 1, 0 ) ) AS passedScanFinish,\n" +
                "\t\tsum( IF ( SCAN_ATR_RESULT = '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS alarmScanFinish,\n" +
                "\t\t:scanGroupBy AS q12 \n" +
                "\tFROM\n" +
                "\t\thistory_finish  \n" +
                "\t:whereScan\t" +
                "\tGROUP BY\n" +
                "\t\tq12 \n" +
                "\t) AS t12 ON t0.q = t12.q12\t" +
                "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( SCAN_ID ) AS totalScanInvalid,\n" +
                "\t\t:scanGroupBy AS q13 \n" +
                "\tFROM\n" +
                "\t\thistory_invalid  \n" +
                "\t:whereScan\t" +
                "\tGROUP BY\n" +
                "\t\tq13 \n" +
                "\t) AS t13 ON t0.q = t13.q13\t";
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


            Long totalScanProcess = Utils.parseLong(item[1]);
            Long totalScanFinish = Utils.parseLong(item[5]);
            Long totalScanInvalid = Utils.parseLong(item[9]);
            Long passScanProcess = Utils.parseLong(item[2]);
            Long passScanFinish = Utils.parseLong(item[6]);
            Long alarmScanProcess = Utils.parseLong(item[3]);
            Long alarmScanFinish = Utils.parseLong(item[7]);

            record.setTotalScan(totalScanProcess + totalScanFinish + totalScanInvalid);
            record.setValidScan(totalScanProcess + totalScanFinish);
            record.setInvalidScan(totalScanInvalid);
            record.setPassedScan(passScanProcess + passScanFinish);
            record.setAlarmScan(alarmScanProcess + alarmScanFinish);
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
