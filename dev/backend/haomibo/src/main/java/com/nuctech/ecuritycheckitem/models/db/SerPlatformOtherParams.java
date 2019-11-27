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
@JsonFilter(ModelJsonFilters.FILTER_SER_PLATFORM_OTHER_PARAMS)
@Table(name = "ser_platform_other_params")
public class SerPlatformOtherParams implements Serializable {

    public static class Setting {
        public static final String HIGH = "high";
        public static final String MIDDLE = "middle";
        public static final String LOW = "low";
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    Long id;


    @Column(name = "INITIAL_PASSWORD", length = 255)
    String initialPassword;

    @Column(name = "LOGIN_NUMBER", length = 11)
    Long loginNumber;

    @Column(name = "LOG_MAX_NUMBER", length = 11)
    Long logMaxNumber;

    @Column(name = "DEVICE_TRAFFIC_SETTINGS", length = 10)
    String deviceTrafficSettings;

    @Column(name = "STORAGE_DETECTION_CYCLE", length = 11)
    Long storageDetectionCycle;

    @Column(name = "STORAGE_ALARM", length = 11)
    Long storageAlarm;

    @Column(name = "HISTORY_DATA_CYCLE", length = 11)
    Long historyDataCycle;




}
