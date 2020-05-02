/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（JudgeStatisticsServiceImpl）
 * 文件名：	JudgeStatisticsServiceImpl.java
 * 描述：	JudgeStatisticsService implement
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsPaginationResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformCheckParamRepository;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.statistics.JudgeStatisticsService;
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

import static com.nuctech.ecuritycheckitem.utils.Utils.*;

@Service
public class JudgeStatisticsServiceImpl implements JudgeStatisticsService {

    @Autowired
    public EntityManager entityManager;

    @Autowired
    SerPlatformCheckParamRepository serPlatformCheckParamRepository;

    @Autowired
    AuthService authService;

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
    public JudgeStatisticsPaginationResponse getStatistics(String sortBy, String order, Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {
        String groupBy = "hour";
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth);
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }
        StringBuilder queryBuilderCount = new StringBuilder();
        queryBuilderCount.append(getCountSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());

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

        JudgeStatisticsPaginationResponse response = new JudgeStatisticsPaginationResponse();

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
        List<JudgeStatisticsResponseModel> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), statWidth, false);
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
     * Get statistics by statistics width
     * @param query
     * @param statWidth : (hour, day, week, month, quarter, year)
     * @return
     */
    private List<JudgeStatisticsResponseModel> getDetailedStatistics(String query, String statWidth, boolean isChart) {
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();

        SerPlatformCheckParams systemConstants = new SerPlatformCheckParams();
        try {
            List<SerPlatformCheckParams> list = serPlatformCheckParamRepository.findAll();
            systemConstants = list.get(0);
        } catch (Exception e) {
        }
        if (systemConstants.getJudgeProcessingTime() == null) {
            systemConstants.setJudgeProcessingTime((long) 0);
        }
        List<JudgeStatisticsResponseModel> data = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            JudgeStatisticsResponseModel record = initModelFromObject(item);
            record.setLimitedArtificialDuration(systemConstants.getJudgeProcessingTime());
            if(isChart == false) {
                record.setTime(Utils.formatDateByStatisticWidth(statWidth, record.getTime()));
            }
            data.add(record);
        }

        return  data;
    }

    @Override
    public JudgeStatisticsPaginationResponse getChartStatistics(Long fieldId, Long deviceId, Long userCategory, String userName,
                                                                Date startTime, Date endTime, String statWidth) {
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

        //.... Get Detailed Statistics
        queryBuilder.append(" GROUP BY  " + groupBy + "(JUDGE_START_TIME)");
        List<JudgeStatisticsResponseModel> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), statWidth, true);
        response.setDetailedStatistics(detailedStatistics);
        return response;
    }
    @Override
    public JudgeStatisticsResponseModel getTotalStatistics(Long fieldId, Long deviceId, Long userCategory, String userName,
                                                           Date startTime, Date endTime, String statWidth) {
        StringBuilder queryBuilder = new StringBuilder();

        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }
        queryBuilder.append(getSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth);
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        JudgeStatisticsResponseModel totalStatistics = getTotalStatistics(queryBuilder.toString());
        return totalStatistics;
    }

    /**
     * Get total statistics amount
     * @param query
     * @return
     */
    private JudgeStatisticsResponseModel getTotalStatistics(String query) {
        query = query.replace("( judge_start_time )", "( '0000:01:01' )");
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
    private Map<String, Object> getPaginatedList(TreeMap<Integer, JudgeStatisticsResponseModel> sorted,  String statWidth, Date startTime, Date endTime, Integer currentPage, Integer perPage) {
        Map<String, Object> result = new HashMap<>();
        TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(statWidth, startTime, endTime);
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

    private String getMainSelectQuery() {
        return "\tsum( IF (JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + ", 1, 0 ) ) AS artificialJudge,\n" +
                "\tsum( IF ( ASSIGN_JUDGE_TIMEOUT = '" + SerJudgeGraph.AssignTimeout.TRUE + "', 1, 0 ) ) AS assignResult,\n" +
                "\tsum( IF ( JUDGE_USER_ID = " + Constants.DEFAULT_SYSTEM_USER + " AND ASSIGN_JUDGE_DEVICE_ID IS NOT NULL " + " AND JUDGE_TIMEOUT = '" + SerJudgeGraph.JudgeTimeout.TRUE +"', 1, 0 ) ) AS judgeTimeout,\n" +
                "\tsum( IF ( (ASSIGN_JUDGE_ID IS NULL OR ASSIGN_JUDGE_TIMEOUT = '" + SerJudgeGraph.AssignTimeout.FALSE + "') AND ASSIGN_JUDGE_DEVICE_ID IS NULL " + ", 1, 0 ) ) AS atrResult,\n" +
                "\tsum( IF ( SCAN_ATR_RESULT = '" + SerScan.ATRResult.TRUE + "' " +
                " AND JUDGE_RESULT = '" + SerJudgeGraph.Result.TRUE + "', 1, 0 ) ) AS suspiction,\n" +
                "\tsum( IF ( SCAN_ATR_RESULT = '" + SerScan.ATRResult.FALSE + "' " +
                " OR JUDGE_RESULT = '" + SerJudgeGraph.Result.FALSE + "', 1, 0 ) ) AS noSuspiction,\n" +
                "\tcount( JUDGE_ID ) AS total ,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ) AS avgDuration,\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ) AS minDuration,\n" +
                "\t\n" +
                "\tAVG( CASE WHEN JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ELSE NULL END ) \n" +
                "\t AS artificialAvgDuration,\n" +
                "\tMAX( CASE WHEN JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ELSE NULL END ) AS artificialMaxDuration,\n" +
                "\tMIN( CASE WHEN JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ELSE NULL END ) AS artificialMinDuration\n";

    }

    /**
     * query of select part
     * @param statWidth : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getDetailSelectQuery(String statWidth) {
        String groupBy = Utils.getGroupByTime(statWidth);
        String judgeGroupBy = groupBy.replace("groupby", "judge_start_time");
        return "SELECT " +
                judgeGroupBy + " as time, " +
                getMainSelectQuery();

    }

    /**
     * query of select part
     * @param groupBy : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getSelectQuery(String groupBy) {
        return "SELECT\n" +
                "\n" +
                groupBy +
                "\t( judge_start_time ) AS time,\n" +
                getMainSelectQuery();
    }

    /**
     * query of select part
     * @param  : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getCountSelectQuery(String statWidth) {
        String groupBy = Utils.getGroupByTime(statWidth);
        String judgeGroupBy = groupBy.replace("groupby", "judge_start_time");
        return "SELECT " +
                judgeGroupBy + " as time ";

    }

    /**
     * Get query for join
     * @return
     */
    private String getJoinQuery() {
        return "\thistory h\n";
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
    private List<String> getWhereCause(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {
        List<String> whereCause = new ArrayList<String>();
        whereCause.add("SCAN_INVALID = '" + SerScan.Invalid.FALSE + "'");

        if (fieldId != null) {
            whereCause.add("SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("SCAN_DEVICE_ID = " + deviceId);
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("JUDGE_USER_NAME like '%" + userName + "%' ");
        }
        if (startTime != null) {
            Date date = startTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("JUDGE_START_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            Date date = endTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("JUDGE_END_TIME <= '" + strDate + "'");
        }
        whereCause.add("JUDGE_ID IS NOT NULL");
//        CategoryUser categoryUser = authService.getDataCategoryUserList();
//        if(categoryUser.isAll() == false) {
//            List<Long> idList = categoryUser.getUserIdList();
//            String idListStr = StringUtils.join(idList, ",");
//            whereCause.add("g.CREATEDBY in (" + idListStr + ") ");
//        }
        return whereCause;
    }

    /**
     * return a judge statistics record from a record of a query
     * @param item
     * @return
     */
    private JudgeStatisticsResponseModel initModelFromObject(Object[] item) {

        JudgeStatisticsResponseModel record = new JudgeStatisticsResponseModel();

        try {

            record.setTime(item[0].toString());
            record.setArtificialJudge(Utils.parseLong(item[1]));
            record.setAssignTimeout(Utils.parseLong(item[2]));
            record.setJudgeTimeout(Utils.parseLong(item[3]));
            record.setAtrResult(Utils.parseLong(item[4]));
            record.setSuspiction(Utils.parseLong(item[5]));
            record.setNoSuspiction(Utils.parseLong(item[6]));
            record.setTotal(Utils.parseLong(item[7]));
            record.setAvgDuration(Utils.parseDouble(item[8]));
            record.setMaxDuration(Utils.parseDouble(item[9]));
            record.setMinDuration(Utils.parseDouble(item[10]));
            record.setAvgArtificialJudgeDuration(Utils.parseDouble(item[11]));
            record.setMaxArtificialJudgeDuration(Utils.parseDouble(item[12]));
            record.setMinArtificialJudgeDuration(Utils.parseDouble(item[13]));
            record.setArtificialResultRate(0);
            record.setAssignTimeoutResultRate(0);
            record.setJudgeTimeoutResultRate(0);
            record.setSuspictionRate(0);
            record.setNoSuspictionRate(0);
            record.setScanResultRate(0);
            if (record.getTotal() > 0) {
                record.setArtificialResultRate(record.getArtificialJudge() * 100 / (double) record.getTotal());
                record.setAssignTimeoutResultRate(record.getAssignTimeout() * 100 / (double) record.getTotal());
                record.setJudgeTimeoutResultRate(record.getJudgeTimeout() * 100 / (double) record.getTotal());
                record.setSuspictionRate(record.getSuspiction() * 100 / (double) record.getTotal());
                record.setNoSuspictionRate(record.getNoSuspiction() * 100 / (double) record.getTotal());
                record.setScanResultRate(record.getScanResult() * 100 / (double) record.getTotal());
                record.setAtrResultRate(record.getAtrResult() * 100 / (double) record.getTotal());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return record;
    }

}
