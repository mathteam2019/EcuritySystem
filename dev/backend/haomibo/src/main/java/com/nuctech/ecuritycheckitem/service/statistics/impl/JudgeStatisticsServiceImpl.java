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
import javax.validation.constraints.Max;
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
        JudgeStatisticsPaginationResponse response = new JudgeStatisticsPaginationResponse();

        String strQuery = makeQuery().replace(":whereJudge", getWhereCauseJudge(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));


        List<String> timeKeyList = Utils.getTimeKeyByStatistics(statWidth);

        String groupBy = timeKeyList.get(0) + "(groupby)";
        if(timeKeyList.size() > 1) {
            for(int i = 1; i < timeKeyList.size(); i ++) {
                groupBy = groupBy + ", SPACE(1)," +  "LPAD(" + timeKeyList.get(i) + "(groupby), 2, 0)";
            }
            groupBy = "CONCAT(" + groupBy + ")";
        }

        String judgeGroupBy = groupBy.replace("groupby", "JUDGE_START_TIME");


        String temp = strQuery;
        temp = temp.replace(":judgeGroupBy", judgeGroupBy);

        temp = temp + " \tWHERE (IFNULL(totalProcess, 0) + IFNULL(total, 0)) > 0 ";

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
        List<JudgeStatisticsResponseModel> detailedStatistics = getDetailedStatistics(temp, statWidth, currentPage, perPage, false);

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
    public JudgeStatisticsPaginationResponse getChartStatistics(Long fieldId, Long deviceId, Long userCategory, String userName,
                                                                Date startTime, Date endTime, String statWidth) {
        JudgeStatisticsPaginationResponse response = new JudgeStatisticsPaginationResponse();

        String strQuery = makeQuery().replace(":whereJudge", getWhereCauseJudge(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));


        String groupBy = statWidth + "(groupby)";

        String judgeGroupBy = groupBy.replace("groupby", "JUDGE_START_TIME");


        String temp = strQuery;
        temp = temp.replace(":judgeGroupBy", judgeGroupBy);

//        temp = temp + " WHERE (IFNULL(totalScan, 0) + IFNULL(validScan, 0) + IFNULL(passedScan, 0) + " +
//                "IFNULL(alarmScan, 0) + IFNULL(totalJudge, 0) + IFNULL(suspictionJudge, 0) + " +
//                "IFNULL(noSuspictionJudge, 0) + IFNULL(totalHand, 0) + " +
//                "IFNULL(seizureHand, 0) + IFNULL(noSeizureHand, 0)) > 0";


        //.... Get Detailed Statistics
        List<JudgeStatisticsResponseModel> detailedStatistics = getDetailedStatistics(temp, statWidth, 0, 0, true);
        response.setDetailedStatistics(detailedStatistics);
        return response;
    }
    @Override
    public JudgeStatisticsResponseModel getTotalStatistics(Long fieldId, Long deviceId, Long userCategory, String userName,
                                                           Date startTime, Date endTime, String statWidth) {
        String strQuery = makeQuery().replace(":whereJudge", getWhereCauseJudge(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth));
        strQuery = strQuery.replace(":selectQuery", getMainSelect());

        JudgeStatisticsResponseModel totalStatistics = getTotalStatistics(strQuery);
        return totalStatistics;
    }

    /**
     * Get total statistics amount
     *
     * @param query
     * @return
     */
    private JudgeStatisticsResponseModel getTotalStatistics(String query) {

        String temp = query.replace(":judgeGroupBy", "1");

        Query jpaQuery = entityManager.createNativeQuery(temp);
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
     * Get statistics by statistics width
     *
     * @param query
     * @return
     */
    private List<JudgeStatisticsResponseModel> getDetailedStatistics(String query, String statWidth, Integer currentPage, Integer perPage, boolean isChart) {


        String temp = query;

        temp = temp + " ORDER BY t0.q";
        if(isChart == false) {
            int start = currentPage * perPage;
            temp = temp + " LIMIT " + start +", " + perPage;
        }



        temp = temp.replace(":selectQuery", getMainSelect());


        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> result = jpaQuery.getResultList();
        List<JudgeStatisticsResponseModel> data = new ArrayList<>();

        SerPlatformCheckParams systemConstants = new SerPlatformCheckParams();
        try {
            List<SerPlatformCheckParams> list = serPlatformCheckParamRepository.findAll();
            systemConstants = list.get(0);
        } catch (Exception e) {
        }
        if (systemConstants.getJudgeProcessingTime() == null) {
            systemConstants.setJudgeProcessingTime((long) 0);
        }

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            JudgeStatisticsResponseModel record = initModelFromObject(item);
            if(isChart == false) {
                record.setTime(Utils.formatDateByStatisticWidth(statWidth, record.getTime()));
            }
            record.setLimitedArtificialDuration(systemConstants.getJudgeProcessingTime());

            data.add(record);
        }

        return data;
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
    private String getWhereCauseJudge(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {

        List<String> whereCause = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

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


//        if(categoryUser.isAll() == false) {
//            List<Long> idList = categoryUser.getUserIdList();
//            String idListStr = StringUtils.join(idList, ",");
//            whereCause.add("SCAN_POINTSMAN_ID in (" + idListStr + ") ");
//        }



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
                "\t\t:judgeGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\thistory_process  UNION\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:judgeGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\thistory_finish\n" +
                "\t\t) AS t00 \n" +
                "\t) AS t0 ";
    }

    /**
     * Get query for whole join
     *
     * @return
     */
    private String getJoinQuery() {

        return getJudgeJoinQuery();

    }

    /**
     * get query for judge join query
     *
     * @return
     */
    private String getJudgeJoinQuery() {


        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\tsum( IF (JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + ", 1, 0 ) ) AS artificialJudgeProcess,\n" +
                "\tsum( IF ( ASSIGN_JUDGE_TIMEOUT = '" + SerJudgeGraph.AssignTimeout.TRUE + "', 1, 0 ) ) AS assignResultProcess,\n" +
                "\tsum( IF ( JUDGE_USER_ID = " + Constants.DEFAULT_SYSTEM_USER + " AND ASSIGN_JUDGE_DEVICE_ID IS NOT NULL " + " AND JUDGE_TIMEOUT = '" + SerJudgeGraph.JudgeTimeout.TRUE +"', 1, 0 ) ) AS judgeTimeoutProcess,\n" +
                "\tsum( IF ( (ASSIGN_JUDGE_ID IS NULL OR ASSIGN_JUDGE_TIMEOUT = '" + SerJudgeGraph.AssignTimeout.FALSE + "') AND ASSIGN_JUDGE_DEVICE_ID IS NULL " + ", 1, 0 ) ) AS atrResultProcess,\n" +
                "\tsum( IF ( SCAN_ATR_RESULT = '" + SerScan.ATRResult.TRUE + "' " +
                " AND JUDGE_RESULT = '" + SerJudgeGraph.Result.TRUE + "', 1, 0 ) ) AS suspictionProcess,\n" +
                "\tsum( IF ( SCAN_ATR_RESULT = '" + SerScan.ATRResult.FALSE + "' " +
                " OR JUDGE_RESULT = '" + SerJudgeGraph.Result.FALSE + "', 1, 0 ) ) AS noSuspictionProcess,\n" +
                "\tcount( JUDGE_ID ) AS totalProcess ,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ) AS avgDurationProcess,\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ) AS maxDurationProcess,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ) AS minDurationProcess,\n" +
                "\t\n" +
                "\tAVG( CASE WHEN JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ELSE NULL END ) \n" +
                "\t AS artificialAvgDurationProcess,\n" +
                "\tMAX( CASE WHEN JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ELSE NULL END ) AS artificialMaxDurationProcess,\n" +
                "\tMIN( CASE WHEN JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ELSE NULL END ) AS artificialMinDurationProcess,\n" +
                "\t\t:judgeGroupBy AS q11 \n" +
                "\tFROM\n" +
                "\t\thistory_process  \n" +
                "\t:whereJudge\t" +
                "\tGROUP BY\n" +
                "\t\tq11 \n" +
                "\t) AS t11 ON t0.q = t11.q11\t" +
                "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\tsum( IF (JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + ", 1, 0 ) ) AS artificialJudge,\n" +
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
                "\tMIN( CASE WHEN JUDGE_USER_ID != " + Constants.DEFAULT_SYSTEM_USER + " THEN TIMESTAMPDIFF( SECOND, JUDGE_START_TIME, JUDGE_END_TIME ) ELSE NULL END ) AS artificialMinDuration,\n" +
                "\t\t:judgeGroupBy AS q12 \n" +
                "\tFROM\n" +
                "\t\thistory_finish  \n" +
                "\t:whereJudge\t" +
                "\tGROUP BY\n" +
                "\t\tq12 \n" +
                "\t) AS t12 ON t0.q = t12.q12\t";
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
            Long artififcialJudgeProcess = Utils.parseLong(item[1]);
            Long assignTimeoutProcess = Utils.parseLong(item[2]);
            Long judgeTimeoutProcess = Utils.parseLong(item[3]);
            Long atrResultProcess = Utils.parseLong(item[4]);
            Long suspictionProcess = Utils.parseLong(item[5]);
            Long noSuspictionProcess = Utils.parseLong(item[6]);
            Long totalProcess = Utils.parseLong(item[7]);
            Double avgDurationProcess = Utils.parseDouble(item[8]);
            Double maxDurationProcess = Utils.parseDouble(item[9]);
            Double minDuratinoProcess = Utils.parseDouble(item[10]);
            Double avgArtificialDurationProcess = Utils.parseDouble(item[11]);
            Double maxArtificialDurationProcess = Utils.parseDouble(item[12]);
            Double minAtificialDuratinoProcess = Utils.parseDouble(item[13]);


            Long artififcialJudgeFinish = Utils.parseLong(item[15]);
            Long assignTimeoutFinish = Utils.parseLong(item[16]);
            Long judgeTimeoutFinish = Utils.parseLong(item[17]);
            Long atrResultFinish = Utils.parseLong(item[18]);
            Long suspictionFinish = Utils.parseLong(item[19]);
            Long noSuspictionFinish = Utils.parseLong(item[20]);
            Long totalFinish = Utils.parseLong(item[21]);
            Double avgDurationFinish = Utils.parseDouble(item[22]);
            Double maxDurationFinish = Utils.parseDouble(item[23]);
            Double minDuratinoFinish = Utils.parseDouble(item[24]);
            Double avgArtificialDurationFinish = Utils.parseDouble(item[25]);
            Double maxArtificialDurationFinish = Utils.parseDouble(item[26]);
            Double minAtificialDuratinoFinish = Utils.parseDouble(item[27]);

            Double maxDuration = maxDurationProcess;
            Double maxArtificialDuration = maxArtificialDurationProcess;
            Double minDuration = minDuratinoProcess;
            Double minArtificialDuration = minAtificialDuratinoProcess;
            if(maxDuration < maxDurationFinish) {
                maxDuration = maxDurationFinish;
            }
            if(maxArtificialDuration < maxArtificialDurationFinish) {
                maxArtificialDuration = maxArtificialDurationFinish;
            }
            if(minDuration > minDuratinoFinish) {
                maxDuration = minDuratinoFinish;
            }
            if(minArtificialDuration > minAtificialDuratinoFinish) {
                minArtificialDuration = minAtificialDuratinoFinish;
            }

            record.setArtificialJudge(artififcialJudgeProcess + artififcialJudgeFinish);
            record.setAssignTimeout(assignTimeoutProcess + assignTimeoutFinish);
            record.setJudgeTimeout(judgeTimeoutProcess + judgeTimeoutFinish);
            record.setAtrResult(atrResultProcess + atrResultFinish);
            record.setSuspiction(suspictionProcess + suspictionFinish);
            record.setNoSuspiction(noSuspictionFinish + noSuspictionProcess);
            record.setTotal(totalFinish + totalProcess);
            record.setAvgDuration(0);
            record.setMaxDuration(maxDuration);
            record.setMinDuration(minDuration);
            record.setAvgArtificialJudgeDuration(0);
            record.setMaxArtificialJudgeDuration(maxArtificialDuration);
            record.setMinArtificialJudgeDuration(minArtificialDuration);
            record.setArtificialResultRate(0);
            record.setAssignTimeoutResultRate(0);
            record.setJudgeTimeoutResultRate(0);
            record.setSuspictionRate(0);
            record.setNoSuspictionRate(0);
            record.setScanResultRate(0);
            if (record.getTotal() > 0) {
                record.setAvgDuration((avgDurationProcess * totalProcess + avgDurationFinish * totalFinish) / (totalFinish + totalProcess));
                record.setAvgArtificialJudgeDuration((avgArtificialDurationProcess * totalProcess + avgArtificialDurationFinish * totalFinish) / (totalFinish + totalProcess));
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
