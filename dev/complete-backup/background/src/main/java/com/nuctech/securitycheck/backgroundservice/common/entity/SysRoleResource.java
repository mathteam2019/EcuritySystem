package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysRoleResource
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
@Table(name = "sys_role_resource")
public class SysRoleResource extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_RESOURCE_ID", length = 20, nullable = false)
    private Long roleResourceId;

    @Column(name = "RESOURCE_ID", length = 20)
    private Long resourceId;

    @Column(name = "ROLE_ID", length = 20)
    private Long roleId;

}
