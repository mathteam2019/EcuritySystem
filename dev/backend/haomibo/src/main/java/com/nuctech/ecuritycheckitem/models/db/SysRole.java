package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
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
    Long roleId;

    @Column(name = "ORG_ID", length = 20)
    Long orgId;

    @Column(name = "ROLE_NUMBER", length = 50)
    String roleNumber;

    @Column(name = "ROLE_NAME", length = 50)
    String roleName;

    @Column(name = "ROLE_FLAG", length = 10)
    String roleFlag;

    @Column(name = "STATUS", length = 10)
    String status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_role_resource",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID")}
    )
    Set<SysResource> resources;


}
