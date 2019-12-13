package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.SuspicionHandgoodsStatisticsController;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.SuspicionHandGoodsPaginationResponse;
import com.nuctech.ecuritycheckitem.service.statistics.SuspictionHandgoodsStatisticsService;
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
public class SuspictionHandgoodsStatisticsServiceImpl implements SuspictionHandgoodsStatisticsService {

    @Autowired
    public EntityManager entityManager;

    public SuspicionHandGoodsPaginationResponse getStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {

        StringBuilder queryBuilder = new StringBuilder();

        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        SuspicionHandGoodsPaginationResponse response = new SuspicionHandGoodsPaginationResponse();

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
            response.setFrom(Long.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Long.parseLong(paginatedResult.get("to").toString()));
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
            } catch (Exception e) {
            }
        }

        return response;


    }

    private TreeMap<String, Long> getTotalStatistics(String query) {


        String strQuery = query.replace(":groupBy", "1");

        Query jpaQuery = entityManager.createNativeQuery(strQuery);
        List<Object> resultTotal = jpaQuery.getResultList();

        TreeMap<String, Long> record = new TreeMap<String, Long>();
        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            for (int j = 0; j < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); j++) {
                record.put(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(j), Long.parseLong(item[j + 1].toString()));
            }

        }

        return record;
    }

    private TreeMap<Integer, TreeMap<String, Long>> getDetailedStatistics(String query, String statWidth, Date startTime, Date endTime) {

        String strQuery = "";

        strQuery = query.replace(":groupBy", statWidth + "(h.HAND_START_TIME)");


        Query jpaQuery = entityManager.createNativeQuery(strQuery);

        List<Object> result = jpaQuery.getResultList();
        TreeMap<Integer, TreeMap<String, Long>> data = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = Utils.getKeyValuesforStatistics(statWidth, startTime, endTime);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) {
        }


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
            TreeMap<String, Long> record = new TreeMap<>();
            for (int j = 0; j < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); j++) {
                record.put(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(j), Long.parseLong(item[j + 1].toString()));
            }
            record.put("time", Long.parseLong(item[0].toString()));
            data.put(Integer.parseInt(record.get("time").toString()), record);
        }

        return data;
    }

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

        return whereCause;
    }

}
