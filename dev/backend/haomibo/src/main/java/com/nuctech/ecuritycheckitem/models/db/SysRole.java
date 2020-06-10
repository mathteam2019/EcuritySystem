/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysRole）
 * 文件名：	SysRole.java
 * 描述：	SysRole Model
 * 作者名：	Sandy
 * 日期：	2019/10/19
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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
@JsonFilter(ModelJsonFilters.FILTER_SYS_ROLE)
@Table(name = "sys_role")
public class SysRole extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID", length = 20, nullable = false)
    private Long roleId;

    @Column(name = "ORG_ID", length = 20)
    private Long orgId;

    @Column(name = "ROLE_NUMBER", length = 50)
    private String roleNumber;

    @Column(name = "ROLE_NAME", length = 50)
    private String roleName;

    @Column(name = "ROLE_FLAG", length = 10)
    private String roleFlag;

    @Column(name = "STATUS", length = 10)
    private String status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_role_resource",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", insertable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID")}
    )
    @OrderBy("resourceId ASC")
    private Set<SysResource> resources;


}
