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
@Table(name = "sys_user_group_user")
public class SysUserGroupUser extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_USER_ID", length = 20, nullable = false)
    Long groupUserId;

    @Column(name = "USER_ID", length = 20)
    Long userId;

    @Column(name = "USERGROUP_ID", length = 20)
    Long userGroupId;


}

