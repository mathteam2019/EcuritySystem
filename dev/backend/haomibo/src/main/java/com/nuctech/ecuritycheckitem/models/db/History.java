package com.nuctech.ecuritycheckitem.models.db;


import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.jdo.annotations.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
//@JsonFilter(ModelJsonFilters.FILTER_SYS_ROLE)
@Table(name = "history")
public class History extends BaseEntity implements Serializable {
    @Column(name = "HISTORY_ID", length = 20)
    Long history_id;

    @Column(name = "TASK_ID", length = 20)
    Long task_id;

    @Column(name = "MODE", length = 20)
    Long mode;

    @Column(name = "SCAN_ID", length = 20)
    Long scan_id;

    @Column(name = "SCAN_WORKFLOW_ID", length = 20)
    Long scan_workflow_id;

    @Column(name = "SCAN_DEVICE_ID", length = 20)
    Long scan_device_id;

    @Column(name = "SCAN_IMAGE_ID", length = 20)
    Long scan_image_id;

    @Column(name = "SCAN_ATR_RESULT", length = 10)
    String scan_atr_result;

    @Column(name = "SCAN_FOOT_ALARM", length = 10)
    String scan_foot_alarm;

    @Column(name = "SCAN_START_TIME", nullable = false)
    Date scan_start_time;

    @Column(name = "SCAN_END_TIME", length = 30)
    Date scan_end_time;

    @Column(name = "SCAN_POINTSMAN_ID", length = 20)
    Long scan_pointsman_id;

    @Column(name = "SCAN_POINTSMAN_NAME", length = 50)
    String scan_pointsman_name;

    @Column(name = "ASSIGNSCAN_ID", length = 20)
    Long assignscan_id;

    @Column(name = "ASSIGN_WORKFLOW_ID", length = 20)
    Long assign_workflow_id;

    @Column(name = "ASSIGN_USER_ID", length = 20)
    Long assign_user_id;

    @Column(name = "ASSIGN_USER_NAME", length = 50)
    String assign_user_name;

    @Column(name = "ASSIGN_JUDGE_DEVICE_ID", length = 10)
    String assign_judge_device_id;

    @Column(name = "ASSIGN_HAND_DEVICE_ID", length = 10)
    String assign_hand_device_id;

    @Column(name = "ASSIGN_START_TIME", nullable = false)
    Date assign_start_time;

    @Column(name = "ASSIGN_END_TIME", nullable = false)
    Date assign_end_time;

    @Column(name = "ASSIGN_TIMEOUT", length = 10)
    String assign_timeout;

    @Column(name = "ASSIGN_STATUS", length = 10)
    String assign_status;

    @Column(name = "CREATEDBY", length = 20)
    Long createdby;

    @Column(name = "CREATEDTIME", nullable = false)
    Date createdtime;

    @Column(name = "EDITEDBY", length = 20)
    Long editedby;

    @Column(name = "EDITEDTIME", nullable = false)
    Date editedtime;

    @Column(name = "NOTE", length = 500)
    String note;

    @Column(name = "JUDGE_ID", length = 20)
    Long judge_id;

    @Column(name = "JUDGE_WORKFLOW_ID", length = 20)
    Long judge_workflow_id;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    Long judge_device_id;

    @Column(name = "JUDGE_RESULT", length = 10)
    String judge_result;

    @Column(name = "JUDGE_TIMEOUT", length = 10)
    String judge_timeout;

    @Column(name = "HAND_EXAMINATION_ID", length = 20)
    Long hand_examination_id;

    @Column(name = "HAND_WORKFLOW_ID", length = 20)
    Long hand_workflow_id;

    @Column(name = "HAND_DEVICE_ID", length = 20)
    Long hand_device_id;

    @Column(name = "HAND_RESULT", length = 10)
    String hand_result;

    @Column(name = "HAND_START_TIME", nullable = false)
    Date hand_start_time;

    @Column(name = "HAND_END_TIME", nullable = false)
    Date hand_end_time;

    @Column(name = "HAND_USER_ID", length = 20)
    Long hand_user_id;

    @Column(name = "HAND_TASK_RESULT", length = 10)
    String hand_task_result;

    @Column(name = "HAND_GOODS", length = 255)
    String hand_goods;

    @Column(name = "HAND_GOODS_GRADE", length = 10)
    String hand_goods_grade;

    @Column(name = "HAND_COLLECT_SIGN", length = 10)
    String hand_collect_sign;

    @Column(name = "HAND_ATTACHED_ID", length = 20)
    Long hand_attached_id;

    @Column(name = "HAND_COLLECT_LABEL", length = 10)
    String hand_collect_label;

    @Column(name = "HAND_APPRAISE", length = 10)
    String hand_appraise;

    @Column(name = "JUDGE_START_TIME", nullable = false)
    Date judge_start_time;

    @Column(name = "JUDGE_END_TIME", nullable = false)
    Date judge_end_time;

    @Column(name = "JUDGE_USER_ID", length = 20)
    Long judge_user_id;

    @Column(name = "JUDGE_ASSIGN_TIMEOUT", length = 10)
    String judge_assign_timeout;

    @Column(name = "JUDGE_STATUS", length = 10)
    String judge_status;

}
