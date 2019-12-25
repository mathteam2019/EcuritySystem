/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName SerScanParam.java
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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_SCAN_PARAM)
@Table(name = "ser_scan_params")
public class SerScanParam extends BaseEntity implements Serializable {

    public static class Status {
        public static final String ACTIVE = "1000000701";
        public static final String INACTIVE = "1000000702";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCAN_PARAMS_ID", length = 20, nullable = false)
    private Long scanParamsId;


    @Column(name = "DEV_ID", length = 20)
    private Long deviceId;


    @Column(name = "AIRCALIWARNTIME", length = 11)
    private Long airCaliWarnTime;

    @Column(name = "STANDBYTIME", length = 11)
    private Long standByTime;

    @Column(name = "ALARMSOUND", length = 10)
    private String alarmSound;

    @Column(name = "PASSSOUND", length = 10)
    private String passSound;

    @Column(name = "POSERRORSOUND", length = 10)
    private String posErrorSound;

    @Column(name = "STANDSOUND", length = 10)
    private String standSound;

    @Column(name = "SCANSOUND", length = 10)
    private String scanSound;

    @Column(name = "SCANOVERUSESOUND", length = 10)
    private String scanOverUseSound;

    @Column(name = "AUTORECOGNISE", length = 10)
    private String autoRecognise;

    @Column(name = "RECOGNITIONRATE", length = 11)
    private Long recognitionRate;

    @Column(name = "SAVESCANDATA", length = 10)
    private String saveScanData;

    @Column(name = "SAVESUSPECTDATA", length = 10)
    private String saveSuspectData;

    @Column(name = "FACIALBLURRING", length = 10)
    private String facialBlurring;

    @Column(name = "CHESTBLURRING", length = 10)
    private String chestBlurring;

    @Column(name = "HIPBLURRING", length = 10)
    private String hipBlurring;

    @Column(name = "GROINBLURRING", length = 10)
    private String groinBlurring;

    @Column(name = "ATUOCONFIG", length = 10)
    private String autoConfig;

    @Column(name = "DICTIONARY_NAME", length = 50)
    private String dictionaryName;

    @Column(name = "STORAGE_ALARM", length = 10)
    private Integer deviceStorageAlarm;

    @Column(name = "STORAGE_ALARM_PERCENT", length = 10)
    private Integer deviceStorageAlarmPercent;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEV_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    private SysDevice device; // Relation to SysDevice table.

    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "SCAN_PARAMS_ID", referencedColumnName = "SCAN_PARAMS_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("from_param")
    private List<SerScanParamsFrom> fromParamsList; // Relation to SerScanParamsFrom table.

}
