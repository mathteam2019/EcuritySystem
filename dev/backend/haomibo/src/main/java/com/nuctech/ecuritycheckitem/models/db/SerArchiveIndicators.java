/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerArchiveIndicators）
 * 文件名：	SerArchiveIndicators.java
 * 描述：	SerArchiveIndicators Model
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

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_ARCHIVE_INDICATORS)
@Table(name = "ser_archives_indicators")
public class SerArchiveIndicators extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDICATORS_ID", length = 20, nullable = false)
    private Long indicatorsId;

    @Column(name = "ARCHIVES_TEMPLATE_ID", length = 20)
    private Long archivesTemplateId;

    @Column(name = "INDICATORS_NAME", length = 20)
    private String indicatorsName;

    @Column(name = "INDICATORS_UNIT", length = 20)
    private String indicatorsUnit;

    @Column(name = "IS_NULL", length = 20)
    private String isNull;

}
