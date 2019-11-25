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
@JsonFilter(ModelJsonFilters.FILTER_SER_TASK)
@Table(name = "ser_task")
public class SerTask extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID", length = 20)
    Long taskId;

    @Column(name = "DEVICE_ID", length = 20)
    Long deviceId;

    @Column(name = "TASK_NUMBER", length = 50)
    String taskNumber;

    @Column(name = "SCENE", length = 20)
    Long fieldId;

    @Column(name = "TASK_STATUS", length = 10)
    String taskStatus;

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
    @JoinColumn(name = "SCENE", referencedColumnName = "FIELD_ID", insertable = false, updatable = false)
    SysField field;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    SerScan serScan;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    SerJudgeGraph serJudgeGraph;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    SerHandExamination serHandExamination;

}