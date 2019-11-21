/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName SerScanParamsFrom.java
 * @ModifyHistory
 *
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_SCAN_PARAMS_FROM)
@Table(name = "ser_scan_params_from")
public class SerScanParamsFrom extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FROM_CONFIG_ID", length = 20, nullable = false)
    Long fromConfigId;


    @Column(name = "SCAN_PARAMS_ID", length = 20)
    Long scanParamsId;

    @Column(name = "FROM_DEVICE_ID", length = 20)
    Long fromDeviceId;

    @Column(name = "DEVICE_ID", length = 20)
    Long deviceId;


}
