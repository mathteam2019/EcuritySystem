package com.nuctech.ecuritycheckitem.models.db;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "history")
public class History extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID", length = 20)
    Long historyId;

    @Column(name = "TASK_ID", length = 20)
    Long taskId;

    @Column(name = "MODE", length = 20)
    Long mode;

    @Column(name = "SCAN_ID", length = 20)
    Long scanId;

    @Column(name = "SCAN_WORKFLOW_ID", length = 20)
    Long scanWorkflowId;

    @Column(name = "SCAN_DEVICE_ID", length = 20)
    Long scanDeviceId;

    @Column(name = "SCAN_IMAGE_ID", length = 20)
    Long scanImageId;

    @Column(name = "SCAN_ATR_RESULT", length = 10)
    String scanAtrResult;

    @Column(name = "SCAN_FOOT_ALARM", length = 10)
    String scanFootAlarm;

    @Column(name = "SCAN_START_TIME", nullable = false)
    Date scanStartTime;

    @Column(name = "SCAN_END_TIME", length = 30)
    Date scanEndTime;

    @Column(name = "SCAN_POINTSMAN_ID", length = 20)
    Long scanPointsmanId;

    @Column(name = "SCAN_POINTSMAN_NAME", length = 50)
    String scanPointsmanName;

//    @Column(name = "ASSIGNSCAN_ID", length = 20)
//    Long assignscanId;

    @Column(name = "ASSIGN_WORKFLOW_ID", length = 20)
    Long assignWorkflowId;

    @Column(name = "ASSIGN_USER_ID", length = 20)
    Long assignUserId;

    @Column(name = "ASSIGN_USER_NAME", length = 50)
    String assignUserName;

    @Column(name = "ASSIGN_JUDGE_DEVICE_ID", length = 10)
    String assignJudgeDeviceId;

    @Column(name = "ASSIGN_HAND_DEVICE_ID", length = 10)
    String assignHandDeviceId;

    @Column(name = "ASSIGN_START_TIME", nullable = false)
    Date assignStartTime;

    @Column(name = "ASSIGN_END_TIME", nullable = false)
    Date assignEndTime;

    @Column(name = "ASSIGN_TIMEOUT", length = 10)
    String assignTimeout;

    @Column(name = "ASSIGN_STATUS", length = 10)
    String assignStatus;

    @Column(name = "JUDGE_ID", length = 20)
    Long judgeId;

    @Column(name = "JUDGE_WORKFLOW_ID", length = 20)
    Long judgeWorkflowId;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    Long judgeDeviceId;

    @Column(name = "JUDGE_RESULT", length = 10)
    String judgeResult;

    @Column(name = "JUDGE_TIMEOUT", length = 10)
    String judgeTimeout;

    @Column(name = "HAND_EXAMINATION_ID", length = 20)
    Long handExaminationId;

    @Column(name = "HAND_WORKFLOW_ID", length = 20)
    Long handWorkflowId;

    @Column(name = "HAND_DEVICE_ID", length = 20)
    Long handDeviceId;

    @Column(name = "HAND_RESULT", length = 10)
    String handResult;

    @Column(name = "HAND_START_TIME", nullable = false)
    Date handStartTime;

    @Column(name = "HAND_END_TIME", nullable = false)
    Date handEndTime;

    @Column(name = "HAND_USER_ID", length = 20)
    Long handUserId;

    @Column(name = "HAND_TASK_RESULT", length = 10)
    String handTaskResult;

    @Column(name = "HAND_GOODS", length = 255)
    String handGoods;

    @Column(name = "HAND_GOODS_GRADE", length = 10)
    String handGoodsGrade;

    @Column(name = "HAND_COLLECT_SIGN", length = 10)
    String handCollectSign;

    @Column(name = "HAND_ATTACHED_ID", length = 20)
    Long handAttachedId;

    @Column(name = "HAND_COLLECT_LABEL", length = 10)
    String handCollectLabel;

    @Column(name = "HAND_APPRAISE", length = 10)
    String handAppraise;

    @Column(name = "JUDGE_START_TIME", nullable = false)
    Date judgeStartTime;

    @Column(name = "JUDGE_END_TIME", nullable = false)
    Date judgeEndTime;

    @Column(name = "JUDGE_USER_ID", length = 20)
    Long judgeUserId;

    @Column(name = "JUDGE_ASSIGN_TIMEOUT", length = 10)
    String judgeAssignTimeout;

    @Column(name = "JUDGE_STATUS", length = 10)
    String judgeStatus;

    @OneToOne()
    @JoinColumn(name = "MODE", referencedColumnName = "MODE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysWorkMode workMode;

    @OneToOne()
    @JoinColumn(name = "SCAN_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysDevice scanDevice;

    @OneToOne()
    @JoinColumn(name = "SCAN_POINTSMAN_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysUser scanPointsman;

    @OneToOne()
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysDevice judgeDevice;

    @OneToOne()
    @JoinColumn(name = "JUDGE_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysUser judgeUser;

    @OneToOne()
    @JoinColumn(name = "HAND_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysDevice handDevice;

    @OneToOne()
    @JoinColumn(name = "HAND_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysUser handUser;

    @OneToOne()
    @JoinColumn(name = "SCAN_IMAGE_ID", referencedColumnName = "IMAGE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SerImage scanImage;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SerTask task;

}
