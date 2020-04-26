/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerArchiveTemplate）
 * 文件名：	SerArchiveTemplate.java
 * 描述：	SerArchiveTemplate Model
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
@JsonFilter(ModelJsonFilters.FILTER_SER_ARCHIVE_TEMPLATE)
@Table(name = "ser_archives_template")
public class SerArchiveTemplate extends BaseEntity implements Serializable {

    public static class Status {
        public static final String ACTIVE = "1000000701";
        public static final String INACTIVE = "1000000702";
        public static final String YES = "1000000601";
        public static final String NO = "1000000602";
    }

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

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("category")
    private SysDeviceCategory deviceCategory; // Relation to SysDeviceCategory table.


    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "ARCHIVES_TEMPLATE_ID", referencedColumnName = "ARCHIVES_TEMPLATE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("indicators")
    @OrderBy("indicatorsId ASC")
    private List<SerArchiveIndicators> archiveIndicatorsList; // Relation to SerArchiveIndicators table.
}
