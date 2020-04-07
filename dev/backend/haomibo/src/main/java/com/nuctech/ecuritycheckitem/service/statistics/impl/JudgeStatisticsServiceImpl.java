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
            response.setTo(Utils.parseLong(paginatedResult.get("to").toString()));
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

    /**
     * Get statistics by statistics width
     * @param query
     * @param statWidth : (hour, day, week, month, quarter, year)
     * @param startTime : start time
     * @param endTime : endtime
     * @return
     */
    private TreeMap<Integer, JudgeStatisticsResponseModel> getDetailedStatistics(String query, String statWidth, Date startTime, Date endTime) {
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();
        TreeMap<Integer, JudgeStatisticsResponseModel> data = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(statWidth, startTime, endTime);
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
            item.setLimitedArtificialDuration(systemConstants.getJudgeProcessingTime());
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
                "\tsum( IF ( g.judge_user_id != " + Constants.DEFAULT_SYSTEM_USER + ", 1, 0 ) ) AS artificialJudge,\n" +
                "\tsum( IF ( a.ASSIGN_TIMEOUT LIKE '" + SerJudgeGraph.AssignTimeout.TRUE + "', 1, 0 ) ) AS assignResult,\n" +
                "\tsum( IF ( g.judge_user_id = " + Constants.DEFAULT_SYSTEM_USER + " AND a.assign_judge_device_id IS NOT NULL " + " AND g.judge_timeout LIKE '" + SerJudgeGraph.JudgeTimeout.TRUE +"', 1, 0 ) ) AS judgeTimeout,\n" +
                "\tsum( IF ( (a.assign_id IS NULL OR a.ASSIGN_TIMEOUT LIKE '" + SerJudgeGraph.AssignTimeout.FALSE + "') AND a.assign_judge_device_id IS NULL " + ", 1, 0 ) ) AS atrResult,\n" +
                "\tsum( IF ( s.SCAN_ATR_RESULT LIKE '" + SerScan.ATRResult.TRUE + "' " +
                " AND g.JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.TRUE + "', 1, 0 ) ) AS suspiction,\n" +
                "\tsum( IF ( s.SCAN_ATR_RESULT LIKE '" + SerScan.ATRResult.FALSE + "' " +
                " OR g.JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.FALSE + "', 1, 0 ) ) AS noSuspiction,\n" +
                "\tcount( JUDGE_ID ) AS total ,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS avgDuration,\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS minDuration,\n" +
                "\t\n" +
                "\tAVG( CASE WHEN g.JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ELSE NULL END ) \n" +
                "\t AS artificialAvgDuration,\n" +
                "\tMAX( CASE WHEN g.JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ELSE NULL END ) AS artificialMaxDuration,\n" +
                "\tMIN( CASE WHEN g.JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ELSE NULL END ) AS artificialMinDuration\n";

    }

    /**
     * Get query for join
     * @return
     */
    private String getJoinQuery() {
        return "\tser_judge_graph g\n" +
                "\tLEFT JOIN sys_user u ON g.JUDGE_USER_ID = u.USER_ID\n" +
                "\tINNER JOIN ser_task t ON g.TASK_ID = t.TASK_ID\n" +
                "\tLEFT JOIN sys_workflow wf ON t.WORKFLOW_ID = wf.WORKFLOW_ID\n" +
                "\tLEFT JOIN ser_scan s ON t.TASK_ID = s.TASK_ID\n" +
                "\tLEFT JOIN ( SELECT task_id, assign_id, assign_judge_device_id, ASSIGN_TIMEOUT FROM ser_assign WHERE ASSIGN_HAND_DEVICE_ID IS NULL) a ON t.task_id = a.task_id \n";
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
        whereCause.add("s.SCAN_INVALID like '" + SerScan.Invalid.FALSE + "'");

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("t.DEVICE_ID = " + deviceId);
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

            record.setTime(Utils.parseInt(item[0]));
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
                record.setScanResultRate(record.getAtrResult() * 100 / (double) record.getTotal());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return record;
    }

}
