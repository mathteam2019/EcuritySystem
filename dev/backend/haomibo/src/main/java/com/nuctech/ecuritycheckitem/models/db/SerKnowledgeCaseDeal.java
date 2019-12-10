/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName SerKnowledgeCaseDeal.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.db;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
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
@JsonFilter(ModelJsonFilters.FILTER_SER_KNOWLEDGE_CASE_DEAL)
@Table(name = "ser_knowledge_case_deal")
public class SerKnowledgeCaseDeal extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CASE_DETAL_ID", length = 20)
    private Long caseDealId;

    @Column(name = "CASE_ID", length = 20)
    private Long caseId;


    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "MODE", length = 20)
    private Long mode;

    @Column(name = "SCAN_ID", length = 20)
    private Long scanId;

    @Column(name = "SCAN_WORKFLOW_ID", length = 20)
    private Long scanWorkflowId;

    @Column(name = "SCAN_DEVICE_ID", length = 20)
    private Long scanDeviceId;

    @Column(name = "SCAN_IMAGE_ID", length = 20)
    private Long scanImageId;

    @Column(name = "SCAN_ATR_RESULT", length = 10)
    private String scanAtrResult;

    @Column(name = "SCAN_FOOT_ALARM", length = 10)
    private String scanFootAlarm;

    @Column(name = "SCAN_START_TIME", nullable = false)
    private Date scanStartTime;

    @Column(name = "SCAN_END_TIME", length = 30)
    private Date scanEndTime;

    @Column(name = "SCAN_POINTSMAN_ID", length = 20)
    private Long scanPointsmanId;

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

    @Column(name = "ASSIGN_JUDGE_DEVICE_ID", length = 10)
    private String assignJudgeDeviceId;

    @Column(name = "ASSIGN_HAND_DEVICE_ID", length = 10)
    private String assignHandDeviceId;

    @Column(name = "ASSIGN_START_TIME", nullable = false)
    private Date assignStartTime;

    @Column(name = "ASSIGN_END_TIME", nullable = false)
    private Date assignEndTime;

    @Column(name = "ASSIGN_TIMEOUT", length = 10)
    private String assignTimeout;

    @Column(name = "ASSIGN_STATUS", length = 10)
    private String assignStatus;


    @Column(name = "JUDGE_ID", length = 20)
    private Long judgeId;

    @Column(name = "JUDGE_WORKFLOW_ID", length = 20)
    private Long judgeWorkflowId;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    private Long judgeDeviceId;

    @Column(name = "JUDGE_RESULT", length = 10)
    private String judgeResult;

    @Column(name = "JUDGE_TIMEOUT", length = 10)
    private String judgeTimeout;

    @Column(name = "HAND_EXAMINATION_ID", length = 20)
    private Long handExaminationId;

    @Column(name = "HAND_WORKFLOW_ID", length = 20)
    private Long handWorkflowId;

    @Column(name = "HAND_DEVICE_ID", length = 20)
    private Long handDeviceId;

    @Column(name = "HAND_RESULT", length = 10)
    private String handResult;

    @Column(name = "HAND_START_TIME", nullable = false)
    private Date handStartTime;

    @Column(name = "HAND_END_TIME", nullable = false)
    private Date handEndTime;

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

    @Column(name = "HAND_ATTACHED_ID", length = 20)
    private Long handAttachedId;

    @Column(name = "HAND_COLLECT_LABEL", length = 10)
    private String handCollectLabel;

    @Column(name = "HAND_APPRAISE", length = 10)
    private String handAppraise;

    @Column(name = "JUDGE_START_TIME", nullable = false)
    private Date judgeStartTime;

    @Column(name = "JUDGE_END_TIME", nullable = false)
    private Date judgeEndTime;

    @Column(name = "JUDGE_USER_ID", length = 20)
    private Long judgeUserId;

    @Column(name = "JUDGE_ASSIGN_TIMEOUT", length = 10)
    private String judgeAssignTimeout;

    @Column(name = "JUDGE_STATUS", length = 10)
    private String judgeStatus;

    @OneToOne()
    @JoinColumn(name = "CASE_ID", referencedColumnName = "CASE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerKnowledgeCase knowledgeCase;

    @OneToOne()
    @JoinColumn(name = "MODE", referencedColumnName = "MODE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysWorkMode workMode;

    @OneToOne()
    @JoinColumn(name = "SCAN_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDevice scanDevice;

    @OneToOne()
    @JoinColumn(name = "HAND_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDevice handDevice;

    @OneToOne()
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDevice judgeDevice;



    @OneToOne()
    @JoinColumn(name = "SCAN_IMAGE_ID", referencedColumnName = "IMAGE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerImage scanImage;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerTask task;



}
