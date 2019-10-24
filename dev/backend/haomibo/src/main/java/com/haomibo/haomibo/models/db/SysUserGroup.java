package com.haomibo.haomibo.models.db;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "sys_user_group")
public class SysUserGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERGROUP_ID", length = 20, nullable = false)
    Long userGroupId;

    @Column(name = "ORG_ID", length = 20)
    Long orgId;

    @Column(name = "GROUP_NAME", length = 50)
    String groupName;

    @Column(name = "GROUP_FLAG", length = 10)
    String groupFlag;

    @Column(name = "CREATEDBY", length = 20)
    private Long createdBy;

    @Column(name = "CREATEDTIME", nullable = false)
    private Date createdTime;

    @Column(name = "EDITEDBY", length = 20)
    private Long editedBy;

    @Column(name = "EDITEDTIME", nullable = false)
    private Date editedTime;

    @Column(name = "NOTE", length = 500, nullable = false)
    private String note;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_user_group_user",
            joinColumns = {@JoinColumn(name = "USERGROUP_ID", referencedColumnName = "USERGROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")}
    )
    Set<SysUser> users;


}

