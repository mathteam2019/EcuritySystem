package com.haomibo.haomibo.models.db;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder(toBuilder = true)
public class BaseEntity {
    @Column(name = "CREATEDBY", length = 20)
    Long createdBy;

    @Column(name = "CREATEDTIME", nullable = false)
    Date createdTime;

    @Column(name = "EDITEDBY", length = 20)
    Long editedBy;

    @Column(name = "EDITEDTIME", nullable = false)
    Date editedTime;

    @Column(name = "NOTE", length = 500, nullable = false)
    String note;

    public void addCreatedInfo(SysUser sysUser) {
        this.createdBy = sysUser.getUserId();
        this.createdTime = new Date();
    }

    public void addEditedInfo(SysUser sysUser) {
        this.editedBy = sysUser.getUserId();
        this.editedTime = new Date();
    }

}
