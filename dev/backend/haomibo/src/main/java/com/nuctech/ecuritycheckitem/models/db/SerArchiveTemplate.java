/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName SerArchiveTemplate.java
 * @ModifyHistory
 *
 */

package com.nuctech.ecuritycheckitem.models.db;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_ARCHIVE_TEMPLATE)
@Table(name = "ser_archives_template")
public class SerArchiveTemplate extends BaseEntity implements Serializable {

    public static class Status {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
        public static final String YES = "yes";
        public static final String NO = "no";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ARCHIVES_TEMPLATE_ID", length = 20, nullable = false)
    Long archivesTemplateId;


    @Column(name = "S_TEMPLATE_NAME", length = 50)
    String templateName;

    @Column(name = "ARCHIVES_TEMPLATE_NUMBER", length = 50)
    String archivesTemplateNumber;


    @Column(name = "CATEGORY_ID", length = 20)
    Long categoryId;

    @Column(name = "MANUFACTURER", length = 10)
    String manufacturer;

    @Column(name = "ORIGINAL_MODEL", length = 50)
    String originalModel;

    @Column(name = "STATUS", length = 10)
    String status;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("category")
    SysDeviceCategory deviceCategory; // Relation to SysDeviceCategory table.


    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "ARCHIVES_TEMPLATE_ID", referencedColumnName = "ARCHIVES_TEMPLATE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("indicators")
    List<SerArchiveIndicators> archiveIndicatorsList; // Relation to SerArchiveIndicators table.
}
