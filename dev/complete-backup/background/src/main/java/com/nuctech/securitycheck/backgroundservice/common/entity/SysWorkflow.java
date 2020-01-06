package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysWorkflow
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
@Table(name = "sys_workflow")
public class SysWorkflow extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORKFLOW_ID", length = 20, nullable = false)
    private Long workflowId;

    @ManyToOne
    @JoinColumn(name = "MODE_Id")
    private SysWorkMode sysWorkMode;

    @Column(name = "STEP_SEQUENCE", length = 11)
    private Integer stepSequence;

    @Column(name = "STEP_NAME", length = 50)
    private String stepName;

    @Column(name = "TASK_TYPE", length = 10)
    private String taskType;

}
