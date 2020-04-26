/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysUserGroup）
 * 文件名：	SysUserGroup.java
 * 描述：	SysUserGroup Model
 * 作者名：	Sandy
 * 日期：	2019/10/24
 */
package com.nuctech.ecuritycheckitem.models.simplifieddb;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_USER_GROUP)
@Table(name = "sys_user_group")
public class SysUserGroupSimple extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERGROUP_ID", length = 20, nullable = false)
    private Long userGroupId;

    @Column(name = "DATA_RANGE_CATEGORY", length = 10)
    private String dataRangeCategory;

    @Column(name = "GROUP_NUMBER", length = 50)
    private String groupNumber;

    @Column(name = "GROUP_NAME", length = 50)
    private String groupName;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_user_group_role",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")}
    )
    @OrderBy("roleId ASC")
    private Set<SysRoleSimple> roles; // Relation to SysRole table.


}

