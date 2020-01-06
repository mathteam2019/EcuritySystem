package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * SerAssign
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
@Table(name = "ser_assign")
public class SerAssign extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ASSIGN_ID", length = 20)
    private Long assignId;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private SerTask serTask;

    @ManyToOne
    @JoinColumn(name = "WORKFLOW_ID")
    private SysWorkflow sysWorkflow;

    @ManyToOne
    @JoinColumn(name = "MODE")
    private SysWorkMode sysWorkMode;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_USER_ID")
    private SysUser assignUser;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_JUDGE_DEVICE_ID")
    private SysDevice assignJudgeDevice;

    @ManyToOne
    @JoinColumn(name = "ASSIGN_HAND_DEVICE_ID")
    private SysDevice assignHandDevice;

    @Column(name = "ASSIGN_START_TIME")
    private Date assignStartTime;

    @Column(name = "ASSIGN_END_TIME")
    private Date assignEndTime;

    @Column(name = "ASSIGN_TIMEOUT")
    private String assignTimeout;

}
