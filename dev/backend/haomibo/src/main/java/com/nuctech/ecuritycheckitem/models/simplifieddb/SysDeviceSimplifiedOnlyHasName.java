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


package com.nuctech.ecuritycheckitem.models.simplifieddb;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import com.nuctech.ecuritycheckitem.models.db.SerArchive;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import lombok.*;
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
public class SysDeviceSimplifiedOnlyHasName extends BaseEntitySimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_ID", length = 20, nullable = false)
    private Long deviceId;

    @Column(name = "DEVICE_NAME", length = 50)
    private String deviceName;

}
