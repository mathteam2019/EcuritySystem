/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（HandExaminationStatisticsServiceImpl）
 * 文件名：	HandExaminationStatisticsServiceImpl.java
 * 描述：	HandExaminationStatisticsService implement
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.db.SysWorkMode;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationStatisticsPaginationResponse;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.statistics.HandExaminationStatisticsService;
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
public class HandExaminationStatisticsServiceImpl implements HandExaminationStatisticsService {

    @Autowired
    public EntityManager entityManager;

    @Autowired
    public AuthService authService;

    /**
     * get hand examination statistics
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
    public HandExaminationStatisticsPaginationResponse getStatistics(String sortBy, String order, Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {
        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth);

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
        


        HandExaminationStatisticsPaginationResponse response = new HandExaminationStatisticsPaginationResponse();
        queryBuilder.append(getDetailSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());

        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        //.... Get Detailed Statistics ....
        queryBuilder.append(" GROUP BY time ");
        currentPage = currentPage - 1;
        int start = currentPage * perPage;
        queryBuilder.append(" LIMIT " + start + ", " + perPage);
        List<HandExaminationResponseModel> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), statWidth, false);
        response.setDetailedStatistics(detailedStatistics);
        response.setTotal(count);
        response.setCurrent_page(currentPage + 1);
        response.setPer_page(perPage);
        response.setLast_page((int) Math.ceil(((double) count) / perPage));
        response.setFrom(perPage * currentPage + 1);
        response.setTo(perPage * currentPage + detailedStatistics.size());
        return response;
    }

    @Override
    public HandExaminationStatisticsPaginationResponse getChartStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth);

        StringBuilder queryBuilder = new StringBuilder();

        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        HandExaminationStatisticsPaginationResponse response = new HandExaminationStatisticsPaginationResponse();
        queryBuilder.append(getSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());

        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        //.... Get Detailed Statistics ....
        queryBuilder.append(" GROUP BY  " + groupBy + "(HAND_START_TIME)");
        List<HandExaminationResponseModel> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), statWidth, true);
        response.setDetailedStatistics(detailedStatistics);
        return response;
    }

    @Override
    public HandExaminationResponseModel getTotalStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {
        StringBuilder queryBuilder = new StringBuilder();

        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        HandExaminationStatisticsPaginationResponse response = new HandExaminationStatisticsPaginationResponse();
        queryBuilder.append(getSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());

        //................. get total statistics ....
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth);
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        return getTotalStatistics(queryBuilder.toString());
    }

    /**
     * Get total statistics amount
     * @param query
     * @return
     */
    private HandExaminationResponseModel getTotalStatistics(String query) {
        query = query.replace("(HAND_START_TIME)", "( '0000:01:01' )");
        HandExaminationResponseModel record = new HandExaminationResponseModel();
        Query jpaQueryTotal = entityManager.createNativeQuery(query);

        List<Object> resultTotal = jpaQueryTotal.getResultList();
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
     * @return
     */
    private List<HandExaminationResponseModel> getDetailedStatistics(String query, String statWidth, boolean isChart) {

        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();
        List<HandExaminationResponseModel> data = new ArrayList<>();


        for (int i = 0; i < result.size(); i++) {
            Object[] item = (Object[]) result.get(i);
            HandExaminationResponseModel record = initModelFromObject(item);
            if(isChart == false) {
                record.setTime(Utils.formatDateByStatisticWidth(statWidth, record.getTime()));
            }
            data.add(record);
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

    private String getMainSelectQuery() {
        return
                "\tcount( HAND_EXAMINATION_ID ) AS total,\n" +
                "\tsum( IF ( HAND_RESULT = '" + SerHandExamination.Result.TRUE + "' , 1, 0 ) ) AS seizure,\n" +
                "\tsum( IF ( HAND_RESULT = '" + SerHandExamination.Result.FALSE + "' , 1, 0 ) ) AS noSeizure,\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, HAND_START_TIME, HAND_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, HAND_START_TIME, HAND_END_TIME ) ) AS minDuration,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, HAND_START_TIME, HAND_END_TIME ) ) AS avgDuration \n";
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
                "\t (HAND_START_TIME) as time,\n" +
                getMainSelectQuery();
    }

    /**
     * query of select part
     * @param statWidth : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getDetailSelectQuery(String statWidth) {
        String groupBy = Utils.getGroupByTime(statWidth);
        String handGroupBy = groupBy.replace("groupby", "HAND_START_TIME");
        return "SELECT " +
                handGroupBy + " as time, " +
                getMainSelectQuery();

    }

    /**
     * query of select part
     * @param  : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getCountSelectQuery(String statWidth) {
        String groupBy = Utils.getGroupByTime(statWidth);
        String handGroupBy = groupBy.replace("groupby", "HAND_START_TIME");
        return "SELECT " +
                handGroupBy + " as time ";

    }

    /**
     * Get query for join
     * @return
     */
    private String getJoinQuery() {
        return "\thistory_finish h\n";
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
        List<String> whereCause = new ArrayList<>();

        if (fieldId != null) {
            whereCause.add("SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("SCAN_DEVICE_ID = " + deviceId);
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("HAND_USER_NAME like '%" + userName + "%'");
        }
        if (startTime != null) {
            Date date = startTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("HAND_START_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            Date date = endTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("HAND_END_TIME <= '" + strDate + "'");
        }

//        CategoryUser categoryUser = authService.getDataCategoryUserList();
//        if(categoryUser.isAll() == false) {
//            List<Long> idList = categoryUser.getUserIdList();
//            String idListStr = StringUtils.join(idList, ",");
//            whereCause.add("h.CREATEDBY in (" + idListStr + ") ");
//        }
        whereCause.add("HAND_EXAMINATION_ID IS NOT NULL ");
        return whereCause;
    }

    /**
     * return a handexamination statistics record from a record of a query
     * @param item
     * @return
     */
    private HandExaminationResponseModel initModelFromObject(Object[] item) {
        HandExaminationResponseModel record = new HandExaminationResponseModel();
        try {
            record.setTime(item[0].toString());
            record.setTotal(Utils.parseLong(item[1]));
            record.setSeizure(Utils.parseLong(item[2]));
            record.setNoSeizure(Utils.parseLong(item[3]));
            record.setMaxDuration(Utils.parseDouble(item[4]));
            record.setMinDuration(Utils.parseDouble(item[5]));
            record.setAvgDuration(Utils.parseDouble(item[6]));
            record.setSeizureRate(0);
            record.setNoSeizureRate(0);
            if (record.getTotal() > 0) {
                record.setSeizureRate(record.getSeizure() * 100 / (double) record.getTotal());
                record.setNoSeizureRate(record.getNoSeizure() * 100 / (double) record.getTotal());
            }

        } catch (Exception e) { }

        return record;
    }


}
