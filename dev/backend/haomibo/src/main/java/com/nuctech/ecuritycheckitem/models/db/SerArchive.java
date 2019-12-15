/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName SerArchive.java
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

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_ARCHIVES)
@Table(name = "ser_archives")
public class SerArchive extends BaseEntity implements Serializable {

    public static class Status {
        public static final String ACTIVE = "1000000701";
        public static final String INACTIVE = "1000000702";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ARCHIVE_ID", length = 20, nullable = false)
    private Long archiveId;


    @Column(name = "ARCHIVES_TEMPLATE_ID", length = 20)
    private Long archivesTemplateId;


    @Column(name = "ARCHIVES_NAME", length = 50)
    private String archivesName;

    @Column(name = "ARCHIVES_NUMBER", length = 50)
    private String archivesNumber;

//    @Column(name = "CATEGORY_ID", length = 20)
//    private Long categoryId;
//
//    @Column(name = "MANUFACTURER", length = 10)
//    private String manufacturer;
//
//    @Column(name = "ORIGINAL_MODEL", length = 50)
//    private String originalModel;

    @Column(name = "STATUS", length = 10)
    private String status;

    @Column(name = "IMAGE_URL", length = 200)
    private String imageUrl;

//    @ToString.Exclude
//    @ManyToOne()
//    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    @MapsId("category")
//    private SysDeviceCategory deviceCategory; // Relation to SysDeviceCategory table.

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "ARCHIVES_TEMPLATE_ID", referencedColumnName = "ARCHIVES_TEMPLATE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("template")
    private SerArchiveTemplate archiveTemplate; // Relation to SerArchivesTemplate table.

    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "ARCHIVE_ID", referencedColumnName = "ARCHIVE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("value")
    private List<SerArchiveValue> archiveValueList; // Relation to SerArchivesValue table.
}
