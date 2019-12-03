package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.TaskManagementController;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import com.nuctech.ecuritycheckitem.models.response.JudgeStatisticsResponseModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SerJudgeGraphRepository extends
        JpaRepository<SerJudgeGraph, Integer>,
        PagingAndSortingRepository<SerJudgeGraph, Integer>,
        QuerydslPredicateExecutor<SerJudgeGraph> {

    @Query(value = "SELECT\n" +
            "\t MONTH ( JUDGE_START_TIME ) Date,\n" +
            "\tcount( JUDGE_ID ) total,\n" +
            "\tsum( IF ( s.SCAN_ATR_RESULT LIKE 'true' AND g.JUDGE_RESULT LIKE 'true', 1, 0 ) ) suspiction,\n" +
            "\tsum( IF ( s.SCAN_ATR_RESULT LIKE 'false' AND g.JUDGE_RESULT LIKE 'false', 1, 0 ) ) noSuspiction,\n" +
            "\tsum( IF ( ( wf.MODE_ID = 11 OR wf.MODE_ID = 10 ), 1, 0 ) ) AS atrResult,\n" +
            "\tsum( IF ( s.SCAN_INVALID LIKE 'true' AND a.ASSIGN_TIMEOUT LIKE 'true', 1, 0 ) ) AS assignResult,\n" +
            "\tsum( IF ( g.judge_user_id != l.USER_ID, 1, 0 ) ) AS artificialResult,\n" +
            "\tmax( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS maxDuration,\n" +
            "\tmin( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS minDuration,\n" +
            "\tAVG( TIMESTAMPDIFF( SECOND, g.JUDGE_START_TIME, g.JUDGE_END_TIME ) ) AS avgDuration\n" +
            "\t\n" +
            "FROM\n" +
            "\tser_judge_graph g\n" +
            "\tleft join sys_user u on g.judge_user_id = u.user_id\n" +
            "\tLEFT JOIN ser_login_info l ON g.JUDGE_DEVICE_ID = l.DEVICE_ID\n" +
            "\tLEFT JOIN ser_task t ON g.TASK_ID = t.TASK_ID\n" +
            "\tLEFT JOIN sys_workflow wf ON t.WORKFLOW_ID = wf.WORKFLOW_ID\n" +
            "\tLEFT JOIN ser_scan s ON t.TASK_ID = s.TASK_ID\n" +
            "\tLEFT JOIN ser_assign a ON t.task_id = a.task_id \n" +
            "\t\n" +
            "WHERE\n" +
            "\ts.SCAN_INVALID LIKE 'true'\n" +
            "\tand g.JUDGE_START_TIME BETWEEN '2019-01-01 00:00:00' and '2019-12-31 00:00:00'\n" +
            "\tand g.JUDGE_END_TIME BETWEEN '2019-01-01 00:00:00' and '2019-12-31 00:00:00'\n" +
            "GROUP BY\n" +
            "\t MONTH ( JUDGE_START_TIME )", nativeQuery = true)
    public List<Object> getStatistics();


}
