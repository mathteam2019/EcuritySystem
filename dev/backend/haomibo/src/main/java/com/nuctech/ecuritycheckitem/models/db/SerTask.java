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

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_TASK)
@Table(name = "ser_task")
public class SerTask extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "DEVICE_ID", length = 20)
    private Long deviceId;

    @Column(name = "TASK_NUMBER", length = 50)
    private String taskNumber;

    @Column(name = "SCENE", length = 20)
    private Long fieldId;

    @Column(name = "TASK_STATUS", length = 10)
    private String taskStatus;

    @Column(name = "WORKFLOW_ID", length = 20)
    private Long workflowId;

    @OneToOne()
    @JoinColumn(name = "SCENE", referencedColumnName = "FIELD_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysField field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerScan serScan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerJudgeGraph serJudgeGraph;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerHandExamination serHandExamination;


    @OneToOne()
    @JoinColumn(name = "WORKFLOW_ID", referencedColumnName = "WORKFLOW_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysWorkflow workFlow;


}