/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysAuditLog）
 * 文件名：	SysAuditLogDetail.java
 * 描述：	SysAuditLogDetail Model
 * 作者名：	Choe
 * 日期：	2019/01/19
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
@JsonFilter(ModelJsonFilters.FILTER_SYS_AUDIT_LOG_DETAIL)
@Table(name = "sys_audit_log_detail")
//@Document(indexName = "mesindex", type = "SYS_AUDIT_LOG")
public class SysAuditLogDetail extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    private Long id;

    @Column(name = "AUDIT_LOG_ID", length = 20)
    private Long auditLogId;

    @Column(name = "FIELD_NAME", length = 255)
    private String fieldName;

    @Column(name = "VALUE_BEFORE", length = 2000)
    private String valueBefore;

    @Column(name = "VALUE_AFTER", length = 2000)
    private String valueAfter;


}
