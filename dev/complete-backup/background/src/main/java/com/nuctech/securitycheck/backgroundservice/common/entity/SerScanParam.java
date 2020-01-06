package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerScanParam
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
@Table(name = "ser_scan_params")
public class SerScanParam extends BaseEntity implements Serializable {

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

}
