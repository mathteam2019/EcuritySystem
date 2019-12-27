/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysWorkflow）
 * 文件名：	SysWorkflow.java
 * 描述：	SysWorkflow Model
 * 作者名：	Tiny
 * 日期：	2019/11/15
 */


package com.nuctech.ecuritycheckitem.models.db;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
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
import javax.persistence.GenerationType;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW)
@Table(name = "sys_workflow")
public class SysWorkflow extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORKFLOW_ID", length = 20)
    private Long workflowId;

    @Column(name = "MODE_ID", length = 20)
    private Long modeId;

    @Column(name = "STEP_SEQUENCE", length = 11)
    private Integer stepSequence;

    @Column(name = "STEP_NAME", length = 50)
    private String stepName;

    @Column(name = "TASK_TYPE", length = 10)
    private String taskType;

    @OneToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MODE_ID", referencedColumnName = "MODE_ID", insertable = false, updatable = false)
    private SysWorkMode workMode;

}
