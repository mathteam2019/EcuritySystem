/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysDeviceSimplifiedOnlyHasName）
 * 文件名：	SysDeviceSimplifiedOnlyHasName.java
 * 描述：	Simplified SysDevice Model only has name
 * 作者名：	Tiny
 * 日期：	2019/12/27
 */


package com.nuctech.ecuritycheckitem.models.simplifieddb;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_DEVICE)
@Table(name = "sys_device")
public class SysDeviceSimplifiedOnlyHasName implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_ID", length = 20, nullable = false)
    private Long deviceId;

    @Column(name = "DEVICE_NAME", length = 50)
    private String deviceName;

    @Column(name = "DEVICE_SERIAL", length = 50)
    private String deviceSerial;

    @Column(name = "CATEGORY_ID", length = 20)
    private Long categoryId;

    @Column(name = "GUID", length = 255)
    private String guid;

}
