/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/13
 * @CreatedBy Sandy.
 * @FileName BaseEntity.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.db;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder(toBuilder = true)
/**
 * All Entity includes createdBy, createdTime, editedBy, editedTime and note.
 * This class is base class for other entities.
 */
public class BaseEntity {
    @Column(name = "CREATEDBY", length = 20)
    private Long createdBy;

    @Column(name = "CREATEDTIME", nullable = false)
    private Date createdTime;

    @Column(name = "EDITEDBY", length = 20)
    private Long editedBy;

    @Column(name = "EDITEDTIME", nullable = false)
    private Date editedTime;

    @Column(name = "NOTE", length = 500, nullable = false)
    private String note;

    /**
     * Adds created info.
     *
     * @param sysUser Represents editor's information
     * @return itself
     */
    public BaseEntity addCreatedInfo(SysUser sysUser) {
        this.createdBy = sysUser.getUserId();
        this.createdTime = new Date();
        return this;
    }

    /**
     * Adds Edited info
     *
     * @param sysUser Represents editor's information
     * @return itself
     */
    public BaseEntity addEditedInfo(SysUser sysUser) {
        this.editedBy = sysUser.getUserId();
        this.editedTime = new Date();
        return this;
    }

}
