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
@JsonFilter(ModelJsonFilters.FILTER_SYS_DATA_GROUP)
@Table(name = "sys_data_group")
public class SysDataGroup extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_GROUP_ID", length = 20, nullable = false)
    Long dataGroupId;

    @Column(name = "ORG_ID", length = 20)
    Long orgId;

    @Column(name = "DATA_GROUP_NUMBER", length = 50)
    String dataGroupNumber;

    @Column(name = "DATA_GROUP_NAME", length = 50)
    String dataGroupName;

    @Column(name = "DATA_GROUP_FLAG", length = 10)
    String dataGroupFlag;

    @Column(name = "STATUS", length = 10)
    String status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_data_group_user",
            joinColumns = {@JoinColumn(name = "DATA_GROUP_ID", referencedColumnName = "DATA_GROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")}
    )
    Set<SysUser> users;


}

