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
import java.util.Date;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_HAND_EXAMINATION)
@Table(name = "ser_hand_examination")
public class SerHandExamination extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HAND_EXAMINATION_ID", length = 20)
    Long handExaminationId;

    @Column(name = "TASK_ID", length = 20)
    Long taskId;

    @Column(name = "WORKFLOW_ID", length = 20)
    Long workflowId;

    @Column(name = "HAND_DEVICE_ID", length = 20)
    Long handDeviceId;

    @Column(name = "HAND_RESULT", length = 10)
    String handResult;

    @Column(name = "HAND_START_TIME", nullable = false)
    Date handStartTime;

    @Column(name = "HAND_END_TIME", nullable = false)
    Date handEndTime;

    @Column(name = "HAND_USER_ID", length = 20)
    Long handUserId;

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
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "WORKFLOW_ID", referencedColumnName = "WORKFLOW_ID", insertable = false, updatable = false)
    SysWorkflow workFlow;

    @OneToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "HAND_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    SysDevice handDevice;

    @OneToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "HAND_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    SysUser handUser;

}
