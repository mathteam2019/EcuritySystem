/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerDevLog）
 * 文件名：	SerDevLog.java
 * 描述：	SerDevLog Model
 * 作者名：	Choe
 * 日期：	2019/11/21
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysDeviceSimple;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysDeviceSimplifiedOnlyHasName;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_DEV_LOG)
@Table(name = "ser_dev_log")
public class SerDevLog extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    private Long id;

    @Column(name = "GUID", length = 255)
    private String guid;

    @Column(name = "DEV_TYPE", length = 10)
    private String devType;

    @Column(name = "LOGIN_NAME", length = 20)
    private String loginName;

    @Column(name = "CATEGORY", length = 2)
    private Long category;

    @Column(name = "LEVEL", length = 11)
    private Long level;

    @Column(name = "CONTENT", length = 2000)
    private String content;

    @Column(name = "TIME", nullable = false)
    private Date time;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GUID", referencedColumnName = "GUID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDeviceSimplifiedOnlyHasName device;

//    @ToString.Exclude
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "LOGIN_NAME", referencedColumnName = "USER_ACCOUNT", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    @MapsId("user")
//    private SysUser user;



    
}
