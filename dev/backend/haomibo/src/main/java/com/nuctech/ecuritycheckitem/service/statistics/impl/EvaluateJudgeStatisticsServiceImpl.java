/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（EvaluateJudgeStatisticsServiceImpl）
 * 文件名：	EvaluateJudgeStatisticsServiceImpl.java
 * 描述：	EvaluateJudgeStatisticsService implement
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.db.SysWorkMode;

import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeStatisticsPaginationResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.statistics.EvaluateJudgeStatisticsService;
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
public class EvaluateJudgeStatisticsServiceImpl implements EvaluateJudgeStatisticsService {

    @Autowired
    public EntityManager entityManager;

    @Autowired
    private AuthService authService;


    /**
     * get evaluate judge statistics
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
    public EvaluateJudgeStatisticsPaginationResponse getStatistics(String sortBy, String order, Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage) {


        StringBuilder queryBuilder = new StringBuilder();
        String groupBy = "hour";
        if (statWidth != null && !statWidth.isEmpty()) {
            groupBy = statWidth;
        }

        EvaluateJudgeStatisticsPaginationResponse response = new EvaluateJudgeStatisticsPaginationResponse();

        queryBuilder.append(getQueryForSelectPart(groupBy) + "FROM\n" + getQueryForJoin());
        List<String> whereCause = getWhereCause(fieldId, deviceId, userCategory, userName, startTime, endTime, statWidth);
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        response.setTotalStatistics(getTotalStatistics(queryBuilder.toString()));

        queryBuilder.append(" GROUP BY  " + groupBy + "(h.HAND_START_TIME)");
        TreeMap<Integer, EvaluateJudgeResponseModel> sorted = getDetailedStatistics(queryBuilder.toString(), statWidth, startTime, endTime);
        try {
            Map<String, Object> paginatedResult = getPaginatedList(sorted, statWidth, startTime, endTime, currentPage, perPage);
            response.setFrom(Utils.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Utils.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Integer, EvaluateJudgeResponseModel>) paginatedResult.get("list"));
        } catch (Exception e) {
            response.setDetailedStatistics(sorted);
        }

        if (perPage != null && currentPage != null) {
            response.setPer_page(perPage);
            response.setCurrent_page(currentPage);
            try {
                response.setTotal(sorted.size());
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
    private Map<String, Object> getPaginatedList(TreeMap<Integer, EvaluateJudgeResponseModel> sorted, String statWidth, Date startTime, Date endTime, Integer currentPage, Integer perPage) {

        Map<String, Object> result = new HashMap<>();
        TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics = new TreeMap<>();

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
     * Get total statistics amount
     *
     * @param query
     * @return
     */
    private EvaluateJudgeResponseModel getTotalStatistics(String query) {
        Query jpaQueryTotal = entityManager.createNativeQuery(query);
        EvaluateJudgeResponseModel record = new EvaluateJudgeResponseModel();

        List<Object> resultTotal = jpaQueryTotal.getResultList();
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
     * @param statWidth : (hour, day, week, month, quarter, year)
     * @param startTime : start time
     * @param endTime   : endtime
     * @return
     */
    private TreeMap<Integer, EvaluateJudgeResponseModel> getDetailedStatistics(String query, String statWidth, Date startTime, Date endTime) {
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();

        TreeMap<Integer, EvaluateJudgeResponseModel> data = new TreeMap<>();
        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = Utils.getKeyValuesforStatistics(statWidth, startTime, endTime);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) {
        }

        for (Integer i = keyValueMin; i <= keyValueMax; i++) {
            EvaluateJudgeResponseModel item = new EvaluateJudgeResponseModel();
            item.setTime(i);
            data.put(i, item);
        }

        for (int i = 0; i < result.size(); i++) {
            Object[] item = (Object[]) result.get(i);
            EvaluateJudgeResponseModel record = initModelFromObject(item);
            if (record.getTime() >= keyValueMin && record.getTime() <= keyValueMax) {
                data.put(record.getTime(), record);
            }
        }

        return data;
    }

    /**
     * query of select part
     *
     * @param groupBy : statistics width (hour, day, week, month, quarter, year)
     * @return
     */
    private String getQueryForSelectPart(String groupBy) {

        return "SELECT\n" +
                "\n" +
                groupBy +
                "\t (h.HAND_START_TIME) as time,\n" +
                "\tcount( HAND_EXAMINATION_ID ) AS total,\n" +
                "\tsum( IF ( h.HAND_RESULT LIKE '" + SerHandExamination.Result.TRUE + "' , 1, 0 ) ) AS seizure,\n" +
                "\tsum( IF ( h.HAND_RESULT LIKE '" + SerHandExamination.Result.FALSE + "' , 1, 0 ) ) AS noSeizure,\n" +
                "\tsum( IF ( s.SCAN_INVALID like '" + SerScan.Invalid.FALSE + "', 1, 0)) as totalJudge,\n" +
                "\tsum( IF ( c.HAND_APPRAISE LIKE '" + SerHandExamination.HandAppraise.MISTAKE + "', 1, 0 ) ) AS missingReport,\n" +
                "\tsum( IF ( c.HAND_APPRAISE2 LIKE '" + SerHandExamination.HandAppraise.MISSING + "', 1, 0 ) ) AS falseReport,\n" +
                "\t\n" +
                "\tsum( IF ( j.JUDGE_USER_ID != 10000, 1, 0)) as artificialJudge,\n" +
                "\tsum( IF ( j.JUDGE_USER_ID != 10000 and c.HAND_APPRAISE like '" + SerHandExamination.HandAppraise.MISTAKE + "', 1, 0)) as artificialJudgeMissing,\n" +
                "\tsum( IF ( j.JUDGE_USER_ID != 10000 and c.HAND_APPRAISE2 like '" + SerHandExamination.HandAppraise.MISSING + "', 1, 0)) as artificialJudgeMistake,\n" +
                "\t\n" +
                "\tsum( IF ( j.JUDGE_USER_ID = 10000, 1, 0)) as intelligenceJudge,\n" +
                "\tsum( IF ( j.JUDGE_USER_ID = 10000 and c.HAND_APPRAISE like '" + SerHandExamination.HandAppraise.MISTAKE + "', 1, 0)) as intelligenceJudgeMissing,\n" +
                "\tsum( IF ( j.JUDGE_USER_ID = 10000 and c.HAND_APPRAISE2 like '" + SerHandExamination.HandAppraise.MISSING + "', 1, 0)) as intelligenceJudgeMistake,\n" +
                "\t\n" +
                "\tMAX( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS maxDuration,\n" +
                "\tMIN( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS minDuration,\n" +
                "\tAVG( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS avgDuration \n" +
                "\t\n";

    }

    /**
     * Get query for join
     *
     * @return
     */
    private String getQueryForJoin() {
        return "\tser_hand_examination h\n" +
                "\tLEFT join sys_user u on h.HAND_USER_ID = u.USER_ID\n" +
                "\tINNER JOIN ser_task t ON h.TASK_ID = t.task_id\n" +
                "\tLEFT JOIN ser_check_result c ON t.TASK_ID = c.task_id\n" +
                "\tleft join ser_judge_graph j on t.TASK_ID = j.TASK_ID\n" +
                "\tleft join ser_scan s on t.TASK_ID = s.TASK_ID\n" +
                "\tleft join ( SELECT task_id, assign_id, assign_judge_device_id, ASSIGN_TIMEOUT FROM ser_assign WHERE ASSIGN_HAND_DEVICE_ID IS NOT NULL ) a on t.task_id = a.task_id\n" +
                "\tleft join sys_workflow wf on t.WORKFLOW_ID = wf.workflow_id\n" +
                "\tleft join sys_work_mode wm on wf.MODE_ID = wm.MODE_ID\n";
    }

    /**
     * Get where condition list
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
    private List<String> getWhereCause(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth) {

        List<String> whereCause = new ArrayList<String>();

        if (fieldId != null) {
            whereCause.add("t.SCENE = " + fieldId);
        }
        if (deviceId != null) {
            whereCause.add("t.DEVICE_ID = " + deviceId);
        }
        if (userName != null && !userName.isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + userName + "%'");
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
            whereCause.add("h.HAND_END_TIME <= '" + strDate + "'");
        }
        whereCause.add("s.SCAN_INVALID like '" + SerScan.Invalid.FALSE + "' ");
//        CategoryUser categoryUser = authService.getDataCategoryUserList();
//        if(categoryUser.isAll() == false) {
//            List<Long> idList = categoryUser.getUserIdList();
//            String idListStr = StringUtils.join(idList, ",");
//            whereCause.add("h.CREATEDBY in (" + idListStr + ") ");
//        }

        return whereCause;
    }

    /**
     * return a judge statistics record from a record of a query
     *
     * @param item
     * @return
     */
    private EvaluateJudgeResponseModel initModelFromObject(Object[] item) {
        EvaluateJudgeResponseModel record = new EvaluateJudgeResponseModel();
        try {
            record.setTime(Utils.parseInt(item[0].toString()));
            record.setTotal(Utils.parseLong(item[1].toString()));
            record.setSeizure(Utils.parseLong(item[2].toString()));
            record.setNoSeizure(Utils.parseLong(item[3].toString()));
            record.setTotalJudge(Utils.parseLong(item[4].toString()));
            record.setMissingReport(Utils.parseLong(item[5].toString()));
            record.setMistakeReport(Utils.parseLong(item[6].toString()));
            record.setArtificialJudge(Utils.parseLong(item[7].toString()));
            record.setArtificialJudgeMissing(Utils.parseLong(item[8].toString()));
            record.setArtificialJudgeMistake(Utils.parseLong(item[9].toString()));
            record.setIntelligenceJudge(Utils.parseLong(item[10].toString()));
            record.setIntelligenceJudgeMissing(Utils.parseLong(item[11].toString()));
            record.setIntelligenceJudgeMistake(Utils.parseLong(item[12].toString()));
            record.setMaxDuration(Utils.parseDouble(item[13].toString()));
            record.setMinDuration(Utils.parseDouble(item[14].toString()));
            record.setAvgDuration(Utils.parseDouble(item[15].toString()));
            record.setMissingReportRate(0);
            record.setMistakeReportRate(0);
            record.setArtificialJudgeMissingRate(0);
            record.setArtificialJudgeMistakeRate(0);
            record.setIntelligenceJudgeMissingRate(0);
            record.setIntelligenceJudgeMistakeRate(0);

            if (record.getTotal() > 0) {
                record.setMissingReportRate(record.getMissingReport() * 100 / (double) record.getTotal());
                record.setMistakeReportRate(record.getMistakeReport() * 100 / (double) record.getTotal());
            }
            if (record.getArtificialJudge() > 0) {
                record.setArtificialJudgeMissingRate(record.getArtificialJudgeMissing() * 100 / (double) record.getArtificialJudge());
                record.setArtificialJudgeMistakeRate(record.getArtificialJudgeMistake() * 100 / (double) record.getArtificialJudge());
            }
            if (record.getIntelligenceJudge() > 0) {
                record.setIntelligenceJudgeMissingRate(record.getIntelligenceJudgeMissing() * 100 / (double) record.getIntelligenceJudge());
                record.setIntelligenceJudgeMistakeRate(record.getIntelligenceJudgeMistake() * 100 / (double) record.getIntelligenceJudge());
            }
        } catch (Exception e) {
        }

        return record;
    }
}
