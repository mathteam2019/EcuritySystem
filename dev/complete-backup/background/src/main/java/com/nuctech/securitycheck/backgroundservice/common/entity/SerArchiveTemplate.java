package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * SerArchiveTemplate
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
@Table(name = "ser_archives_template")
public class SerArchiveTemplate extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ARCHIVES_TEMPLATE_ID", length = 20, nullable = false)
    private Long archivesTemplateId;

    @Column(name = "S_TEMPLATE_NAME", length = 50)
    private String templateName;

    @Column(name = "ARCHIVES_TEMPLATE_NUMBER", length = 50)
    private String archivesTemplateNumber;

    @Column(name = "CATEGORY_ID", length = 20)
    private Long categoryId;

    @Column(name = "MANUFACTURER", length = 10)
    private String manufacturer;

    @Column(name = "ORIGINAL_MODEL", length = 50)
    private String originalModel;

    @Column(name = "STATUS", length = 10)
    private String status;

    @ManyToOne()
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", insertable = false, updatable = false)
    @MapsId("category")
    private SysDeviceCategory deviceCategory;

    @OneToMany()
    @JoinColumn(name = "ARCHIVES_TEMPLATE_ID", referencedColumnName = "ARCHIVES_TEMPLATE_ID", insertable = false, updatable = false)
    @MapsId("indicators")
    private List<SerArchiveIndicators> archiveIndicatorsList;

}
