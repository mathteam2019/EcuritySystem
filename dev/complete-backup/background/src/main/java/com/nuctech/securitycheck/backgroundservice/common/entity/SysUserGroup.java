package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * SysUserGroup
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
@Table(name = "sys_user_group")
public class SysUserGroup extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERGROUP_ID", length = 20, nullable = false)
    private Long userGroupId;

    @Column(name = "ORG_ID", length = 20)
    private Long orgId;

    @Column(name = "DATA_RANGE_CATEGORY", length = 10)
    private String dataRangeCategory;

    @Column(name = "GROUP_NUMBER", length = 50)
    private String groupNumber;

    @Column(name = "GROUP_NAME", length = 50)
    private String groupName;

    @Column(name = "GROUP_FLAG", length = 10)
    private String groupFlag;

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "sys_user_group_user",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")}
    )
    private Set<SysUser> users;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_user_group_role",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")}
    )
    private Set<SysRole> roles;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_user_group_lookup",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "DATA_GROUP_ID", referencedColumnName = "DATA_GROUP_ID")}
    )
    private Set<SysDataGroup> dataGroups;

}

