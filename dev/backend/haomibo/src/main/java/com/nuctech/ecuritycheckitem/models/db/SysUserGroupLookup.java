/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/10
 * @CreatedBy Sandy.
 * @FileName SysUserGroupLookup.java
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

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_user_group_lookup")
public class SysUserGroupLookup extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOOKUP_ID", length = 20, nullable = false)
    private Long lookupId;

    @Column(name = "USER_ID", length = 20)
    private Long userId;

    @Column(name = "DATA_GROUP_ID", length = 20)
    private Long dataGroupId;

    @Column(name = "USERGROUP_ID", length = 20)
    private Long userGroupId;


}

