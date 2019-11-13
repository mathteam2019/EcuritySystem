package com.haomibo.haomibo.models.db;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_user_group_lookup")
public class SysUserGroupLookup extends BaseEntity implements Serializable {

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


}

