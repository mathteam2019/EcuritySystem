/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（StatisticsByDeviceServiceImpl）
 * 文件名：	StatisticsByDeviceServiceImpl.java
 * 描述：	StatisticsByDeviceService implement
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerScan;

import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.statistics.StatisticsByDeviceService;
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
public class StatisticsByDeviceServiceImpl implements StatisticsByDeviceService {

    @Autowired
    public EntityManager entityManager;

    @Autowired
    AuthService authService;

    CategoryUser categoryUser;

    /**
     * get total statistics by device
     * @param deviceType : workmode id
     * @param deviceName         : user name
     * @param startTime        : start time
     * @param endTime          : end time
     * @param currentPage      : current page
     * @param perPage          : per page
     * @return
     */
    @Override
    public TotalTimeStatisticsResponse getStatistics(String sortBy, String order, String deviceType, String deviceName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {

        TotalTimeStatisticsResponse response = new TotalTimeStatisticsResponse();
        //categoryUser = authService.getDataCategoryUserList();
        //.... Get Total Statistics
        String strQuery = makeQuery(deviceType, deviceName, startTime, endTime, statWidth);
        TotalTimeStatistics mainTimeStatics = getTotalStatistics(strQuery);

        //.... Get Detailed Statistics
        TotalTimeStatistics detailTimeStatics = getDetailedStatistics(strQuery, startTime, endTime);
        TreeMap<Long, TotalTimeStatistics> data = new TreeMap<>();
        List<String> nameList = new ArrayList<>();
        List<Long> timeList = new ArrayList<>();
        List<DetailTimeStatistics> statisticsList = mainTimeStatics.getDetailedStatistics();
        List<DetailTimeStatistics> detailStatisticsList = detailTimeStatics.getDetailedStatistics();
        for(int i = 0; i < detailStatisticsList.size(); i ++) {
            statisticsList.add(detailStatisticsList.get(i));
        }
        for(int i = 0; i < statisticsList.size(); i ++) {
            String name = statisticsList.get(i).getUserName();
            long time = statisticsList.get(i).getTime();
            if(!nameList.contains(name)) {
                nameList.add(name);
            }
            if(!timeList.contains(time)) {
                timeList.add(time);
            }
        }


        List<DetailTimeStatistics> totalStatistics = new ArrayList<>();
        for(int i = 0; i < nameList.size(); i ++) {
            DetailTimeStatistics statistics = new DetailTimeStatistics();
            statistics.setUserName(nameList.get(i));
            long workingTime = 0;
            for(int j = 0; j < statisticsList.size(); j ++) {
                if(nameList.get(i).equals(statisticsList.get(j).getUserName())) {
                    workingTime += statisticsList.get(j).getWorkingTime();
                }
            }
            statistics.setWorkingTime(workingTime);
            totalStatistics.add(statistics);
        }
        TotalTimeStatistics totalTimeStatistics = new TotalTimeStatistics();
        totalTimeStatistics.setDetailedStatistics(totalStatistics);
        response.setTotalStatistics(totalTimeStatistics);

        for(int id = 0; id < timeList.size(); id ++) {
            long time = timeList.get(id);
            List<DetailTimeStatistics> detailTimeStatistics = new ArrayList<>();
            for(int i = 0; i < nameList.size(); i ++) {
                DetailTimeStatistics statistics = new DetailTimeStatistics();
                statistics.setUserName(nameList.get(i));
                long workingTime = 0;
                for(int j = 0; j < statisticsList.size(); j ++) {
                    if(nameList.get(i).equals(statisticsList.get(j).getUserName()) && time == statisticsList.get(j).getTime()) {
                        workingTime += statisticsList.get(j).getWorkingTime();
                    }
                }
                statistics.setWorkingTime(workingTime);
                detailTimeStatistics.add(statistics);
            }
            TotalTimeStatistics subTotalStatistics = new TotalTimeStatistics();
            subTotalStatistics.setDetailedStatistics(detailTimeStatistics);
            data.put(time, subTotalStatistics);

        }
        try {
            Map<String, Object> paginatedResult = getPaginatedList(data, currentPage, perPage);
            response.setFrom(Utils.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Utils.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Long, TotalTimeStatistics>) paginatedResult.get("list"));
        } catch (Exception e) {
            response.setDetailedStatistics(data);
        }

        if (perPage != null && currentPage != null) {
            response.setPer_page(perPage);
            response.setCurrent_page(currentPage);
            try {
                response.setTotal(data.size());
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
    private TotalTimeStatistics getTotalStatistics(String query) {

        String temp = query;
        temp = temp + ", (d.DEVICE_TYPE)";
        temp = temp.replace(":detail", "d.DEVICE_TYPE");

        temp = temp + " ORDER BY d.DEVICE_TYPE ASC ";

        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> resultTotal = jpaQuery.getResultList();

        TotalTimeStatistics record = new TotalTimeStatistics();
        List<DetailTimeStatistics> detailTimeStatistics = new ArrayList<>();
        DetailTimeStatistics scanStatics = new DetailTimeStatistics();
        scanStatics.setUserName(SysDevice.DeviceType.SECURITY);
        scanStatics.setWorkingTime(0);

        DetailTimeStatistics judgeStatics = new DetailTimeStatistics();
        judgeStatics.setUserName(SysDevice.DeviceType.JUDGE);
        judgeStatics.setWorkingTime(0);

        DetailTimeStatistics handStatics = new DetailTimeStatistics();
        handStatics.setUserName(SysDevice.DeviceType.MANUAL);
        handStatics.setWorkingTime(0);

        detailTimeStatistics.add(scanStatics);
        detailTimeStatistics.add(judgeStatics);
        detailTimeStatistics.add(handStatics);


        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            DetailTimeStatistics timeStatistics = initModelFromObject(item);
            if(timeStatistics.getUserName() != null) {
                for(int j = 0; j < detailTimeStatistics.size(); j ++) {
                    if(detailTimeStatistics.get(j).getUserName().equals(timeStatistics.getUserName())) {
                        detailTimeStatistics.set(j, timeStatistics);
                    }
                }
            }
        }
        record.setDetailedStatistics(detailTimeStatistics);
        return record;
    }

    /**
     * Get statistics by statistics width
     * @param query
     * @param startTime : start time
     * @param endTime   : endtime
     * @return
     */
    private TotalTimeStatistics getDetailedStatistics(String query, Date startTime, Date endTime) {

        String temp = query;
        temp = temp + ", (r.DEVICE_ID)";
        temp = temp.replace(":detail", "d.DEVICE_NAME");
        temp = temp + " ORDER BY d.DEVICE_NAME ASC ";

        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> result = jpaQuery.getResultList();
        TotalTimeStatistics record = new TotalTimeStatistics();
        record.setDetailedStatistics(new ArrayList<>());

        for (int i = 0; i < result.size(); i++) {
            Object[] item = (Object[]) result.get(i);
            DetailTimeStatistics timeStatistics = initModelFromObject(item);
            if(timeStatistics.getUserName() != null) {
                record.getDetailedStatistics().add(timeStatistics);
            }
        }
        return record;
    }

    /**
     * Get paginated list using current pang and per page
     * @param list
     * @param currentPage
     * @param perPage
     * @return
     */
    private Map<String, Object> getPaginatedList(TreeMap<Long, TotalTimeStatistics> list, Integer currentPage, Integer perPage) {

        HashMap<String, Object> paginationResult = new HashMap<String, Object>();
        TreeMap<Long, TotalTimeStatistics> subList = new TreeMap<>();

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
        for (Map.Entry<Long, TotalTimeStatistics> entry : list.entrySet()) {
            if (index >= from - 1 && index <= to - 1) {
                subList.put(entry.getKey(), entry.getValue());
            }
            index++;
        }

        paginationResult.put("list", subList);

        return paginationResult;
    }



    /**
     * build entire query
     * @param deviceType
     * @param deviceName
     * @param startTime
     * @param endTime
     * @return
     */
    private String makeQuery(String deviceType, String deviceName, Date startTime, Date endTime, String groupBy) {

        String strQuery = getSelectQuery(groupBy) + getJoinQuery();

        strQuery = strQuery +  " " + getWhereCause(deviceType, deviceName, startTime, endTime);
        strQuery = strQuery + " " + "GROUP BY  hour(r.REGISTER_TIME) ";
//        strQuery = strQuery.replace(":whereJudge", getWhereCauseJudge(modeId, userName, startTime, endTime));
//        strQuery = strQuery.replace(":whereHand", getWhereCauseHand(modeId, userName, startTime, endTime));

        return strQuery;
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
                "\t (r.REGISTER_TIME) as time,\n" +
                "\t sum(TIMESTAMPDIFF(SECOND,REGISTER_TIME,IF ( ISNULL(UNREGISTER_TIME) , NOW(), UNREGISTER_TIME ))),\n" +
                "\t :detail \n" +
                "\t FROM ser_device_register r \n";
    }

    /**
     * get join query part
     * @return
     */
    private String getJoinQuery() {

        return "\tLEFT JOIN \n" +
                "\tsys_device d ON r.DEVICE_ID = d.DEVICE_ID \n";
    }

    /**
     * get where cause for scan statistics
     * @param deviceType
     * @param deviceName
     * @param startTime
     * @param endTime
     * @return
     */
    private String getWhereCause(String deviceType, String deviceName, Date startTime, Date endTime) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if (deviceType != null) {
            whereCause.add("d.DEVICE_TYPE = '" + deviceType + "'");
        }
        if (startTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(startTime);
            whereCause.add("r.REGISTER_TIME >= '" + strDate + "'");
        }
        if (endTime != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(endTime);
            whereCause.add("r.UNREGISTER_TIME <= '" + strDate + "'");
        }
        if (deviceName != null && !deviceName.isEmpty()) {
            whereCause.add("d.DEVICE_NAME like '%" + deviceName + "%' ");
        }

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
            record.setTime(Utils.parseLong(item[0]));
            record.setWorkingTime(Utils.parseLong(item[1]));
            record.setUserName(item[2].toString());
        } catch (Exception e) { }

        return record;
    }
}