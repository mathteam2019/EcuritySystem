package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * SerJudgeGraph
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
@Table(name = "ser_judge_graph")
public class SerJudgeGraph extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JUDGE_ID", length = 20)
    private Long judgeId;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private SerTask serTask;

    @ManyToOne
    @JoinColumn(name = "WORKFLOW_ID")
    private SysWorkflow sysWorkflow;

    @ManyToOne
    @JoinColumn(name = "JUDGE_DEVICE_ID")
    private SysDevice judgeDevice;

    @Column(name = "JUDGE_RESULT", length = 10)
    private String judgeResult;

    @Column(name = "JUDGE_TIMEOUT", length = 10)
    private String judgeTimeout;

    @Column(name = "JUDGE_SUBMITRECTS", length = 255)
    private String judgeSubmitRects;

    @Column(name = "JUDGE_TIME", length = 255)
    private String judgeTime;

    @Column(name = "JUDGE_START_TIME", nullable = false)
    private Date judgeStartTime;

    @Column(name = "JUDGE_END_TIME", nullable = false)
    private Date judgeEndTime;

    @ManyToOne
    @JoinColumn(name = "JUDGE_USER_ID")
    private SysUser judgeUser;

}
