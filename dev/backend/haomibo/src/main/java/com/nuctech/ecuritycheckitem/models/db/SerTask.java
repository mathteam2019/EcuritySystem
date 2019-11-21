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
    @Column(name = "TASK_ID", length = 20)
    Long task_id;

    @Column(name = "DEVICE_ID", length = 20)
    Long device_id;

    @Column(name = "TASK_NUMBER", length = 50)
    String task_number;

    @Column(name = "SCENE", length = 20)
    Long field_id;

    @Column(name = "CREATEDBY", length = 20)
    private Long createdby;

    @Column(name = "CREATEDTIME", nullable = false)
    private Date createdtime;

    @Column(name = "EDITEDBY", length = 20)
    private Long editedby;

    @Column(name = "EDITEDTIME", nullable = false)
    private Date editedtime;

    @Column(name = "NOTE", length = 500)
    String note;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    History history;

    @OneToOne()
    @JoinColumn(name = "SCENE", referencedColumnName = "FIELD_ID", insertable = false, updatable = false)
    SysField field;


}
