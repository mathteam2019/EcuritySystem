package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysResource
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
@Table(name = "sys_resource")
public class SysResource extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESOURCE_ID", length = 20, nullable = false)
    private Long resourceId;

    @Column(name = "PARENT_RESOURCE_ID", length = 20)
    private Long parentResourceId;

    @Column(name = "RESOURCE_NAME", length = 50)
    private String resourceName;

    @Column(name = "RESOURCE_CAPTION", length = 50)
    private String resourceCaption;

    @Column(name = "RESOURCE_URL", length = 200)
    private String resourceUrl;

    @Column(name = "RESOURCE_CATEGORY", length = 10)
    String resourceCategory;

}

