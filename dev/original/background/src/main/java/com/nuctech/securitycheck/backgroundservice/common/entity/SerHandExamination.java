package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * SerHandExamination
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
@Table(name = "ser_hand_examination")
public class SerHandExamination extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HAND_EXAMINATION_ID", length = 20)
    private Long handExaminationId;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private SerTask serTask;

    @ManyToOne
    @JoinColumn(name = "WORKFLOW_ID")
    private SysWorkflow sysWorkflow;

    @ManyToOne
    @JoinColumn(name = "HAND_DEVICE_ID")
    private SysDevice handDevice;

    @Column(name = "HAND_RESULT", length = 10)
    private String handResult;

    @Column(name = "HAND_START_TIME", nullable = false)
    private Date handStartTime;

    @Column(name = "HAND_END_TIME", nullable = false)
    private Date handEndTime;

    @ManyToOne
    @JoinColumn(name = "HAND_USER_ID")
    private SysUser handUser;

}
