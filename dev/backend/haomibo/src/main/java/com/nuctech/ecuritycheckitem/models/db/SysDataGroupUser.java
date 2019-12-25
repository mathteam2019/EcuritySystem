/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/24
 * @CreatedBy Sandy.
 * @FileName SysDataGroupUser.java
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
@Table(name = "sys_data_group_user")
public class SysDataGroupUser extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_GROUP_USER_ID", length = 20, nullable = false)
    private Long dataGroupUserId;

    @Column(name = "USER_ID", length = 20)
    private Long userId;

    @Column(name = "DATA_GROUP_ID", length = 20)
    private Long dataGroupId;

}

