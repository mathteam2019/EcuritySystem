package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerArchiveValue
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
@Table(name = "ser_archives_value")
public class SerArchiveValue extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VALUE_ID", length = 20, nullable = false)
    private Long id;

    @Column(name = "INDICATORS_ID", length = 20)
    private Long indicatorsId;

    @Column(name = "ARCHIVE_ID", length = 20)
    private Long archiveId;

    @Column(name = "VALUE", length = 255)
    private String value;

}
