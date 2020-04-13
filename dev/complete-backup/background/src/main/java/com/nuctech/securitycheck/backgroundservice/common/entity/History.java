package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * History
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "history")
public class History extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID", length = 20)
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private SerTask serTask;

    @ManyToOne
    @JoinColumn(name = "MODE")
    private SysWorkMode sysWorkMode;

    @ManyToOne
    @JoinColumn(name = "SCAN_ID")
    private SerScan serScan;

    @ManyToOne
    @JoinColumn(name = "SCAN_WORKFLOW_ID")
    private SysWorkflow sysWorkflow;

    @ManyToOne
    @JoinColumn(name = "SCAN_DEVICE_ID")
    private SysDevice scanDevice;

    @ManyToOne
    @JoinColumn(name = "SCAN_IMAGE_ID")
    private SerImage scanImage;

    @Column(name = "SCAN_INVALID", length = 10)
    private String scanInvalid;

    @Column(name = "SCAN_ATR_RESULT", length = 10)
    private String scanATRResult;

    @Column(name = "SCAN_FOOT_ALARM", length = 10)
    private String scanFootAlarm;

    @Column(name = "SCAN_START_TIME", nullable = false)
    private Date scanStartTime;

    @Column(name = "SCAN_END_TIME", nullable = false)
    private Date scanEndTime;

    @ManyToOne
    @JoinColumn(name = "SCAN_POINTSMAN_ID")
    private SysUser scanPointsman;

    @Column(name = "SCAN_POINTSMAN_NAME", length = 50)
    private String scanPointsmanName;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_JUDGE_ID")
    private SerAssign serAssignJudge;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_HAND_ID")
    private SerAssign serAssignHand;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_WORKFLOW_JUDGE_ID")
    private SysWorkflow assignWorkflowJudge;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_WORKFLOW_HAND_ID")
    private SysWorkflow assignWorkflowHand;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_USER_JUDGE_ID")
    private SysUser assignUserJudge;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_USER_HAND_ID")
    private SysUser assignUserHand;

    @Column(name = "ASSIGN_USER_JUDGE_NAME", length = 50)
    private String assignJudgeUserName;

    @Column(name = "ASSIGN_USER_HAND_NAME", length = 50)
    private String assignHandUserName;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_JUDGE_DEVICE_ID")
    private SysDevice assignJudgeDevice;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_HAND_DEVICE_ID")
    private SysDevice assignHandDevice;

    @Column(name = "ASSIGN_JUDGE_START_TIME", nullable = false)
    private Date assignJudgeStartTime;

    @Column(name = "ASSIGN_JUDGE_END_TIME", nullable = false)
    private Date assignJudgeEndTime;

    @Column(name = "ASSIGN_HAND_START_TIME", nullable = false)
    private Date assignHandStartTime;

    @Column(name = "ASSIGN_HAND_END_TIME", nullable = false)
    private Date assignHandEndTime;

    @Column(name = "ASSIGN_JUDGE_TIMEOUT", length = 10)
    private String assignJudgeTimeout;

    @Column(name = "ASSIGN_HAND_TIMEOUT", length = 10)
    private String assignHandTimeout;

    @Column(name = "ASSIGN_JUDGE_STATUS", length = 10)
    private String assignJudgeStatus;

    @Column(name = "ASSIGN_HAND_STATUS", length = 10)
    private String assignHandStatus;

    @ManyToOne
    @JoinColumn(name = "JUDGE_ID")
    private SerJudgeGraph judgeGraph;

    @ManyToOne
    @JoinColumn(name = "JUDGE_WORKFLOW_ID")
    private SysWorkflow judgeWorkflow;

    @ManyToOne
    @JoinColumn(name = "JUDGE_DEVICE_ID")
    private SysDevice judgeDevice;

    @Column(name = "JUDGE_RESULT", length = 10)
    private String judgeResult;

    @Column(name = "JUDGE_TIMEOUT", length = 10)
    private String judgeTimeout;

    @ManyToOne
    @JoinColumn(name = "HAND_EXAMINATION_ID")
    private SerHandExamination handExamination;

    @ManyToOne
    @JoinColumn(name = "HAND_WORKFLOW_ID")
    private SysWorkflow handWorkflow;

    @ManyToOne
    @JoinColumn(name = "HAND_DEVICE_ID")
    private SysDevice handDevice;

    @Column(name = "HAND_RESULT", length = 10)
    private String handResult;

    @Column(name = "HAND_START_TIME", nullable = false)
    private Date handStartTime;

    @Column(name = "HAND_END_TIME", nullable = false)
    private Date handEndTime;

    @ManyToOne
    @JoinColumn(name = "HAND_USER_ID")
    private SysUser handUser;

    @Column(name = "HAND_TASK_RESULT", length = 10)
    private String handTaskResult;

    @Column(name = "HAND_GOODS", length = 255)
    private String handGoods;

    @Column(name = "HAND_GOODS_GRADE", length = 10)
    private String handGoodsGrade;

    @Column(name = "HAND_COLLECT_SIGN", length = 10)
    private String handCollectSign;

    @Column(name = "HAND_ATTACHED", length = 50)
    private String handAttached;

    @Column(name = "HAND_COLLECT_LABEL", length = 10)
    private String handCollectLabel;

    @Column(name = "HAND_APPRAISE", length = 10)
    private String handAppraise;

    @Column(name = "HAND_APPRAISE2", length = 10)
    private String handAppraiseSecond;

    @Column(name = "JUDGE_START_TIME", nullable = false)
    private Date judgeStartTime;

    @Column(name = "JUDGE_END_TIME", nullable = false)
    private Date judgeEndTime;

    @ManyToOne
    @JoinColumn(name = "JUDGE_USER_ID")
    private SysUser judgeUser;

    @Column(name = "JUDGE_ASSIGN_TIMEOUT", length = 10)
    private String judgeAssignTimeout;

    @Column(name = "JUDGE_STATUS", length = 10)
    private String judgeStatus;

    @Column(name = "TASK_STATUS", length = 10)
    private String taskStatus;

}
