/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/24
 * @CreatedBy Sandy.
 * @FileName SysUserGroup.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
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
public class SysUserGroup extends BaseEntity implements Serializable {


    @AllArgsConstructor
    @Getter
    public enum DataRangeCategory {
        PERSON("person"),
        GROUP("group"),
        ALL("all"),
        SPECIFIED("specified");

        String value;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERGROUP_ID", length = 20, nullable = false)
    Long userGroupId;

    @Column(name = "ORG_ID", length = 20)
    Long orgId;

    @Column(name = "DATA_RANGE_CATEGORY", length = 10)
    String dataRangeCategory;

    @Column(name = "GROUP_NUMBER", length = 50)
    String groupNumber;

    @Column(name = "GROUP_NAME", length = 50)
    String groupName;

    @Column(name = "GROUP_FLAG", length = 10)
    String groupFlag;

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "sys_user_group_user",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")}
    )
    Set<SysUser> users; // Relation to sysUser table.

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_user_group_role",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")}
    )
    Set<SysRole> roles; // Relation to SysRole table.

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_user_group_lookup",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "DATA_GROUP_ID", referencedColumnName = "DATA_GROUP_ID")}
    )
    Set<SysDataGroup> dataGroups;

}

