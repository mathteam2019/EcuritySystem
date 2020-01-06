package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysUserGroupUser
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
@Table(name = "sys_user_group_user")
public class SysUserGroupUser extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_USER_ID", length = 20, nullable = false)
    private Long groupUserId;

    @Column(name = "USER_ID", length = 20)
    private Long userId;

    @Column(name = "USERGROUP_ID", length = 20)
    private Long userGroupId;

}

