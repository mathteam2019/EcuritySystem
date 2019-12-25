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
@JsonFilter(ModelJsonFilters.FILTER_SER_SCAN)
@Table(name = "ser_scan")
public class SerScan extends BaseEntity implements Serializable {

    public static class Invalid {
        public static final String FALSE = "FALSE";
        public static final String TRUE = "TRUE";
    }

    public static class ATRResult {
        public static final String TRUE = "TRUE";
        public static final String FALSE = "FALSE";
    }

    public static class FootAlarm {
        public static final String TRUE = "TRUE";
        public static final String FALSE = "FALSE";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCAN_ID", length = 20)
    private Long scanId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "WORKFLOW_ID", length = 20)
    private Long workflowId;

    @Column(name = "SCAN_DEVICE_ID", length = 20)
    private Long scanDeviceId;

    @Column(name = "SCAN_IMAGE_ID", length = 20)
    private Long scanImageId;

    @Column(name = "SCAN_DEVICEIMAGES", length = 1000)
    private String scanDeviceImages;

    @Column(name = "SCAN_ATR_RESULT", length = 10)
    private String scanAtrResult;

    @Column(name = "SCAN_INVALID", length = 10)
    private String scanInvalid;

    @Column(name = "SCAN_FOOT_ALARM", length = 10)
    private String scanFootAlarm;

    @Column(name = "SCAN_START_TIME", nullable = false)
    private Date scanStartTime;

    @Column(name = "SCAN_END_TIME", nullable = false)
    private Date scanEndTime;

    @Column(name = "SCAN_POINTSMAN_ID", length = 20)
    private Long scanPointsmanId;

    @Column(name = "SCAN_ASSIGN_TIMEOUT", length = 10)
    private String scanAssignTimeout;

    @Column(name = "SCAN_IMAGEGENDER", length = 10)
    private String scanImageGender;


    @OneToOne()
    @JoinColumn(name = "WORKFLOW_ID", referencedColumnName = "WORKFLOW_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysWorkflow workFlow;

    @OneToOne()
    @JoinColumn(name = "SCAN_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysDevice scanDevice;

    @OneToOne()
    @JoinColumn(name = "SCAN_POINTSMAN_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysUser scanPointsman;

    @OneToOne()
    @JoinColumn(name = "SCAN_IMAGE_ID", referencedColumnName = "IMAGE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SerImage scanImage;

    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SerTask task;

}
