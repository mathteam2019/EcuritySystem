package com.nuctech.ecuritycheckitem.models.db;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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
    Long userGroupRoleId;

    @Column(name = "USERGROUP_ID", length = 20)
    Long userGroupId;

    @Column(name = "ROLE_ID", length = 20)
    Long roleId;

}

