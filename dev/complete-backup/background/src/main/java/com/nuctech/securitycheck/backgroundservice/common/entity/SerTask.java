package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerTask
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
@Table(name = "ser_task")
public class SerTask extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    private SysDevice device;

    @Column(name = "TASK_NUMBER", length = 50)
    private String taskNumber;

    @ManyToOne
    @JoinColumn(name = "MODE_ID")
    private SysWorkMode sysWorkMode;

    @Column(name = "MODE_CONFIG", length = 2000)
    private String modeConfig;

    @Column(name = "MODE_NAME", length = 20)
    private String sysWorkModeName;

    @Column(name = "TASK_STATUS", length = 10)
    private String taskStatus;

    @ManyToOne
    @JoinColumn(name = "WORKFLOW_ID")
    private SysWorkflow sysWorkflow;

    @ManyToOne
    @JoinColumn(name = "SCENE")
    private SysField sysField;

}