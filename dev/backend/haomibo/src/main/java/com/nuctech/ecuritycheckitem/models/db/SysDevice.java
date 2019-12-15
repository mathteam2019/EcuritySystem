/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName SysDevice.java
 * @ModifyHistory
 *
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
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
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

//    @Column(name = "CATEGORY_ID", length = 20)
//    Long categoryId;
//
//    @Column(name = "MANUFACTURER", length = 10)
//    String manufacturer;
//
//    @Column(name = "ORIGINAL_MODEL", length = 50)
//    String originalModel;

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

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "FIELD_ID", referencedColumnName = "FIELD_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("field")
    private SysField field; // Relation to SysField table.




    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "ARCHIVE_ID", referencedColumnName = "ARCHIVE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("archive")
    private SerArchive archive; // Relation to SerArchives table.

    @ToString.Exclude
    @OneToOne()
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("config")
    private SysDeviceConfig deviceConfig; // Relation to SysDeviceConfig table.

    @ToString.Exclude
    @OneToOne()
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEV_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("scan")
    private SerScanParam scanParam; // Relation to SerScanParam table.

}
