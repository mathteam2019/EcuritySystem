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


package com.nuctech.ecuritycheckitem.models.simplifieddb;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import com.nuctech.ecuritycheckitem.models.db.SysWorkMode;
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
@JsonFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW)
@Table(name = "sys_workflow")
public class SysWorkflowSimplifiedForProcessTaskManagement extends BaseEntitySimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORKFLOW_ID", length = 20)
    private Long workflowId;

    @Column(name = "MODE_ID", length = 20)
    private Long modeId;

    @OneToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MODE_ID", referencedColumnName = "MODE_ID", insertable = false, updatable = false)
    private SysWorkMode workMode;

}
