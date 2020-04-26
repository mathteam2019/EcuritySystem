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
package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysDataGroupSimple;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysRoleSimple;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysUserSimplifiedOnlyHasName;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
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
public class SysUserGroup extends BaseEntity implements Serializable {


    @AllArgsConstructor
    @Getter
    public enum DataRangeCategory {
        PERSON("1000000501"),
        GROUP("1000000506"),
        ALL("1000000504"),
        SPECIFIED("1000000505");

        String value;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERGROUP_ID", length = 20, nullable = false)
    private Long userGroupId;

    @Column(name = "ORG_ID", length = 20)
    private Long orgId;

    @Column(name = "DATA_RANGE_CATEGORY", length = 10)
    private String dataRangeCategory;

    @Column(name = "GROUP_NUMBER", length = 50)
    private String groupNumber;

    @Column(name = "GROUP_NAME", length = 50)
    private String groupName;

    @Column(name = "GROUP_FLAG", length = 10)
    private String groupFlag;

    @OneToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "sys_user_group_user",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")}
    )
    @OrderBy("userId ASC")
    private Set<SysUserSimplifiedOnlyHasName> users; // Relation to sysUser table.

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sys_user_group_role",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")}
    )
    @OrderBy("roleId ASC")
    private Set<SysRoleSimple> roles; // Relation to SysRole table.

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sys_user_group_lookup",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "DATA_GROUP_ID", referencedColumnName = "DATA_GROUP_ID")}
    )
    @OrderBy("dataGroupId ASC")
    private List<SysDataGroupSimple> dataGroups;

}

