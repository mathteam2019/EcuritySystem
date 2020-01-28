/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ForbiddenToken）
 * 文件名：	ForbiddenToken.java
 * 描述：	ForbiddenToken Model
 * 作者名：	Sandy
 * 日期：	2019/10/15
 */

package com.nuctech.ecuritycheckitem.models.db;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sys_forbidden_token")
public class ForbiddenToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKEN_ID", nullable = false)
    private Integer id;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "CREATEDTIME", nullable = false)
    private Date createdTime;

    @Column(name = "EDITEDTIME", nullable = false)
    private Date editedTime;

}

