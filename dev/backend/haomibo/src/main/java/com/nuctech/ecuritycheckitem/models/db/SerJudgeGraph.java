package com.nuctech.ecuritycheckitem.models.db;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_JUDGE_GRAPH)
@Table(name = "ser_judge_graph")
public class SerJudgeGraph extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JUDGE_ID", length = 20)
    Long judgeId;

    @Column(name = "TASK_ID", length = 20)
    Long taskId;

    @Column(name = "WORKFLOW_ID", length = 20)
    Long workflowId;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    Long judgeDeviceId;

    @Column(name = "JUDGE_RESULT", length = 10)
    String judgeResult;

    @Column(name = "JUDGE_TIMEOUT", length = 10)
    String judgeTimeout;

    @Column(name = "JUDGE_START_TIME", nullable = false)
    Date judgeStartTime;

    @Column(name = "JUDGE_END_TIME", nullable = false)
    Date judgeEndTime;

    @Column(name = "JUDGE_USER_ID", length = 20)
    Long judgeUserId;

    @Column(name = "CREATEDBY", length = 20)
    private Long createdBy;

    @Column(name = "CREATEDTIME", nullable = false)
    private Date createdTime;

    @Column(name = "EDITEDBY", length = 20)
    private Long editedBy;

    @Column(name = "EDITEDTIME", nullable = false)
    private Date editedTime;

    @Column(name = "NOTE", length = 500, nullable = false)
    private String note;

    @OneToOne()
    @JoinColumn(name = "WORKFLOW_ID", referencedColumnName = "WORKFLOW_ID", insertable = false, updatable = false)
    SysWorkflow workFlow;

    @OneToOne()
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    SysDevice judgeDevice;

    @OneToOne()
    @JoinColumn(name = "JUDGE_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    SysUser judgeUser;

}