package com.nuctech.ecuritycheckitem.models.db;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_IMAGE)
@Table(name = "ser_image")
public class SerImage extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID", length = 20)
    Long imageId ;

    @Column(name = "SCAN_ID", length = 20)
    Long scanId;

    @Column(name = "TASK_ID", length = 20, nullable = false)
    Long taskId;

    @Column(name = "IMAGE_FORMAT", length = 10)
    String imageFormat;

    @Column(name = "IMAGE_CATEGORY", length = 10)
    String imageCategory;

    @Column(name = "IMAGE_URL", length = 200)
    String imageUrl;

    @Column(name = "IMAGE_LABEL", length = 50)
    String imageLabel;

    @Column(name = "STATUS", length = 10)
    String status ;

    @Column(name = "CREATEDBY", length = 20)
    private Long createdBy;

    @Column(name = "CREATEDTIME", nullable = false)
    private Date createdTime;

    @Column(name = "EDITEDBY", length = 20)
    private Long editedBy;

    @Column(name = "EDITEDTIME", nullable = false)
    private Date editedTime;

    @Column(name = "NOTE", length = 500, nullable = false)
    private String note;


}
