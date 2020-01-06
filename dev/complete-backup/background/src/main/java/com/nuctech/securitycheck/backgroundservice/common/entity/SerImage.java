package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerImage
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
@Table(name = "ser_image")
public class SerImage extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID", length = 20)
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "SCAN_ID")
    private SerScan serScan;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private SerTask serTask;

    @Column(name = "IMAGE_FORMAT", length = 10)
    private String imageFormat;

    @Column(name = "IMAGE_CATEGORY", length = 10)
    private String imageCategory;

    @Column(name = "IMAGE_URL", length = 200)
    private String imageUrl;

    @Column(name = "IMAGE_LABEL", length = 50)
    private String imageLabel;

    @Column(name = "STATUS", length = 10)
    private String status;

}
