package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * SysRole
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
@Table(name = "sys_role")
public class SysRole extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID", length = 20, nullable = false)
    private Long roleId;

    @Column(name = "ORG_ID", length = 20)
    private Long orgId;

    @Column(name = "ROLE_NUMBER", length = 50)
    private String roleNumber;

    @Column(name = "ROLE_NAME", length = 50)
    private String roleName;

    @Column(name = "ROLE_FLAG", length = 10)
    private String roleFlag;

    @Column(name = "STATUS", length = 10)
    private String status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_role_resource",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID")}
    )
    private Set<SysResource> resources;

}
