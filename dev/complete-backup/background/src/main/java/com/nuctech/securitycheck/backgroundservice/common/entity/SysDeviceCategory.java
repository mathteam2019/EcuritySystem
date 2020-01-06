package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysDeviceCategory
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
@Table(name = "sys_device_category")
public class SysDeviceCategory extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID", length = 20, nullable = false)
    private Long categoryId;

    @Column(name = "PARENT_CATEGORY_ID", length = 20)
    private Long parentCategoryId;

    @Column(name = "CATEGORY_NUMBER", length = 50)
    private String categoryNumber;

    @Column(name = "CATEGORY_NAME", length = 50)
    private String categoryName;

    @Column(name = "STATUS", length = 10)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CATEGORY_ID", referencedColumnName = "CATEGORY_ID", insertable = false, updatable = false)
    @MapsId("parent")
    private SysDeviceCategory parent;

}
