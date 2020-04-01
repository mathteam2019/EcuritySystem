/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SuspictionHandgoodsStatisticsServiceImpl）
 * 文件名：	SuspictionHandgoodsStatisticsServiceImpl.java
 * 描述：	SuspictionHandgoodsStatisticsService implement
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.SuspicionHandgoodsStatisticsController;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.SuspicionHandGoodsPaginationResponse;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.statistics.SuspictionHandgoodsStatisticsService;
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
public class SuspictionHandgoodsStatisticsServiceImpl implements SuspictionHandgoodsStatisticsService {

    @Autowired
    public EntityManager entityManager;

    @Autowired
    AuthService authService;

    /**
     * get statistics
     * @param fieldId
     * @param deviceId
     * @param userCategory
     * @param userName
     * @param startTime
     * @param endTime
     * @param statWidth
     * @param currentPage
     * @param perPage
     * @return
     */
    public SuspicionHandGoodsPaginationResponse getStatistics(String sortBy, String order, Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {

        SuspicionHandGoodsPaginationResponse response = new SuspicionHandGoodsPaginationResponse();

        StringBuilder queryBuilder = new StringBuilder();
        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        //.... Get Total Statistics
        String strQuery = getSelectQuery();
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth);
        StringBuilder whereBuilder = new StringBuilder();
        if (!whereCause.isEmpty()) {
            whereBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        strQuery = strQuery.replace(":where", whereBuilder.toString());
        queryBuilder.append(strQuery);

        TreeMap<String, Long> totalStatistics = getTotalStatistics(queryBuilder.toString());
        response.setTotalStatistics(totalStatistics);

        //.... Get Detailed Statistics
        TreeMap<Integer, TreeMap<String, Long>> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), statWidth, startTime, endTime);
        try {
            Map<String, Object> paginatedResult = getPaginatedList(detailedStatistics, statWidth, startTime, endTime, currentPage, perPage);
            response.setFrom(Utils.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Utils.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Integer, TreeMap<String, Long>>) paginatedResult.get("list"));
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
            } catch (Exception e) { }
        }

        return response;
    }

    /**
     * Get total statistics amount
     * @param query
     * @return
     */
    private TreeMap<String, Long> getTotalStatistics(String query) {

        String strQuery = query.replace(":groupBy", "1");

        Query jpaQuery = entityManager.createNativeQuery(strQuery);
        List<Object> resultTotal = jpaQuery.getResultList();

        TreeMap<String, Long> record = new TreeMap<String, Long>();
        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            for (int j = 0; j < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); j++) {
                record.put(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(j), Utils.parseLong(item[j + 1].toString()));
            }
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
    private TreeMap<Integer, TreeMap<String, Long>> getDetailedStatistics(String query, String statWidth, Date startTime, Date endTime) {

        String strQuery = query.replace(":groupBy", statWidth + "(h.HAND_START_TIME)");

        Query jpaQuery = entityManager.createNativeQuery(strQuery);

        List<Object> result = jpaQuery.getResultList();
        TreeMap<Integer, TreeMap<String, Long>> data = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = Utils.getKeyValuesforStatistics(statWidth, startTime, endTime);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }

        for (Integer i = keyValueMin; i <= keyValueMax; i++) {
            TreeMap<String, Long> item = new TreeMap<String, Long>();
            for (int j = 0; j < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); j++) {
                item.put(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(j), (long) 0);
            }
            item.put("time", (long)i);
            data.put(i, item);
        }

        for (int i = 0; i < result.size(); i++) {
            Object[] item = (Object[]) result.get(i);
            if(item[0] == null) {
                continue;
            }
            TreeMap<String, Long> record = new TreeMap<>();
            for (int j = 0; j < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); j++) {
                record.put(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(j), Utils.parseLong(item[j + 1].toString()));
            }
            record.put("time", Utils.parseLong(item[0].toString()));
            data.put(Utils.parseInt(record.get("time").toString()), record);
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
    private Map<String, Object> getPaginatedList(TreeMap<Integer, TreeMap<String, Long>> sorted, String statWidth, Date startTime, Date endTime, Integer currentPage, Integer perPage) {
        Map<String, Object> result = new HashMap<>();
        TreeMap<Integer, TreeMap<String, Long>> detailedStatistics = new TreeMap<>();

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
                detailedStatistics.put(i, sorted.get(i));
            }
        }

        result.put("list", detailedStatistics);
        return result;
    }

    /**
     * query of select part
     * @return
     */
    private String getSelectQuery() {

        return "SELECT\n" +
                "\t:groupBy,\n" +
                "\tsum( IF ( hand_goods LIKE '1000001601', 1, 0 ) ) AS a1,\n" +
                "\tsum( IF ( hand_goods LIKE '1000001602', 1, 0 ) ) AS a2,\n" +
                "\tsum( IF ( hand_goods LIKE '1000001603', 1, 0 ) ) AS a3,\n" +
                "\tsum( IF ( hand_goods LIKE '1000001604', 1, 0 ) ) AS a4,\n" +
                "\tsum( IF ( hand_goods LIKE '1000001605', 1, 0 ) ) AS a5\n" +
                "FROM\n" +
                "\thistory h \n" +
                "left join ser_task t on h.task_id = t.task_id\n" +
                "left join sys_user u on h.HAND_USER_ID = u.user_id\n" +
                "\n" +
                "\n:where\n" +
                "\nGROUP BY\n" +
                "\t:groupBy";

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

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("h.HAND_DEVICE_ID = " + deviceId);
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + userName + "%' ");
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
            whereCause.add("h.HAND_START_TIME <= '" + strDate + "'");
        }

//        CategoryUser categoryUser = authService.getDataCategoryUserList();
//        if(categoryUser.isAll() == false) {
//            List<Long> idList = categoryUser.getUserIdList();
//            String idListStr = StringUtils.join(idList, ",");
//            whereCause.add("h.CREATEDBY in (" + idListStr + ") ");
//        }

        return whereCause;
    }

}
