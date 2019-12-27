/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerTask）
 * 文件名：	SerTask.java
 * 描述：	SerTask Model
 * 作者名：	Tiny
 * 日期：	2019/11/21
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

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_TASK)
@Table(name = "ser_task")
public class SerTaskSimplifiedForProcessTaskManagement extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "DEVICE_ID", length = 20)
    private Long deviceId;

    @Column(name = "TASK_NUMBER", length = 50)
    private String taskNumber;

    @Column(name = "SCENE", length = 20)
    private Long fieldId;

    @Column(name = "TASK_STATUS", length = 10)
    private String taskStatus;

    @Column(name = "WORKFLOW_ID", length = 20)
    private Long workflowId;

    @OneToOne()
    @JoinColumn(name = "SCENE", referencedColumnName = "FIELD_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysFieldSimplifiedForProcessTaskManagement field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerScanSimplifiedForTaskManagement serScan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerJudgeGraphSimplifiedForProcessTaskManagement serJudgeGraph;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerHandExaminationSimplifiedForProcessTaskManagement serHandExamination;

    @OneToOne()
    @JoinColumn(name = "WORKFLOW_ID", referencedColumnName = "WORKFLOW_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysWorkflowSimplifiedForProcessTaskManagement workFlow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private HistorySimplifiedForProcessTaskManagement history;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerCheckResultSimplifiedForProcessTaskManagement serCheckResult;

}