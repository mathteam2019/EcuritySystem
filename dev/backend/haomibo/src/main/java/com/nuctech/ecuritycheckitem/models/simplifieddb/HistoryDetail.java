/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（HistorySimplifiedForHistoryTaskManagement）
 * 文件名：	HistorySimplifiedForHistoryTaskManagement.java
 * 描述：	Simplified History Model
 * 作者名：	Tiny
 * 日期：	2019/12/27
 */

package com.nuctech.ecuritycheckitem.models.simplifieddb;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
@JsonFilter(ModelJsonFilters.FILTER_HISTORY)
@Table(name = "history_finish")
public class HistoryDetail extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID", length = 20)
    private Long historyId;


    //    @ManyToOne
//    @JoinColumn(name = "TASK_ID")
//    private SerTask serTask;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

//    @ManyToOne
//    @JoinColumn(name = "MODE")
//    private SysWorkMode sysWorkMode;

    @Column(name = "MODE", length = 20)
    private Long modeId;

//    @ManyToOne
//    @JoinColumn(name = "SCAN_ID")
//    private SerScan serScan;

    @Column(name = "SCAN_ID", length = 20)
    private Long scanId;

//    @ManyToOne
//    @JoinColumn(name = "SCAN_WORKFLOW_ID")
//    private SysWorkflow sysWorkflow;

    @Column(name = "SCAN_WORKFLOW_ID", length = 20)
    private Long workflowId;

//    @ManyToOne
//    @JoinColumn(name = "SCAN_DEVICE_ID")
//    private SysDevice scanDevice;

    @Column(name = "SCAN_DEVICE_ID", length = 20)
    private Long scanDeviceId;

//    @ManyToOne
//    @JoinColumn(name = "SCAN_IMAGE_ID")
//    private SerImage scanImage;

    @Column(name = "SCAN_IMAGE_ID", length = 20)
    private Long scanImageId;

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

//    @ManyToOne
//    @JoinColumn(name = "SCAN_POINTSMAN_ID")
//    private SysUser scanPointsman;

    @Column(name = "SCAN_POINTSMAN_ID", length = 20)
    private Long scanPointmanId;

    @Column(name = "SCAN_POINTSMAN_NAME", length = 50)
    private String scanPointsmanName;

//    @ManyToOne
//    @JoinColumn(name = "ASSIGN_JUDGE_ID")
//    private SerAssign serAssignJudge;
//
//    @ManyToOne
//    @JoinColumn(name = "ASSIGN_HAND_ID")
//    private SerAssign serAssignHand;

    @Column(name = "ASSIGN_JUDGE_ID", length = 20)
    private Long assignJudgeId;

    @Column(name = "ASSIGN_HAND_ID", length = 20)
    private Long assignHandId;

//    @ManyToOne
//    @JoinColumn(name = "ASSIGN_WORKFLOW_JUDGE_ID")
//    private SysWorkflow assignWorkflowJudge;

    @Column(name = "ASSIGN_WORKFLOW_JUDGE_ID", length = 20)
    private Long assignWorkflowJudgeId;

//    @ManyToOne
//    @JoinColumn(name = "ASSIGN_WORKFLOW_HAND_ID")
//    private SysWorkflow assignWorkflowHand;

    @Column(name = "ASSIGN_WORKFLOW_HAND_ID", length = 20)
    private Long assignWorkflowHandId;

//    @ManyToOne
//    @JoinColumn(name = "ASSIGN_USER_JUDGE_ID")
//    private SysUser assignUserJudge;

    @Column(name = "ASSIGN_USER_JUDGE_ID", length = 20)
    private Long assignUserJudgeId;

//    @ManyToOne
//    @JoinColumn(name = "ASSIGN_USER_HAND_ID")
//    private SysUser assignUserHand;

    @Column(name = "ASSIGN_USER_HAND_ID", length = 20)
    private Long assignUserHandId;

    @Column(name = "ASSIGN_USER_JUDGE_NAME", length = 50)
    private String assignJudgeUserName;

    @Column(name = "ASSIGN_USER_HAND_NAME", length = 50)
    private String assignHandUserName;

//    @ManyToOne
//    @JoinColumn(name = "ASSIGN_JUDGE_DEVICE_ID")
//    private SysDevice assignJudgeDevice;

    @Column(name = "ASSIGN_JUDGE_DEVICE_ID", length = 20)
    private Long assignJudgeDeviceId;

//    @ManyToOne
//    @JoinColumn(name = "ASSIGN_HAND_DEVICE_ID")
//    private SysDevice assignHandDevice;

    @Column(name = "ASSIGN_HAND_DEVICE_ID", length = 20)
    private Long assignHandDeviceId;

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

//    @ManyToOne
//    @JoinColumn(name = "JUDGE_ID")
//    private SerJudgeGraph judgeGraph;

    @Column(name = "JUDGE_ID", length = 20)
    private Long judgeGraphId;

//    @ManyToOne
//    @JoinColumn(name = "JUDGE_WORKFLOW_ID")
//    private SysWorkflow judgeWorkflow;

    @Column(name = "JUDGE_WORKFLOW_ID", length = 20)
    private Long judgeWorkflowId;

//    @ManyToOne
//    @JoinColumn(name = "JUDGE_DEVICE_ID")
//    private SysDevice judgeDevice;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    private Long judgeDeviceId;

    @Column(name = "JUDGE_RESULT", length = 10)
    private String judgeResult;

    @Column(name = "JUDGE_TIMEOUT", length = 10)
    private String judgeTimeout;

//    @ManyToOne
//    @JoinColumn(name = "HAND_EXAMINATION_ID")
//    private SerHandExamination handExamination;

    @Column(name = "HAND_EXAMINATION_ID", length = 20)
    private Long handExaminationId;

//    @ManyToOne
//    @JoinColumn(name = "HAND_WORKFLOW_ID")
//    private SysWorkflow handWorkflow;

    @Column(name = "HAND_WORKFLOW_ID", length = 20)
    private Long handWorkflowId;

//    @ManyToOne
//    @JoinColumn(name = "HAND_DEVICE_ID")
//    private SysDevice handDevice;

    @Column(name = "HAND_DEVICE_ID", length = 20)
    private Long handDeviceId;

    @Column(name = "HAND_RESULT", length = 10)
    private String handResult;

    @Column(name = "HAND_START_TIME", nullable = false)
    private Date handStartTime;

    @Column(name = "HAND_END_TIME", nullable = false)
    private Date handEndTime;

//    @ManyToOne
//    @JoinColumn(name = "HAND_USER_ID")
//    private SysUser handUser;

    @Column(name = "HAND_USER_ID", length = 20)
    private Long handUserId;

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

//    @ManyToOne
//    @JoinColumn(name = "JUDGE_USER_ID")
//    private SysUser judgeUser;

    @Column(name = "JUDGE_USER_ID", length = 20)
    private Long judgeUserId;

    @Column(name = "JUDGE_ASSIGN_TIMEOUT", length = 10)
    private String judgeAssignTimeout;

    @Column(name = "JUDGE_STATUS", length = 10)
    private String judgeStatus;

    @Column(name = "TASK_STATUS", length = 10)
    private String taskStatus;

    @Column(name = "SCENE", length = 20)
    private Long fieldId;

    @Column(name = "FIELD_DESIGNATION", length = 50)
    private String fieldDesignation;



    @Column(name = "TASK_NUMBER", length = 50)
    private String taskNumber;

    @Column(name = "MODE_NAME", length = 50)
    private String modeName;

    @Column(name = "SCAN_DEVICE_NAME", length = 50)
    private String scanDeviceName;

    @Column(name = "JUDGE_DEVICE_NAME", length = 50)
    private String judgeDeviceName;

    @Column(name = "HAND_DEVICE_NAME", length = 50)
    private String handDeviceName;

    @Column(name = "ASSIGN_JUDGE_DEVICE_NAME", length = 50)
    private String assignJudgeDeviceName;

    @Column(name = "ASSIGN_HAND_DEVICE_NAME", length = 50)
    private String assignHandDeviceName;

    @Column(name = "JUDGE_USER_NAME", length = 50)
    private String judgeUserName;

    @Column(name = "HAND_USER_NAME", length = 50)
    private String handUserName;


}
