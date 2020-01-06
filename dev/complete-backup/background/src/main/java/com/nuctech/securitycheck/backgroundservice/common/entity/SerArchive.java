package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * SerArchive
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
@Table(name = "ser_archives")
public class SerArchive extends BaseEntity implements Serializable {

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

    @Column(name = "STATUS", length = 10)
    private String status;

    @Column(name = "IMAGE_URL", length = 200)
    private String imageUrl;

    @ManyToOne()
    @JoinColumn(name = "ARCHIVES_TEMPLATE_ID", referencedColumnName = "ARCHIVES_TEMPLATE_ID", insertable = false, updatable = false)
    @MapsId("template")
    private SerArchiveTemplate archiveTemplate;

    @OneToMany()
    @JoinColumn(name = "ARCHIVE_ID", referencedColumnName = "ARCHIVE_ID", insertable = false, updatable = false)
    @MapsId("value")
    private List<SerArchiveValue> archiveValueList;

}
