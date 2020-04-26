/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PreviewStatisticsServiceImpl）
 * 文件名：	PreviewStatisticsServiceImpl.java
 * 描述：	PreviewStatisticsService implement
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerScan;

import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatisticsResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsModelForPreview;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationStatisticsForPreview;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.UserService;
import com.nuctech.ecuritycheckitem.service.statistics.PreviewStatisticsService;
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
public class PreviewStatisticsServiceImpl implements PreviewStatisticsService {

    @Autowired
    public EntityManager entityManager;

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    private CategoryUser categoryUser;

    private String relateUserIdListStr;

    @Override
    public TotalStatistics getTotalStatistics(Long fieldId, Long deviceId, String userCategory, String userName, Date startTime, Date endTime, String statWidth) {
        categoryUser = authService.getDataCategoryUserList();
        String strQuery = makeQuery().replace(":whereScan", getWhereCauseScan(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":whereJudge", getWhereCauseJudge(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":whereHand", getWhereCauseHand(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":selectQuery", getMainSelect());

        TotalStatistics totalStatistics = getTotalStatistics(strQuery);
        return totalStatistics;
    }

    /**
     * get judge statistcs
     *
     * @param fieldId      : scene id
     * @param deviceId     : device id
     * @param userCategory : user category
     * @param userName     : user name
     * @param startTime    : start time
     * @param endTime      : end time
     * @param statWidth    : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    @Override
    public TotalStatisticsResponse getStatistics(String sortBy, String order, Long fieldId, Long deviceId, String userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {

        TotalStatisticsResponse response = new TotalStatisticsResponse();
        categoryUser = authService.getDataCategoryUserList();

        if(userCategory != null) {
            List<Long> relateUserIdList = userService.getUserListByResource(Constants.userCategory.get(userCategory));
            relateUserIdListStr = StringUtils.join(relateUserIdList, ",");
        }



        //.... Get Total Statistics
        String strQuery = makeQuery().replace(":whereScan", getWhereCauseScan(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":whereJudge", getWhereCauseJudge(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":whereHand", getWhereCauseHand(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));


        List<String> timeKeyList = Utils.getTimeKeyByStatistics(statWidth);

        String groupBy = timeKeyList.get(0) + "(groupby)";
        if(timeKeyList.size() > 1) {
            for(int i = 1; i < timeKeyList.size(); i ++) {
                groupBy = groupBy + ", SPACE(1)," +  "LPAD(" + timeKeyList.get(i) + "(groupby), 2, 0)";
            }
            groupBy = "CONCAT(" + groupBy + ")";
        }

        String scanGroupBy = groupBy.replace("groupby", "SCAN_START_TIME");
        String judgeGroupBy = groupBy.replace("groupby", "JUDGE_START_TIME");
        String handGroupBy = groupBy.replace("groupby", "HAND_START_TIME");


        String temp = strQuery;
        temp = temp.replace(":scanGroupBy", scanGroupBy);
        temp = temp.replace(":judgeGroupBy", judgeGroupBy);
        temp = temp.replace(":handGroupBy", handGroupBy);

        temp = temp + " WHERE (IFNULL(totalScan, 0) + IFNULL(validScan, 0) + IFNULL(passedScan, 0) + " +
                "IFNULL(alarmScan, 0) + IFNULL(totalJudge, 0) + IFNULL(suspictionJudge, 0) + " +
                "IFNULL(noSuspictionJudge, 0) + IFNULL(totalHand, 0) + " +
                "IFNULL(seizureHand, 0) + IFNULL(noSeizureHand, 0)) > 0";

        String tempCount = temp.replace(":selectQuery", "\tcount(q)\n");
        Query countQuery = entityManager.createNativeQuery(tempCount);
        List<Object> countResult = countQuery.getResultList();

        Long count = Utils.parseLong(countResult.get(0).toString());

        currentPage = currentPage - 1;
        //.... Get Detailed Statistics
        List<TotalStatistics> detailedStatistics = getDetailedStatistics(temp, statWidth, currentPage, perPage, false);

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
     * get judge statistcs
     *
     * @param fieldId      : scene id
     * @param deviceId     : device id
     * @param userCategory : user category
     * @param userName     : user name
     * @param startTime    : start time
     * @param endTime      : end time
     * @param statWidth    : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    @Override
    public TotalStatisticsResponse getChartStatistics(String sortBy, String order, Long fieldId, Long deviceId, String userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {
        categoryUser = authService.getDataCategoryUserList();
        TotalStatisticsResponse response = new TotalStatisticsResponse();
        //categoryUser = authService.getDataCategoryUserList();

        if(userCategory != null) {
            List<Long> relateUserIdList = userService.getUserListByResource(Constants.userCategory.get(userCategory));
            relateUserIdListStr = StringUtils.join(relateUserIdList, ",");
        }



        //.... Get Total Statistics
        String strQuery = makeQuery().replace(":whereScan", getWhereCauseScan(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":whereJudge", getWhereCauseJudge(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":whereHand", getWhereCauseHand(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));

        String groupBy = statWidth + "(groupby)";

        String scanGroupBy = groupBy.replace("groupby", "SCAN_START_TIME");
        String judgeGroupBy = groupBy.replace("groupby", "JUDGE_START_TIME");
        String handGroupBy = groupBy.replace("groupby", "HAND_START_TIME");


        String temp = strQuery;
        temp = temp.replace(":scanGroupBy", scanGroupBy);
        temp = temp.replace(":judgeGroupBy", judgeGroupBy);
        temp = temp.replace(":handGroupBy", handGroupBy);

        temp = temp + " WHERE (IFNULL(totalScan, 0) + IFNULL(validScan, 0) + IFNULL(passedScan, 0) + " +
                "IFNULL(alarmScan, 0) + IFNULL(totalJudge, 0) + IFNULL(suspictionJudge, 0) + " +
                "IFNULL(noSuspictionJudge, 0) + IFNULL(totalHand, 0) + " +
                "IFNULL(seizureHand, 0) + IFNULL(noSeizureHand, 0)) > 0";


        //.... Get Detailed Statistics
        List<TotalStatistics> detailedStatistics = getDetailedStatistics(temp, statWidth, currentPage, perPage, true);

        response.setDetailedStatistics(detailedStatistics);

        return response;

    }

    /**
     * Get total statistics amount
     *
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
     *
     * @param query
     * @return
     */
    private List<TotalStatistics> getDetailedStatistics(String query, String statWidth, Integer currentPage, Integer perPage, boolean isChart) {


        String temp = query;

        temp = temp + " ORDER BY t0.q";
        if(isChart == false) {
            int start = currentPage * perPage;
            temp = temp + " LIMIT " + start +", " + perPage;
        }



        temp = temp.replace(":selectQuery", getMainSelect());


        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> result = jpaQuery.getResultList();
        List<TotalStatistics> data = new ArrayList<>();



        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            TotalStatistics record = initModelFromObject(item);
            if(isChart == false) {
                record.setTime(Utils.formatDateByStatisticWidth(statWidth, record.getTime()));
            }

            data.add(record);
        }

        return data;
    }

    /**
     * Get paginated list using current pang and per page
     *
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
    private String getWhereCauseScan(Long fieldId, Long deviceId, String userCategory, String userName, Date startTime, Date endTime, String statWidth) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("t.DEVICE_ID = " + deviceId);
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
        if (userCategory != null && !userCategory.isEmpty()) {

            //whereCause.add("u.role_id = " + (Constants.userCategory.get(userCategory) == null ? "0" : Constants.userCategory.get(userCategory)));
        }

        if(categoryUser.isAll() == false) {
            List<Long> idList = categoryUser.getUserIdList();
            String idListStr = StringUtils.join(idList, ",");
            whereCause.add("s.CREATEDBY in (" + idListStr + ") ");
        }

        if (userCategory != null && !userCategory.isEmpty()) {
            whereCause.add(" u.user_id in (" + relateUserIdListStr + ") ");
        }


        stringBuilder.append("\t\tLEFT JOIN ser_task t ON s.task_id = t.task_id\n" +
                "\t\tLEFT JOIN \n" +
                "\t\tsys_user u\n" +
                "\t\tON s.SCAN_POINTSMAN_ID = u.user_id ");




        if (!whereCause.isEmpty()) {

            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }


        return stringBuilder.toString();
    }

    /**
     * Get judge statistics where condition  list
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
    private String getWhereCauseJudge(Long fieldId, Long deviceId, String userCategory, String userName, Date startTime, Date endTime, String statWidth) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("t.DEVICE_ID = " + deviceId);
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
        if (userCategory != null && !userCategory.isEmpty()) {

            //whereCause.add("u.role_id = " + (Constants.userCategory.get(userCategory) == null ? "0" : Constants.userCategory.get(userCategory)));
        }

        if(categoryUser.isAll() == false) {
            List<Long> idList = categoryUser.getUserIdList();
            String idListStr = StringUtils.join(idList, ",");
            whereCause.add("j.CREATEDBY in (" + idListStr + ") ");
        }

        if (userCategory != null && !userCategory.isEmpty()) {
            whereCause.add(" u.user_id in (" + relateUserIdListStr + ") ");
        }



        stringBuilder.append("\t\tLEFT JOIN ser_task t ON j.task_id = t.task_id\n" +
                "\t\tLEFT JOIN " +
                "\t\tser_scan s\n" +
                "\t\tON j.task_id = s.task_id " +
                "\t\tLEFT JOIN " +
                "\t\tsys_user u\n" +
                "\t\tON j.JUDGE_USER_ID = u.user_id ");

        whereCause.add(" s.SCAN_INVALID = 'FALSE' ");
        if (!whereCause.isEmpty()) {


            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        return stringBuilder.toString();
    }

    /**
     * Get hand statistics where condition  list
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
    private String getWhereCauseHand(Long fieldId, Long deviceId, String userCategory, String userName, Date startTime, Date endTime, String statWidth) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("t.DEVICE_ID = " + deviceId);
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
        if (userCategory != null && !userCategory.isEmpty()) {

            //whereCause.add("u.role_id = " + (Constants.userCategory.get(userCategory) == null ? "0" : Constants.userCategory.get(userCategory)));
        }

        if(categoryUser.isAll() == false) {
            List<Long> idList = categoryUser.getUserIdList();
            String idListStr = StringUtils.join(idList, ",");
            whereCause.add("h.CREATEDBY in (" + idListStr + ") ");
        }

        if (userCategory != null && !userCategory.isEmpty()) {
            whereCause.add(" u.user_id in (" + relateUserIdListStr + ") ");
        }


        stringBuilder.append("\t\tLEFT JOIN ser_task t ON h.task_id = t.task_id\n" +
                "\t\tLEFT JOIN " +
                "\t\tsys_user u\n" +
                "\t\tON h.HAND_USER_ID = u.user_id ");

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
        return "\tq,\n" +
                "\tIFNULL(totalScan, 0) as totalScan,\n" + "\tIFNULL(validScan, 0) as validScan,\n" + "\tIFNULL(invalidScan, 0) as invalidScan,\n"
                + "\tIFNULL(passedScan, 0) as passedScan,\n" + "\tIFNULL(alarmScan, 0) as alarmScan,\n" +
                "\tIFNULL(totalJudge, 0) as totalJudge,\n" + "\tIFNULL(suspictionJudge, 0) as suspictionJudge,\n" + "\tIFNULL(noSuspictionJudge, 0) as noSuspictionJudge,\n" +
                "\tIFNULL(totalHand, 0) as totalHand,\n" + "\tIFNULL(seizureHand, 0) as seizureHand,\n" + "\tIFNULL(noSeizureHand, 0) as noSeizureHand\n";
    }

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
     *
     * @return
     */
    private String getJoinQuery() {

        return getScanJoinQuery() + getJudgeJoinQuery() + getHandJoinQuery();

    }

    /**
     * get query for scan join query
     *
     * @return
     */
    private String getScanJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( SCAN_ID ) AS totalScan,\n" +
                "\t\tsum( IF ( s.SCAN_INVALID = '" + SerScan.Invalid.FALSE + "', 1, 0 ) ) AS validScan,\n" +
                "\t\tsum( IF ( s.SCAN_INVALID = '" + SerScan.Invalid.TRUE + "', 1, 0 ) ) AS invalidScan,\n" +
                "\t\tsum( IF ( s.SCAN_INVALID = '" + SerScan.Invalid.FALSE + "' AND SCAN_ATR_RESULT = '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS passedScan,\n" +
                "\t\tsum( IF ( s.SCAN_INVALID = '" + SerScan.Invalid.FALSE + "' AND SCAN_ATR_RESULT = '" + SerScan.ATRResult.FALSE + "', 1, 0 ) ) AS alarmScan,\n" +
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
     *
     * @return
     */
    private String getJudgeJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( judge_id ) AS totalJudge,\n" +
                "\t\tsum( IF ( JUDGE_RESULT = '" + SerJudgeGraph.Result.TRUE + "', 1, 0 ) ) AS suspictionJudge,\n" +
                "\t\tsum( IF ( JUDGE_RESULT = '" + SerJudgeGraph.Result.FALSE + "', 1, 0 ) ) AS noSuspictionJudge,\n" +
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
     *
     * @return
     */
    private String getHandJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( HAND_EXAMINATION_ID ) AS totalHand,\n" +
                "\t\tsum( IF ( HAND_RESULT = '" + SerHandExamination.Result.TRUE + "', 1, 0 ) ) AS seizureHand,\n" +
                "\t\tsum( IF ( HAND_RESULT = '" + SerHandExamination.Result.FALSE + "', 1, 0 ) ) AS noSeizureHand,\n" +
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
     *
     * @param item
     * @return
     */
    private TotalStatistics initModelFromObject(Object[] item) {

        TotalStatistics record = new TotalStatistics();
        try {
            record.setTime(item[0].toString());
            ScanStatistics scanStat = new ScanStatistics();
            JudgeStatisticsModelForPreview judgeStat = new JudgeStatisticsModelForPreview();
            HandExaminationStatisticsForPreview handStat = new HandExaminationStatisticsForPreview();

            scanStat.setTotalScan(Utils.parseLong(item[1].toString()));
            scanStat.setValidScan(Utils.parseLong(item[2].toString()));
            scanStat.setInvalidScan(Utils.parseLong(item[3].toString()));
            scanStat.setPassedScan(Utils.parseLong(item[4].toString()));
            scanStat.setAlarmScan(Utils.parseLong(item[5].toString()));
            judgeStat.setTotalJudge(Utils.parseLong(item[6].toString()));
            judgeStat.setSuspictionJudge(Utils.parseLong(item[7].toString()));
            judgeStat.setNoSuspictionJudge(Utils.parseLong(item[8].toString()));
            handStat.setTotalHandExamination(Utils.parseLong(item[9].toString()));
            handStat.setSeizureHandExamination(Utils.parseLong(item[10].toString()));
            handStat.setNoSeizureHandExamination(Utils.parseLong(item[11].toString()));

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

        } catch (Exception e) {
        }

        return record;
    }
}
