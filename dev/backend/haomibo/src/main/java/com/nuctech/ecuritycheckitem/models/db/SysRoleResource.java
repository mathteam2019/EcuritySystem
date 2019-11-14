package com.nuctech.ecuritycheckitem.models.db;

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
@Table(name = "sys_role_resource")
public class SysRoleResource extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_RESOURCE_ID", length = 20, nullable = false)
    Long roleResourceId;

    @Column(name = "RESOURCE_ID", length = 20)
    Long resourceId;

    @Column(name = "ROLE_ID", length = 20)
    Long roleId;


}

