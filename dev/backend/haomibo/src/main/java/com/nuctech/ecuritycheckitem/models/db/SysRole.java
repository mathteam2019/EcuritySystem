/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/24
 * @CreatedBy Sandy.
 * @FileName SysRole.java
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
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID")}
    )
    private Set<SysResource> resources;


}
