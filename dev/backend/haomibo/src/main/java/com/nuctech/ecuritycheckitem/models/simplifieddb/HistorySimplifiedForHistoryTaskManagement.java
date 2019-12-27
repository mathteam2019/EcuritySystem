/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（FromConfigId）
 * 文件名：	FromConfigId.java
 * 描述：	FromConfigId Model
 * 作者名：	Tiny
 * 日期：	2019/10/15
 */

package com.nuctech.ecuritycheckitem.models.simplifieddb;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
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
@Table(name = "history")
public class HistorySimplifiedForHistoryTaskManagement extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID", length = 20)
    private Long historyId;

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

    @Column(name = "SCAN_END_TIME", nullable = false)
    private Date scanEndTime;

    @Column(name = "SCAN_POINTSMAN_ID", length = 20)
    private Long scanPointsmanId;

    @Column(name = "SCAN_POINTSMAN_NAME", length = 50)
    private String scanPointsmanName;

    @Column(name = "ASSIGN_JUDGE_ID", length = 20)
    private Long assignJudgeId;

    @Column(name = "ASSIGN_HAND_ID", length = 20)
    private Long assignHandId;

    @Column(name = "ASSIGN_WORKFLOW_JUDGE_ID", length = 20)
    private Long assignWorkflowJudgeId;

    @Column(name = "ASSIGN_WORKFLOW_HAND_ID", length = 20)
    private Long assignWorkflowHandId;

    @Column(name = "ASSIGN_USER_JUDGE_ID", length = 20)
    private Long assignUserJudgeId;

    @Column(name = "ASSIGN_USER_JUDGE_NAME", length = 50)
    private String assignUserJudgeName;

    @Column(name = "ASSIGN_USER_HAND_ID", length = 20)
    private Long assignUserHandId;

    @Column(name = "ASSIGN_USER_HAND_NAME", length = 50)
    private String assignUserHandName;

    @Column(name = "ASSIGN_JUDGE_DEVICE_ID", length = 20)
    private Long assignJudgeDeviceId;

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

    @Column(name = "HAND_ATTACHED", length = 50)
    private String handAttached;

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
    @JoinColumn(name = "MODE", referencedColumnName = "MODE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysWorkMode workMode;

    @OneToOne()
    @JoinColumn(name = "SCAN_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDeviceSimplifiedOnlyHasName scanDevice;

    @OneToOne()
    @JoinColumn(name = "SCAN_POINTSMAN_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUserSimplifiedOnlyHasName scanPointsman;

    @OneToOne()
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDeviceSimplifiedOnlyHasName judgeDevice;

    @OneToOne()
    @JoinColumn(name = "JUDGE_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUserSimplifiedOnlyHasName judgeUser;

    @OneToOne()
    @JoinColumn(name = "HAND_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDeviceSimplifiedOnlyHasName handDevice;

    @OneToOne()
    @JoinColumn(name = "HAND_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUserSimplifiedOnlyHasName handUser;

    @OneToOne()
    @JoinColumn(name = "SCAN_IMAGE_ID", referencedColumnName = "IMAGE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerImageSimplifiedForTaskManagement scanImage;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerTaskSimplifiedForHistoryTaskManagement task;

    @OneToOne()
    @JoinColumn(name = "SCAN_ID", referencedColumnName = "SCAN_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerScanSimplifiedForTaskManagement serScan;

    @OneToOne()
    @JoinColumn(name = "JUDGE_ID", referencedColumnName = "JUDGE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerJudgeGraphSimplifiedForHistoryTaskManagement serJudgeGraph;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerKnowledgeCaseSimplifiedForHistoryTaskManagement serKnowledgeCase;
}
