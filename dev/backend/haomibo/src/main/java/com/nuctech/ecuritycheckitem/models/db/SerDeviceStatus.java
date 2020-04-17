/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerDeviceStatus）
 * 文件名：	SerDeviceStatus.java
 * 描述：	SerDeviceStatus Model
 * 作者名：	Choe
 * 日期：	2019/11/21
 */


package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
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
        public static final String ACTIVE = "1000000701";
        public static final String INACTIVE = "1000000702";
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
    private Long statusId;


    @Column(name = "DEVICE_ID", length = 20, unique = true)
    private Long deviceId;

    @Column(name = "DEVICE_ONLINE", length = 20)
    private Integer deviceOnline;

    @Column(name = "DEVICE_LOGIN_TIME")
    private Date deviceLoginTime;



    @Column(name = "FIELD_ID", length = 20)
    private Long fieldId;


    @Column(name = "CURRENT_WORKFLOW", length = 50)
    private String currentWorkFlow;

    @Column(name = "CURRENT_STATUS", length = 50)
    private String currentStatus;

    @Column(name = "CATEGORY_ID", length = 20)
    private String categoryId;

    @Column(name = "MANUFACTURER", length = 50)
    private String manufacturer;

    @Column(name = "ORIGINAL_MODEL", length = 50)
    private String originalModel;


    @Column(name = "CHECK_COUNT", length = 11)
    private String checkCount;

    @Column(name = "PASS_COUNT", length = 11)
    private String passCount;

    @Column(name = "WARNING_COUNT", length = 11)
    private String warningCount;

    @Column(name = "MANUAL_COUNT", length = 11)
    private String manualCount;

    @Column(name = "ACCOUNT", length = 50)
    private String account;

    @Column(name = "LOGIN_TIME", nullable = false)
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
    private String servo;

    @Column(name = "SLIDE_POSITION", length = 50)
    private String slidePosition;

    @Column(name = "EMERGENCY_STOP", length = 50)
    private String emergencyStop;

    @Column(name = "FOOT_WARNING", length = 50)
    private String footWarning;

    @Column(name = "DISK_SPACE", length = 50)
    private String diskSpace;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    private SysDevice device; // Relation to SysDevice table.

    @javax.persistence.Transient
    private Integer deviceTrafficHigh;

    @javax.persistence.Transient
    private Integer deviceTrafficMiddle;

    @javax.persistence.Transient
    private Integer deviceStorageAlarm;



    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEV_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("scan_param")
    SerScanParamSimple serScanParam;

//    @OneToMany(fetch = FetchType.EAGER)
//    @Fetch(FetchMode.JOIN)
//    @JoinTable(
//            name = "ser_scan",
//            joinColumns = {@JoinColumn(name = "SCAN_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)}
//    )
////    @JoinFormula("(SELECT scan.SCAN_ID FROM ser_scan scan WHERE scan.SCAN_DEVICE_ID = DEVICE_ID ORDER BY login.CREATEDTIME DESC LIMIT 1)")
//    @NotFound(action = NotFoundAction.IGNORE)
////    @Size(max = 200)
//    private List<SerScanSimple> scanSimples;

    @javax.persistence.Transient
    private List<SerScanSimple> scanList; // Relation to SysDevice table.

    @javax.persistence.Transient
    private MonitorRecord record;

    @javax.persistence.Transient
    private String guid;

}
