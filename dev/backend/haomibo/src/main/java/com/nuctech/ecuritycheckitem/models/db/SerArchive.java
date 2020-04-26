/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerArchive）
 * 文件名：	SerArchive.java
 * 描述：	SerArchive Model
 * 作者名：	Choe
 * 日期：	2019/11/19
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

    @Column(name = "STATUS", length = 10)
    private String status;

    @Column(name = "IMAGE_URL", length = 200)
    private String imageUrl;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARCHIVES_TEMPLATE_ID", referencedColumnName = "ARCHIVES_TEMPLATE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("template")
    private SerArchiveTemplate archiveTemplate; // Relation to SerArchivesTemplate table.

    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "ARCHIVE_ID", referencedColumnName = "ARCHIVE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("value")
    @OrderBy("id ASC")
    private List<SerArchiveValue> archiveValueList; // Relation to SerArchivesValue table.
}
