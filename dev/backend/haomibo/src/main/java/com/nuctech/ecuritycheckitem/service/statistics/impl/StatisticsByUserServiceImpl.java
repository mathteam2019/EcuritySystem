/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（StatisticsByUserServiceImpl）
 * 文件名：	StatisticsByUserServiceImpl.java
 * 描述：	StatisticsByUserService implement
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerScan;

import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.statistics.StatisticsByUserService;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.hql.internal.ast.DetailedSemanticException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.soap.Detail;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;

@Service
public class StatisticsByUserServiceImpl implements StatisticsByUserService {

    @Autowired
    public EntityManager entityManager;

    @Autowired
    AuthService authService;

    CategoryUser categoryUser;

    /**
     * get total statistics by device
     * @param modeId : workmode id
     * @param userName         : user name
     * @param startTime        : start time
     * @param endTime          : end time
     * @param currentPage      : current page
     * @param perPage          : per page
     * @return
     */
    @Override
    public TotalTimeStatisticsResponse getStatistics(String sortBy, String order, String modeId, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {

        Long count = getCount(modeId, userName, startTime, endTime, statWidth);

        String strQueryTime = getCountSelectQuery() + getJoinQuery();

        strQueryTime = strQueryTime +  " " + getWhereCause(modeId, userName, startTime, endTime);
        strQueryTime = strQueryTime + " GROUP BY startTime";
        String groupBy = Utils.getGroupByTime(statWidth);
        String timeGroupBy = groupBy.replace("groupby", "l.TIME");
        strQueryTime = strQueryTime.replace(":timeInfo", timeGroupBy + " as startTime");
        currentPage = currentPage - 1;
        int start = currentPage * perPage;
        strQueryTime = strQueryTime + " LIMIT " + start + ", " + perPage;

        String strQueryDetail = makeQuery(modeId, userName, startTime, endTime, statWidth);
        List<DetailTimeStatistics> detailTimeStatistics = getDetailedStatistics(strQueryTime, strQueryDetail, statWidth, currentPage, perPage);

        List<TotalTimeStatistics> totalTimeStatisticsList = Utils.convertTimeStatistcis(detailTimeStatistics);
        for(int i = 0; i < totalTimeStatisticsList.size(); i ++) {
            TotalTimeStatistics totalTimeStatistics = totalTimeStatisticsList.get(i);
            totalTimeStatistics.setTime(Utils.formatDateByStatisticWidth(statWidth, totalTimeStatistics.getTime()));
        }
        TotalTimeStatisticsResponse response = new TotalTimeStatisticsResponse();
        response.setDetailedStatistics(totalTimeStatisticsList);
        response.setTotal(count);
        response.setCurrent_page(currentPage + 1);
        response.setPer_page(perPage);
        response.setLast_page((int) Math.ceil(((double) count) / perPage));
        response.setFrom(perPage * currentPage + 1);
        response.setTo(perPage * currentPage + totalTimeStatisticsList.size());
        return response;
    }


    @Override
    public TotalTimeStatistics getChartStatistics(String modeId, String userName, Date startTime, Date endTime, String statWidth) {
        //categoryUser = authService.getDataCategoryUserList();
        //.... Get Total Statistics
        String strQuery = makeQuery(modeId, userName, startTime, endTime, statWidth);
        List<DetailTimeStatistics> detailTimeStatistics = getTotalStatistics(strQuery);

        List<TotalTimeStatistics> totalTimeStatisticsList = Utils.convertTimeStatistcis(detailTimeStatistics);
        return totalTimeStatisticsList.get(0);
    }
    /**
     * Get total statistics amount
     * @param query
     * @return
     */
    private List<DetailTimeStatistics> getTotalStatistics(String query) {

        String temp = query;
        temp = temp + " GROUP BY u.USER_NAME, d.DEVICE_TYPE";
        temp = temp + " ORDER BY u.USER_NAME";
        temp = temp.replace(":detail", "d.DEVICE_TYPE, u.USER_NAME ");
        temp = temp.replace(":timeInfo", "1 as startTime");



        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> resultTotal = jpaQuery.getResultList();

        TotalTimeStatistics record = new TotalTimeStatistics();
        List<DetailTimeStatistics> detailTimeStatistics = new ArrayList<>();

        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            DetailTimeStatistics timeStatistics = initModelFromObject(item);
            if(timeStatistics.getUserName() != null) {
                detailTimeStatistics.add(timeStatistics);
            }
        }
        return detailTimeStatistics;
    }



    private Long getCount(String modeId, String userName, Date startTime, Date endTime, String statWidth) {

        String strQuery = getCountSelectQuery() + getJoinQuery();

        strQuery = strQuery +  " " + getWhereCause(modeId, userName, startTime, endTime);
        strQuery = strQuery + " GROUP BY startTime";
        String groupBy = Utils.getGroupByTime(statWidth);
        String timeGroupBy = groupBy.replace("groupby", "l.TIME");
        strQuery = strQuery.replace(":timeInfo", timeGroupBy + " as startTime");
        String countQueryStr = "SELECT count(startTime) FROM (" + strQuery + ") as t";
        Query countQuery = entityManager.createNativeQuery(countQueryStr);
        List<Object> countResult = countQuery.getResultList();

        Long count = Utils.parseLong(countResult.get(0).toString());

//        strQuery = strQuery.replace(":whereJudge", getWhereCauseJudge(modeId, userName, startTime, endTime));
//        strQuery = strQuery.replace(":whereHand", getWhereCauseHand(modeId, userName, startTime, endTime));

        return count;
    }

    /**
     * Get statistics by statistics width
     * @param query
     * @return
     */
    private List<DetailTimeStatistics> getDetailedStatistics(String queryTime, String query, String statWidth, Integer currentPage, int perPage) {

        String temp = query;
        temp = temp + " GROUP BY startTime, u.USER_NAME, d.DEVICE_TYPE";
        temp = temp + " ORDER BY u.USER_NAME";
        temp = temp.replace(":detail", "d.DEVICE_TYPE, u.USER_NAME ");

        String groupBy = Utils.getGroupByTime(statWidth);
        String timeGroupBy = groupBy.replace("groupby", "l.TIME");

        temp = temp.replace(":timeInfo", timeGroupBy + " as startTime");

        String completeQuery = "SELECT t1.* FROM (" + queryTime + ") as t LEFT JOIN (\n" + temp + ") as t1 ON t.startTime = t1.startTime";

        Query jpaQuery = entityManager.createNativeQuery(completeQuery);
        List<Object> result = jpaQuery.getResultList();
        List<DetailTimeStatistics> detailTimeStatisticsList = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            Object[] item = (Object[]) result.get(i);
            DetailTimeStatistics timeStatistics = initModelFromObject(item);
            detailTimeStatisticsList.add(timeStatistics);
        }
        return detailTimeStatisticsList;
    }




    /**
     * build entire query
     * @param modeId
     * @param userName
     * @param startTime
     * @param endTime
     * @return
     */
    private String makeQuery(String modeId, String userName, Date startTime, Date endTime, String groupBy) {

        String strQuery = getSelectQuery() + getJoinQuery();

        strQuery = strQuery +  " " + getWhereCause(modeId, userName, startTime, endTime);
//        strQuery = strQuery.replace(":whereJudge", getWhereCauseJudge(modeId, userName, startTime, endTime));
//        strQuery = strQuery.replace(":whereHand", getWhereCauseHand(modeId, userName, startTime, endTime));

        return strQuery;
    }


    /**
     * query of select part
     * @param : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getSelectQuery() {
        return "SELECT\n" +
                "\n" +
                "\t :timeInfo,\n" +
                "\t sum(TIMESTAMPDIFF(SECOND,TIME,IF ( ISNULL(LOGOUT_TIME) , NOW(), LOGOUT_TIME ))),\n" +
                "\t :detail \n" +
                "\t FROM ser_login_info l \n";
    }

    /**
     * query of select part
     * @param : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getCountSelectQuery() {
        return "SELECT\n" +
                "\n" +
                "\t :timeInfo\n" +
                "\t FROM ser_login_info l \n";
    }

    /**
     * get join query part
     * @return
     */
    private String getJoinQuery() {

        return "LEFT JOIN \n" +
                "\tsys_device d ON l.DEVICE_ID = d.DEVICE_ID \n" +
                "\tLEFT JOIN \n" +
                "\tsys_user u ON l.USER_ID = u.USER_ID \n";
    }

    /**
     * get where cause for scan statistics
     * @param modeId
     * @param userName
     * @param startTime
     * @param endTime
     * @return
     */
    private String getWhereCause(String modeId, String userName, Date startTime, Date endTime) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if (modeId != null) {
            whereCause.add("d.DEVICE_TYPE = '" + modeId + "'");
        }
        if (startTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(startTime);
            whereCause.add("l.TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(endTime);
            whereCause.add("l.LOGOUT_TIME <= '" + strDate + "'");
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + userName + "%' ");
        }
        whereCause.add("l.LOGOUT_TIME IS NOT NULL ");
        whereCause.add("l.USER_ID IS NOT NULL ");

//        if(categoryUser.isAll() == false) {
//            List<Long> idList = categoryUser.getUserIdList();
//            String idListStr = StringUtils.join(idList, ",");
//            whereCause.add("s.CREATEDBY in (" + idListStr + ") ");
//        }

        if (!whereCause.isEmpty()) {
            stringBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        return stringBuilder.toString();
    }


    /**
     * return a total statistics record from a record of a query
     * @param item
     * @return
     */
    private DetailTimeStatistics initModelFromObject(Object[] item) {

        DetailTimeStatistics record = new DetailTimeStatistics();
        try {
            record.setTime(item[0].toString());
            record.setWorkingTime(Utils.parseLong(item[1]));
            record.setDeviceType(item[2].toString());
            record.setUserName(item[3].toString());
        } catch (Exception e) { }

        return record;
    }
}
