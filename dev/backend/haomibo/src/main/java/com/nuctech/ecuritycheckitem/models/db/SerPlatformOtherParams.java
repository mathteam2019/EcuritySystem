/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName SerPlatformOtherParams.java
 * @ModifyHistory
 *
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_PLATFORM_OTHER_PARAMS)
@Table(name = "ser_platform_other_params")
public class SerPlatformOtherParams implements Serializable {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    private Long id;


    @Column(name = "INITIAL_PASSWORD", length = 255)
    private String initialPassword;

    @Column(name = "LOGIN_NUMBER", length = 11)
    private Long loginNumber;

    @Column(name = "LOG_MAX_NUMBER", length = 11)
    private Long logMaxNumber;

    @Column(name = "DEVICE_TRAFFIC_SETTINGS", length = 11)
    private Integer deviceTrafficSettings;

    @Column(name = "DEVICE_TRAFFIC_HIGH", length = 11)
    private Integer deviceTrafficHigh;

    @Column(name = "DEVICE_TRAFFIC_MIDDLE", length = 11)
    private Integer deviceTrafficMiddle;

    @Column(name = "STORAGE_DETECTION_CYCLE", length = 11)
    private Integer storageDetectionCycle;

    @Column(name = "STORAGE_ALARM", length = 11)
    private Integer storageAlarm;

    @Column(name = "HISTORY_DATA_CYCLE", length = 11)
    private Integer historyDataCycle;




}
