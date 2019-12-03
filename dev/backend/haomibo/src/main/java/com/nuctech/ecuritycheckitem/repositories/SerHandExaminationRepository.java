package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerHandExaminationRepository extends
        JpaRepository<SerHandExamination, Integer>,
        PagingAndSortingRepository<SerHandExamination, Integer>,
        QuerydslPredicateExecutor<SerHandExamination> {

    @Query(value = "SELECT\n" +
            "\n" +
            "\tMONTH(h.HAND_START_TIME) as time,\n" +
            "\tcount( HAND_EXAMINATION_ID ) AS total,\n" +
            "\tsum( IF ( h.HAND_RESULT LIKE 'true', 1, 0 ) ) AS seizure,\n" +
            "\tsum( IF ( h.HAND_RESULT LIKE 'false', 1, 0 ) ) AS noSeizure,\n" +
            "\tsum( IF ( s.SCAN_INVALID like 'true', 1, 0)) as totalJudge,\n" +
            "\tsum( IF ( c.HAND_APPRAISE LIKE 'missing', 1, 0 ) ) AS missingReport,\n" +
            "\tsum( IF ( c.HAND_APPRAISE LIKE 'mistake', 1, 0 ) ) AS falseReport,\n" +
            "\t\n" +
            "\tsum( IF ( j.JUDGE_TIMEOUT like 'weikong', 1, 0)) as artificialJudge,\n" +
            "\tsum( IF ( j.JUDGE_TIMEOUT like 'weikong' and c.HAND_APPRAISE like 'missing', 1, 0)) as artificialJudgeMissing,\n" +
            "\tsum( IF ( j.JUDGE_TIMEOUT like 'weikong' and c.HAND_APPRAISE like 'mistake', 1, 0)) as artificialJudgeMistake,\n" +
            "\t\n" +
            "\tsum( IF ( s.SCAN_INVALID like 'true' and wf.MODE_ID = 11 and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true', 1, 0)) as intelligenceJudge,\n" +
            "\tsum( IF ( s.SCAN_INVALID like 'true' and wf.MODE_ID = 11 and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true' and c.HAND_APPRAISE like 'missing', 1, 0)) as intelligenceJudgeMissing,\n" +
            "\tsum( IF ( s.SCAN_INVALID like 'true' and wf.MODE_ID = 11 and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true' and c.HAND_APPRAISE like 'mistake', 1, 0)) as intelligenceJudgeMistake,\n" +
            "\t\n" +
            "\t\n" +
            "\tMAX( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS maxDuration,\n" +
            "\tMIN( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS minDuration,\n" +
            "\tAVG( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS avgDuration \n" +
            "\t\n" +
            "FROM\n" +
            "\tser_hand_examination h\n" +
            "\tLEFT join ser_login_info l on h.HAND_DEVICE_ID = l.DEVICE_ID\n" +
            "\tLEFT JOIN ser_task t ON h.TASK_ID = t.task_id\n" +
            "\tLEFT JOIN ser_check_result2 c ON t.TASK_ID = c.task_id\n" +
            "\tleft join ser_judge_graph j on t.TASK_ID = j.TASK_ID\n" +
            "\tleft join ser_scan s on t.TASK_ID = s.TASK_ID\n" +
            "\tleft join ser_assign a on t.task_id = a.task_id\n" +
            "\tleft join sys_workflow wf on t.WORKFLOW_ID = wf.workflow_id\n" +
            "GROUP BY MONTH(h.HAND_START_TIME)\n" +
            "\t\n" +
            "\t", nativeQuery = true)
    public List<Object> getStatistics();


    @Query(value = "SELECT\n" +
            "\tMONTH (h.HAND_STAR_TIME) as time,\n" +
            "\tcount( HAND_EXAMINATION_ID ) AS total,\n" +
            "\tsum( IF ( h.HAND_RESULT LIKE 'true', 1, 0 ) ) AS seizure,\n" +
            "\tsum( IF ( h.HAND_RESULT LIKE 'false', 1, 0 ) ) AS noSeizure,\n" +
            "\tsum( IF ( s.SCAN_INVALID like 'true', 1, 0)) as totalJudge,\n" +
            "\tsum( IF ( c.HAND_APPRAISE LIKE 'missing', 1, 0 ) ) AS missingReport,\n" +
            "\tsum( IF ( c.HAND_APPRAISE LIKE 'mistake', 1, 0 ) ) AS falseReport,\n" +
            "\t\n" +
            "\tsum( IF ( ISNULL(j.JUDGE_TIMEOUT), 1, 0)) as artificialJudge,\n" +
            "\tsum( IF ( ISNULL(j.JUDGE_TIMEOUT) and c.HAND_APPRAISE like 'missing', 1, 0)) as artificialJudgeMissing,\n" +
            "\tsum( IF ( ISNULL(j.JUDGE_TIMEOUT) and c.HAND_APPRAISE like 'mistake', 1, 0)) as artificialJudgeMistake,\n" +
            "\t\n" +
            "\tsum( IF ( s.SCAN_INVALID like 'true' and wf.MODE_ID = 11 and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true', 1, 0)) as intelligenceJudgeMissing,\n" +
            "\tsum( IF ( s.SCAN_INVALID like 'true' and wf.MODE_ID = 11 and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true' and c.HAND_APPRAISE like 'missing', 1, 0)) as intelligenceJudgeMissing,\n" +
            "\tsum( IF ( s.SCAN_INVALID like 'true' and wf.MODE_ID = 11 and a.ASSIGN_TIMEOUT like 'true' and j.JUDGE_USER_ID = l.USER_ID and j.JUDGE_TIMEOUT like 'true' and c.HAND_APPRAISE like 'mistake', 1, 0)) as intelligenceJudgeMistake,\n" +
            "\t\n" +
            "\t\n" +
            "\tMAX( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS maxDuration,\n" +
            "\tMIN( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS minDuration,\n" +
            "\tAVG( TIMESTAMPDIFF( SECOND, h.HAND_START_TIME, h.HAND_END_TIME ) ) AS avgDuration \n" +
            "\t\n" +
            "FROM\n" +
            "\tser_hand_examination h\n" +
            "\tLEFT join ser_login_info l on h.HAND_DEVICE_ID = l.DEVICE_ID\n" +
            "\tLEFT JOIN ser_task t ON h.TASK_ID = t.task_id\n" +
            "\tLEFT JOIN ser_check_result2 c ON t.TASK_ID = c.task_id\n" +
            "\tleft join ser_judge_graph j on t.TASK_ID = j.TASK_ID\n" +
            "\tleft join ser_scan s on t.TASK_ID = s.TASK_ID\n" +
            "\tleft join ser_assign a on t.task_id = a.task_id\n" +
            "\tleft join sys_workflow wf on t.WORKFLOW_ID = wf.workflow_id\n" +
            "\t", nativeQuery = true)
    public List<Object> getEvaluateStatistics();

}