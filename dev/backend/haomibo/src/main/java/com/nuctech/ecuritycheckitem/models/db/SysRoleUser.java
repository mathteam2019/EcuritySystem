/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysRoleUser）
 * 文件名：	SysRoleUser.java
 * 描述：	SysRoleUser Model
 * 作者名：	Sandy
 * 日期：	2019/11/01
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
@Table(name = "sys_role_user")
public class SysRoleUser extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_USER_ID", length = 20, nullable = false)
    private Long roleUserId;

    @Column(name = "ROLE_ID", length = 20)
    private Long roleId;

    @Column(name = "USER_ID", length = 20)
    private Long userId;


}

