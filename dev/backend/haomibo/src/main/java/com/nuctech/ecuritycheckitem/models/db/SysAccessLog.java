/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName SysAccessLog.java
 * @ModifyHistory
 *
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
@JsonFilter(ModelJsonFilters.FILTER_SYS_ACCESS_LOG)
@Table(name = "sys_access_log")
public class SysAccessLog extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    Long id;

    @Column(name = "OPERATE_TIME", nullable = false)
    Date operateTime;

    @Column(name = "CLIENT_IP", length = 255)
    String clientIp;

    @Column(name = "OPERATE_ID", length = 20)
    Long operateId;

    @Column(name = "OPERATE_ACCOUNT", length = 200)
    String operateAccount;

    @Column(name = "ACTION", length = 10)
    String action;

    @Column(name = "OPERATE_RESULT", length = 10)
    String operateResult;

    @Column(name = "REASON_CODE", length = 255)
    String womanManualGender;

    @Column(name = "ONLINE_TIME", length = 11)
    Long onlineTime;
    
}