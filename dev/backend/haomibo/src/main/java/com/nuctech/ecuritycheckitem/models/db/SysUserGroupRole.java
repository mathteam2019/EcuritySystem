/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/10
 * @CreatedBy Sandy.
 * @FileName SysUserGroupRole.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.db;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
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

