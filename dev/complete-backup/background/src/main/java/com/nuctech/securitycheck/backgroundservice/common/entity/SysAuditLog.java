package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * SysAuditLog
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "sys_audit_log")
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
    private String womanManualGender;

    @Column(name = "ONLINE_TIME", length = 11)
    private Long onlineTime;

}
