/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysDeviceSimple）
 * 文件名：	SysDeviceSimple.java
 * 描述：	SysDeviceSimple Model
 * 作者名：	Choe
 * 日期：	2019/11/20
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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.JoinColumn;
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
public class SysDeviceSimple extends BaseEntity implements Serializable {



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
    private SysField field;
}
