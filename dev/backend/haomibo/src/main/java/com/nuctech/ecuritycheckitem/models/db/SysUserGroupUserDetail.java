/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysUserGroupUser）
 * 文件名：	SysUserGroupUser.java
 * 描述：	SysUserGroupUser Model
 * 作者名：	Sandy
 * 日期：	2019/10/24
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.nuctech.ecuritycheckitem.models.simplifieddb.SysUserGroupSimple;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_user_group_user")
public class SysUserGroupUserDetail extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_USER_ID", length = 20, nullable = false)
    private Long groupUserId;

    @Column(name = "USER_ID", length = 20)
    private Long userId;

    @Column(name = "USERGROUP_ID", length = 20)
    private Long userGroupId;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUserGroupSimple userGroupSimple;
//
//    @ToString.Exclude
//    @ManyToMany()
//    @JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    private List<SysUserGroupSimple> userGroupList; // Relation to SysOrg table.



}

