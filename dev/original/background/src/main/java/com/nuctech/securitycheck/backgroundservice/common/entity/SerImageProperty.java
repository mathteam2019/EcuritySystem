package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerImageProperty
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
@Table(name = "ser_image_property")
public class SerImageProperty extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_PROPERTY_ID", length = 20)
    private Long imagePropertyId;

    @ManyToOne
    @JoinColumn(name = "IMAGE_ID")
    private SerImage serImage;

    @Column(name = "IMAGE_PROPERTY_NAME", length = 50)
    String imagePropertyName;

    @Column(name = "IMAGE_PROPERTY_VALUE", length = 50)
    private String imagePropertyValue;

}
