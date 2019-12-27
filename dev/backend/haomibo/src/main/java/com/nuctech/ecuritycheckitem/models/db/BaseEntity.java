/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（BaseEntity）
 * 文件名：	BaseEntity.java
 * 描述：	All Entity includes createdBy, createdTime, editedBy, editedTime and note.
 *          This class is base class for other entities.
 * 作者名：	Sandy
 * 日期：	2019/11/13
 */

package com.nuctech.ecuritycheckitem.models.db;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
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
