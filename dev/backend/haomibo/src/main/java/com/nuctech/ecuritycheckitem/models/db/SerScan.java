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
@JsonFilter(ModelJsonFilters.FILTER_SER_SCAN)
@Table(name = "ser_scan")
public class SerScan extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCAN_ID", length = 20)
    Long scanId;

    @Column(name = "TASK_ID", length = 20)
    Long taskId;

    @Column(name = "WORKFLOW_ID", length = 20)
    Long workflowId;

    @Column(name = "SCAN_DEVICE_ID", length = 20)
    Long scanDeviceId;

    @Column(name = "SCAN_IMAGE_ID", length = 20)
    Long scanImageId;

    @Column(name = "SCAN_ATR_RESULT", length = 10)
    String scanAtrResult;

    @Column(name = "SCAN_INVALID", length = 10)
    String scanInvalid;

    @Column(name = "SCAN_FOOT_ALARM", length = 10)
    String scanFootAlarm;

    @Column(name = "SCAN_START_TIME", nullable = false)
    Date scanStartTime;

    @Column(name = "SCAN_END_TIME", nullable = false)
    Date scanEndTime;

    @Column(name = "SCAN_POINTSMAN_ID", length = 20)
    Long scanPointsmanId;

    @Column(name = "SCAN_ASSIGN_TIMEOUT", length = 10)
    String scanAssignTimeout;

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
    @JoinColumn(name = "SCAN_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    SysDevice scanDevice;

    @OneToOne()
    @JoinColumn(name = "SCAN_POINTSMAN_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    SysUser scanPointsman;

    @OneToOne()
    @JoinColumn(name = "SCAN_IMAGE_ID", referencedColumnName = "IMAGE_ID", insertable = false, updatable = false)
    SerImage scanImage;
}