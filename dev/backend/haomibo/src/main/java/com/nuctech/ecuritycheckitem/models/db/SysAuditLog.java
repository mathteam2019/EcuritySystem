/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysAuditLog）
 * 文件名：	SysAuditLog.java
 * 描述：	SysAuditLog Model
 * 作者名：	Choe
 * 日期：	2019/11/21
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
@JsonFilter(ModelJsonFilters.FILTER_SYS_AUDIT_LOG)
@Table(name = "sys_audit_log")
//@Document(indexName = "mesindex", type = "SYS_AUDIT_LOG")
public class SysAuditLog extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    private Long id;

    @Column(name = "OPERATE_TIME", nullable = false)
    private Date operateTime;

    @Column(name = "CLIENT_IP", length = 255)
    private String clientIp;

    @Column(name = "OPERATOR_ID", length = 20)
    private Long operatorId;

    @Column(name = "OPERATE_ACCOUNT", length = 200)
    private String operateAccount;

    @Column(name = "OPERATE_OBJECT", length = 255)
    private String operateObject;

    @Column(name = "ACTION", length = 10)
    private String action;

    @Column(name = "OPERATE_RESULT", length = 10)
    private String operateResult;

    @Column(name = "OPERATE_CONTENT", length = 255)
    private String operateContent;

    @Column(name = "REASON_CODE", length = 255)
    private String reasonCode;

    @Column(name = "ONLINE_TIME", length = 11)
    private Long onlineTime;

    @ManyToOne()
    @JoinColumn(name = "OPERATOR_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUser user;

}
