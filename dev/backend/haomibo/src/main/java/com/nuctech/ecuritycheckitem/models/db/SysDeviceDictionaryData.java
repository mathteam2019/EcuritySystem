/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysDeviceDictionaryData）
 * 文件名：	SysDeviceDictionaryData.java
 * 描述：	SysDeviceDictionaryData Model
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
@JsonFilter(ModelJsonFilters.FILTER_SYS_DEVICE_DICTIONARY_DATA)
@Table(name = "sys_device_dictionary_data")
public class SysDeviceDictionaryData extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_ID", length = 20, nullable = false)
    private Long dataId;

    @Column(name = "DICTIONARY_ID", length = 20)
    private Long dictionaryId;

    @Column(name = "DICTIONARY_NAME", length = 50)
    private String dictionaryName;

    @Column(name = "DATA_CODE", length = 100)
    private String dataCode;

    @Column(name = "DATA_VALUE", length = 100)
    private String dataValue;

}
