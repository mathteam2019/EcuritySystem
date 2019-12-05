/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName SerDeviceStatus.java
 * @ModifyHistory
 *
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_DEVICE_STATUS)
@Table(name = "ser_device_status")
public class SerDeviceStatus extends BaseEntity implements Serializable {

    public static class Status {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
    }

    @Getter
    @Setter
    public static class MonitorRecord {
        List<String> timeList;
        List<Integer> countList;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATUS_ID", length = 20, nullable = false)
    Long statusId;


    @Column(name = "DEVICE_ID", length = 20)
    Long deviceId;

    @Column(name = "FIELD_ID", length = 20)
    Long fieldId;


    @Column(name = "CURRENT_WORKFLOW", length = 50)
    String currentWorkFlow;

    @Column(name = "CURRENT_STATUS", length = 50)
    String currentStatus;

    @Column(name = "CATEGORY_ID", length = 20)
    String categoryId;

    @Column(name = "MANUFACTURER", length = 50)
    String manufacturer;

    @Column(name = "ORIGINAL_MODEL", length = 50)
    String originalModel;


    @Column(name = "CHECK_COUNT", length = 11)
    String checkCount;

    @Column(name = "PASS_COUNT", length = 11)
    String passCount;

    @Column(name = "WARNING_COUNT", length = 11)
    String warningCount;

    @Column(name = "MANUAL_COUNT", length = 11)
    String manualCount;

    @Column(name = "ACCOUNT", length = 50)
    String account;

    @Column(name = "LOGIN_TIME", nullable = false)
    Date loginTime;

    @Column(name = "IP_ADDRESS", length = 50)
    String ipAddress;

    @Column(name = "PLC_STATUS", length = 50)
    String plcStatus;

    @Column(name = "MASTER_CARD_STATUS", length = 50)
    String masterCardStatus;

    @Column(name = "SLAVE_CARD_STATUS", length = 50)
    String slaveCardStatus;

    @Column(name = "SERVO", length = 50)
    String servo;

    @Column(name = "SLIDE_POSITION", length = 50)
    String slidePosition;

    @Column(name = "EMERGENCY_STOP", length = 50)
    String emergencyStop;

    @Column(name = "FOOT_WARNING", length = 50)
    String footWarning;

    @Column(name = "DISK_SPACE", length = 50)
    String diskSpace;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    private SysDevice device; // Relation to SysDevice table.

    @javax.persistence.Transient
    Integer deviceTrafficHigh;

    @javax.persistence.Transient
    Integer deviceTrafficMiddle;

    @javax.persistence.Transient
    Integer storageAlarm;

    @javax.persistence.Transient
    MonitorRecord record;

}
