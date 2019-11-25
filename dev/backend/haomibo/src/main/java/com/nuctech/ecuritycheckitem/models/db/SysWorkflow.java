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
@JsonFilter(ModelJsonFilters.FILTER_SYS_WORKFLOW)
@Table(name = "sys_workflow")
public class SysWorkflow extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORKFLOW_ID", length = 20)
    Long workflowId;

    @Column(name = "MODE_ID", length = 20)
    Long modeId;

    @Column(name = "STEP_SEQUENCE", length = 11)
    Integer stepSequence;

    @Column(name = "STEP_NAME", length = 50)
    String stepName;

    @Column(name = "TASK_TYPE", length = 10)
    String taskType;

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
    @JoinColumn(name = "MODE_ID", referencedColumnName = "MODE_ID", insertable = false, updatable = false)
    SysWorkMode workMode;

}
