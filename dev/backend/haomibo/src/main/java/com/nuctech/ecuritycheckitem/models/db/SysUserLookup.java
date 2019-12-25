/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/01
 * @CreatedBy Sandy.
 * @FileName SysUserLookup.java
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
@Table(name = "sys_user_lookup")
public class SysUserLookup extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOOKUP_ID", length = 20, nullable = false)
    private Long lookupId;

    @Column(name = "USER_ID", length = 20)
    private Long userId;

    @Column(name = "DATA_GROUP_ID", length = 20)
    private Long dataGroupId;


}

