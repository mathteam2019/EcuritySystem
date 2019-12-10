package com.nuctech.ecuritycheckitem.models.db;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import javax.persistence.*;
import java.io.Serializable;

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
    private Long imageId ;

    @Column(name = "SCAN_ID", length = 20)
    private Long scanId;

    @Column(name = "TASK_ID", length = 20, nullable = false)
    private Long taskId;

    @Column(name = "IMAGE_FORMAT", length = 10)
    private String imageFormat;

    @Column(name = "IMAGE_CATEGORY", length = 10)
    private String imageCategory;

    @Column(name = "IMAGE_URL", length = 200)
    private String imageUrl;

    @Column(name = "IMAGE_LABEL", length = 50)
    private String imageLabel;

    @Column(name = "STATUS", length = 10)
    private String status ;


}
