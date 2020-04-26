/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysDataGroup）
 * 文件名：	SysDataGroup.java
 * 描述：	SysDataGroup Model
 * 作者名：	Sandy
 * 日期：	2019/10/24
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysUserSimplifiedOnlyHasName;
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
@JsonFilter(ModelJsonFilters.FILTER_SYS_DATA_GROUP)
@Table(name = "sys_data_group")
public class SysDataGroup extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_GROUP_ID", length = 20, nullable = false)
    private Long dataGroupId;

    @Column(name = "ORG_ID", length = 20)
    private Long orgId;

    @Column(name = "DATA_GROUP_NUMBER", length = 50)
    private String dataGroupNumber;

    @Column(name = "DATA_GROUP_NAME", length = 50)
    private String dataGroupName;

    @Column(name = "DATA_GROUP_FLAG", length = 10)
    private String dataGroupFlag;

    @Column(name = "STATUS", length = 10)
    private String status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_data_group_user",
            joinColumns = {@JoinColumn(name = "DATA_GROUP_ID", referencedColumnName = "DATA_GROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")}
    )
    @OrderBy("userId ASC")
    private Set<SysUserSimplifiedOnlyHasName> users;


}

