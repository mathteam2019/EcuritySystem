/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysDevice）
 * 文件名：	SysDevice.java
 * 描述：	SysDevice Model
 * 作者名：	Choe
 * 日期：	2019/11/19
 */


package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_DEVICE)
@Table(name = "sys_device")
public class SysDevice extends BaseEntity implements Serializable {

    public static class Status {
        public static final String ACTIVE = "1000000701";
        public static final String INACTIVE = "1000000702";
    }

    public static class DeviceStatus {
        public static final String REGISTER = "1000002001";
        public static final String UNREGISTER = "1000002002";
        public static final String LOGIN = "1000002003";
        public static final String LOGOUT = "1000002004";
        public static final String START = "1000002005";
        public static final String STOP = "1000002006";
    }

    public static class DeviceWorkStatus {
        public static final String BUSY = "1000002101";
        public static final String FREE = "1000002102";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_ID", length = 20, nullable = false)
    private Long deviceId;


    @Column(name = "GUID", length = 255)
    private String guid;

    @Column(name = "DEVICE_NAME", length = 50)
    private String deviceName;


    @Column(name = "DEVICE_TYPE", length = 50)
    private String deviceType;

    @Column(name = "DEVICE_SERIAL", length = 50)
    private String deviceSerial;

    @Column(name = "ORIGINAL_FACTORY_NUMBER", length = 50)
    private String originalFactoryNumber;

    @Column(name = "MANUFACTURE_DATE", nullable = false)
    private Date manufacturerDate;

    @Column(name = "PURCHASE_DATE", nullable = false)
    private Date purchaseDate;

    @Column(name = "SUPPLIER", length = 50)
    private String supplier;

    @Column(name = "CONTACTS", length = 50)
    private String contacts;

    @Column(name = "MOBILE", length = 50)
    private String mobile;

    @Column(name = "REGISTRATION_NUMBER", length = 50)
    private String registrationNumber;

    @Column(name = "IMAGE_URL", length = 200)
    private String imageUrl;

    @Column(name = "FIELD_ID", length = 20)
    private Long fieldId;

    @Column(name = "ARCHIVE_ID", length = 20)
    private Long archiveId;

    @Column(name = "CATEGORY_ID", length = 20)
    private Long categoryId;

    @Column(name = "REGISTER_ID", length = 20)
    private Long registerId;

    @Column(name = "DEVICE_DESC", length = 500)
    private String deviceDesc;

    @Column(name = "DEVICE_IP", length = 20)
    private String deviceIp;

    @Column(name = "DEVICE_PASSAGEWAY", length = 50)
    private String devicePassageWay;



    @Column(name = "STATUS", length = 10)
    private String status;

    @Column(name = "CURRENT_STATUS", length = 30)
    private String currentStatus;

    @Column(name = "WORK_STATUS", length = 30)
    private String workStatus;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FIELD_ID", referencedColumnName = "FIELD_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("field")
    private SysField field; // Relation to SysField table.

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARCHIVE_ID", referencedColumnName = "ARCHIVE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("archive")
    private SerArchive archive; // Relation to SerArchives table.

//    @javax.persistence.Transient
//    private SysDeviceConfig deviceConfig; // Relation to SysDeviceConfig table.
//
//    @javax.persistence.Transient
//    private SerScanParam scanParam; // Relation to SerScanParam table.

}
