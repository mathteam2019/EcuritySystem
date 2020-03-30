/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerTaskSimplifiedForHistoryTaskManagement）
 * 文件名：	SerTaskSimplifiedForHistoryTaskManagement.java
 * 描述：	Simplified SerTask Model for history task management
 * 作者名：	Tiny
 * 日期：	2019/12/27
 */


package com.nuctech.ecuritycheckitem.models.simplifieddb;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.GenerationType;
import javax.persistence.FetchType;
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
public class SerTaskSimplifiedForHistoryTaskManagement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID", length = 20)
    private Long taskId;


    @Column(name = "TASK_NUMBER", length = 50)
    private String taskNumber;


//    @Column(name = "WORKFLOW_ID", length = 20)
//    private Long workflowId;

    @OneToOne()
    @JoinColumn(name = "SCENE", referencedColumnName = "FIELD_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysFieldSimplifiedForProcessTaskManagement field;

//    @OneToOne()
//    @JoinColumn(name = "WORKFLOW_ID", referencedColumnName = "WORKFLOW_ID", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    private SysWorkflowSimplifiedForProcessTaskManagement workFlow;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    private SerCheckResultSimplifiedForProcessTaskManagement serCheckResult;

}