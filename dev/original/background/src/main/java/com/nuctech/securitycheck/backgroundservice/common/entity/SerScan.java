package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * SerScan
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
@Table(name = "ser_scan")
public class SerScan extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCAN_ID", length = 20)
    private Long scanId;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private SerTask serTask;

    @ManyToOne
    @JoinColumn(name = "WORKFLOW_ID")
    private SysWorkflow sysWorkflow;

    @ManyToOne
    @JoinColumn(name = "SCAN_DEVICE_ID")
    private SysDevice sysDevice;

    @Column(name = "SCAN_IMAGE_ID")
    private Long scanImageId;

    @Column(name = "SCAN_ATR_RESULT", length = 10)
    private String scanATRResult;

    @Column(name = "SCAN_INVALID", length = 10)
    private String scanInvalid;

    @Column(name = "SCAN_FOOT_ALARM", length = 10)
    private String scanFootAlarm;

    @Column(name = "SCAN_START_TIME")
    private Date scanStartTime;

    @Column(name = "SCAN_END_TIME")
    private Date scanEndTime;

    @ManyToOne
    @JoinColumn(name = "SCAN_POINTSMAN_ID")
    private SysUser scanPointsman;

    @Column(name = "SCAN_ASSIGN_TIMEOUT", length = 10)
    private String scanAssignTimeout;

    @Column(name = "SCAN_OFFLINE", length = 10)
    private Integer scanOffline;

    @Column(name = "SCAN_IMAGEGENDER", length = 10)
    private Integer scanImageGender;

    @Column(name = "SCAN_DATA", length = 255)
    private String scanData;

    @Column(name = "SCAN_DEVICEIMAGES", length = 1000)
    private String scanDeviceImages;

    @Column(name = "SCAN_RANDOMALARM", length = 10)
    private String scanRandomAlarm;

}
