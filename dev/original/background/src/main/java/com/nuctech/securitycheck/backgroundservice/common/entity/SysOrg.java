package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysOrg
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
@Table(name = "sys_org")
public class SysOrg extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORG_ID", length = 20, nullable = false)
    private Long orgId;

    @Column(name = "PARENT_ORG_ID", length = 20)
    private Long parentOrgId;

    @Column(name = "ORG_NAME", length = 50)
    private String orgName;

    @Column(name = "ORG_NUMBER", length = 50)
    private String orgNumber;

    @Column(name = "LEADER", length = 50)
    private String leader;

    @Column(name = "MOBILE", length = 20)
    private String mobile;

    @Column(name = "STATUS", length = 10)
    private String status;

}
