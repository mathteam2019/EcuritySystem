package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerArchiveIndicators
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
@Table(name = "ser_archives_indicators")
public class SerArchiveIndicators extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDICATORS_ID", length = 20, nullable = false)
    private Long indicatorsId;

    @Column(name = "ARCHIVES_TEMPLATE_ID", length = 20)
    private Long archivesTemplateId;

    @Column(name = "INDICATORS_NAME", length = 20)
    private String indicatorsName;

    @Column(name = "INDICATORS_UNIT", length = 20)
    private String indicatorsUnit;

    @Column(name = "IS_NULL", length = 20)
    private String isNull;

}
