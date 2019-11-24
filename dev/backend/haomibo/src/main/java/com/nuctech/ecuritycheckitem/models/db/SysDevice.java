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

import javax.persistence.*;
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
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_ID", length = 20, nullable = false)
    Long deviceId;


    @Column(name = "GUID", length = 255)
    String guid;

    @Column(name = "DEVICE_NAME", length = 50)
    String deviceName;


    @Column(name = "DEVICE_TYPE", length = 50)
    String deviceType;

    @Column(name = "DEVICE_SERIAL", length = 50)
    String deviceSerial;

//    @Column(name = "CATEGORY_ID", length = 20)
//    Long categoryId;
//
//    @Column(name = "MANUFACTURER", length = 10)
//    String manufacturer;
//
//    @Column(name = "ORIGINAL_MODEL", length = 50)
//    String originalModel;

    @Column(name = "ORIGINAL_FACTORY_NUMBER", length = 50)
    String originalFactoryNumber;

    @Column(name = "MANUFACTURE_DATE", nullable = false)
    Date manufacturerDate;

    @Column(name = "PURCHASE_DATE", nullable = false)
    Date purchaseDate;

    @Column(name = "SUPPLIER", length = 50)
    String supplier;

    @Column(name = "CONTACTS", length = 50)
    String contacts;

    @Column(name = "MOBILE", length = 50)
    String mobile;

    @Column(name = "REGISTRATION_NUMBER", length = 50)
    String registrationNumber;

    @Column(name = "IMAGE_URL", length = 200)
    String imageUrl;

    @Column(name = "FIELD_ID", length = 20)
    Long fieldId;

    @Column(name = "ARCHIVE_ID", length = 20)
    Long archiveId;



    @Column(name = "STATUS", length = 10)
    String status;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
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
