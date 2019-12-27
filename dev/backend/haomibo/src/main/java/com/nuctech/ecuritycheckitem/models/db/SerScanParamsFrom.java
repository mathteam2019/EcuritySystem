/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerScanParamsFrom）
 * 文件名：	SerScanParamsFrom.java
 * 描述：	SerScanParamsFrom Model
 * 作者名：	Choe
 * 日期：	2019/11/26
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
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_SCAN_PARAMS_FROM)
@Table(name = "ser_scan_params_from")
public class SerScanParamsFrom extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FROM_CONFIG_ID", length = 20, nullable = false)
    private Long fromConfigId;


    @Column(name = "SCAN_PARAMS_ID", length = 20)
    private Long scanParamsId;

    @Column(name = "FROM_DEVICE_ID", length = 20)
    private Long fromDeviceId;

    @Column(name = "DEVICE_ID", length = 20)
    private Long deviceId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    private SysDevice device; // Relation to SysDevice table.


}
