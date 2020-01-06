package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * SerDeviceStatus
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
@Table(name = "ser_device_status")
public class SerDeviceStatus extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATUS_ID", length = 20, nullable = false)
    private Long statusId;

    @Column(name = "DEVICE_ID", length = 20)
    private Long deviceId;

    @Column(name = "FIELD_ID", length = 20)
    private Long fieldId;

    @Column(name = "CURRENT_WORKFLOW", length = 50)
    private String currentWorkflow;

    @Column(name = "CURRENT_STATUS", length = 50)
    private String currentStatus;

    @Column(name = "CATEGORY_ID", length = 50)
    private String categoryId;

    @Column(name = "MANUFACTURER", length = 50)
    private String manufacturer;

    @Column(name = "ORIGINAL_MODEL", length = 50)
    private String originalModel;

    @Column(name = "CHECK_COUNT", length = 11)
    private Integer checkCount;

    @Column(name = "PASS_COUNT", length = 11)
    private Integer passCount;

    @Column(name = "WARNING_COUNT", length = 11)
    private Integer warningCount;

    @Column(name = "MANUAL_COUNT", length = 11)
    private Integer manualCount;

    @Column(name = "ACCOUNT", length = 50)
    private String account;

    @Column(name = "LOGIN_TIME")
    private Date loginTime;

    @Column(name = "IP_ADDRESS", length = 50)
    private String ipAddress;

    @Column(name = "PLC_STATUS", length = 50)
    private String plcStatus;

    @Column(name = "MASTER_CARD_STATUS", length = 50)
    private String masterCardStatus;

    @Column(name = "SLAVE_CARD_STATUS", length = 50)
    private String slaveCardStatus;

    @Column(name = "SERVO", length = 50)
    private String serVo;

    @Column(name = "SLIDE_POSITION", length = 50)
    private String slidePosition;

    @Column(name = "EMERGENCY_STOP", length = 50)
    private String emergencyStop;

    @Column(name = "FOOT_WARNING", length = 50)
    private String footWarning;

    @Column(name = "DISK_SPACE", length = 50)
    private String diskSpace;

}
