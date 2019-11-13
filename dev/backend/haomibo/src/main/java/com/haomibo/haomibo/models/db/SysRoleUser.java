package com.haomibo.haomibo.models.db;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_role_user")
public class SysRoleUser extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_USER_ID", length = 20, nullable = false)
    Long roleUserId;

    @Column(name = "ROLE_ID", length = 20)
    Long roleId;

    @Column(name = "USER_ID", length = 20)
    Long userId;


}

