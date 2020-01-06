package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysUserGroupRole
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
@Table(name = "sys_user_group_role")
public class SysUserGroupRole extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_GROUP_ROLE_ID", length = 20, nullable = false)
    private Long userGroupRoleId;

    @Column(name = "USERGROUP_ID", length = 20)
    private Long userGroupId;

    @Column(name = "ROLE_ID", length = 20)
    private Long roleId;

}

