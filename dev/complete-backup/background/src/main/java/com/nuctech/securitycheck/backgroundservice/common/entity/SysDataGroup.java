package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * SysDataGroup
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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

