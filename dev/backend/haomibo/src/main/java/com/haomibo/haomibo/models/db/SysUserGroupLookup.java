package com.haomibo.haomibo.models.db;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "sys_user_group_lookup")
public class SysUserGroupLookup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOOKUP_ID", length = 20, nullable = false)
    Long lookupId;

    @Column(name = "USER_ID", length = 20)
    Long userId;

    @Column(name = "DATA_GROUP_ID", length = 20)
    Long dataGroupId;

    @Column(name = "USERGROUP_ID", length = 20)
    Long userGroupId;

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


}
