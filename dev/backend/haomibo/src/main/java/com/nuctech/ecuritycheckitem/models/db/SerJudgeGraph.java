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

    public static class Result {
        public static final String TRUE = "TRUE";
        public static final String FALSE = "FALSE";
    }

    public static class AssignTimeout {
        public static final String TRUE = "TRUE";
        public static final String FALSE = "FALSE";
    }

    public static class JudgeTimeout {
        public static final String TRUE = "TRUE";
        public static final String FALSE = "FALSE";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JUDGE_ID", length = 20)
    private Long judgeId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "WORKFLOW_ID", length = 20)
    private Long workflowId;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    private Long judgeDeviceId;

    @Column(name = "JUDGE_RESULT", length = 10)
    private String judgeResult;

    @Column(name = "JUDGE_TIMEOUT", length = 10)
    private String judgeTimeout;

    @Column(name = "JUDGE_START_TIME", nullable = false)
    private Date judgeStartTime;

    @Column(name = "JUDGE_END_TIME", nullable = false)
    private Date judgeEndTime;

    @Column(name = "JUDGE_USER_ID", length = 20)
    private Long judgeUserId;

    @OneToOne()
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDevice judgeDevice;

    @OneToOne()
    @JoinColumn(name = "JUDGE_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUser judgeUser;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerTask task;

}
