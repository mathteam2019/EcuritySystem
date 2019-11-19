/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName SerArchiveIndicators.java
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

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_ARCHIVE_INDICATORS)
@Table(name = "ser_archives_indicators")
public class SerArchiveIndicators extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDICATORS_ID", length = 20, nullable = false)
    Long indicators_id;

    @Column(name = "ARCHIVES_TEMPLATE_ID", length = 20)
    Long archives_template_id;

    @Column(name = "INDICATORS_NAME", length = 20)
    String indicators_name;

    @Column(name = "INDICATORS_UNIT", length = 20)
    String indicators_unit;

    @Column(name = "IS_NULL", length = 20)
    String is_null;

}
