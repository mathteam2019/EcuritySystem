package com.nuctech.securitycheck.backgroundservice.common.entity;

import com.nuctech.securitycheck.backgroundservice.common.utils.DateUtil;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * BaseEntity
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@MappedSuperclass
@Data
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

    public BaseEntity addCreatedInfo(SysUser sysUser) {
        this.createdBy = sysUser.getUserId();
        this.createdTime = DateUtil.getCurrentDate();
        return this;
    }

    public BaseEntity addEditedInfo(SysUser sysUser) {
        this.editedBy = sysUser.getUserId();
        this.editedTime = DateUtil.getCurrentDate();
        return this;
    }

    public BaseEntity addNote(String note) {
        this.note = note;
        return this;
    }

}
