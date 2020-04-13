/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerKnowledgeCaseDeal）
 * 文件名：	SerKnowledgeCaseDeal.java
 * 描述：	SerKnowledgeCaseDeal Model
 * 作者名：	Choe
 * 日期：	2019/11/26
 */

package com.nuctech.securitycheck.backgroundservice.common.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "ser_knowledge_case_deal")
public class SerKnowledgeCaseDeal extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CASE_DETAL_ID", length = 20)
    private Long caseDealId;

    @Column(name = "CASE_ID", length = 20)
    private Long caseId;


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

    @Column(name = "ASSIGNSCAN_ID", length = 20)
    private Long assignscanId;

    @Column(name = "ASSIGN_WORKFLOW_ID", length = 20)
    private Long assignWorkflowId;

    @Column(name = "ASSIGN_USER_ID", length = 20)
    private Long assignUserId;

    @Column(name = "ASSIGN_USER_NAME", length = 50)
    private String assignUserName;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_JUDGE_DEVICE_ID")
    private SysDevice assignJudgeDevice;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_HAND_DEVICE_ID")
    private SysDevice assignHandDevice;

    @Column(name = "ASSIGN_START_TIME", nullable = false)
    private Date assignStartTime;

    @Column(name = "ASSIGN_END_TIME", nullable = false)
    private Date assignEndTime;

    @Column(name = "ASSIGN_TIMEOUT", length = 10)
    private String assignTimeout;

    @Column(name = "ASSIGN_STATUS", length = 10)
    private String assignStatus;


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

    @Column(name = "HAND_ATTACHED_ID", length = 20)
    private Long handAttachedId;

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

}
