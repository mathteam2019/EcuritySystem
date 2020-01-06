package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerTaskStep
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
@Table(name = "ser_task_step")
public class SerTaskStep extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STEP_ID", length = 20)
    private Long stepId;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private SerTask serTask;

    @ManyToOne
    @JoinColumn(name = "WORKFLOW_ID")
    private SysWorkflow sysWorkflow;

}
