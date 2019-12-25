/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/24
 * @CreatedBy Sandy.
 * @FileName SysDataGroup.java
 * @ModifyHistory
 *
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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
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
    private Set<SysUser> users;


}

