package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerPlatformOtherParams
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
@Table(name = "ser_platform_other_params")
public class SerPlatformOtherParams implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    private Long scanId;

    @Column(name = "INITIAL_PASSWORD", length = 255)
    private String initialPassword;

    @Column(name = "LOGIN_NUMBER", length = 11)
    private Long loginNumber;

    @Column(name = "LOG_MAX_NUMBER", length = 11)
    private Long logMaxNumber;

    @Column(name = "DEVICE_TRAFFIC_SETTINGS", length = 11)
    private Long deviceTrafficSettings;

    @Column(name = "STORAGE_DETECTION_CYCLE", length = 11)
    private Long storageDetectionCycle;

    @Column(name = "STORAGE_ALARM", length = 11)
    private Long storageAlarm;

    @Column(name = "HISTORY_DATA_CYCLE", length = 11)
    private Long historyDataCycle;

}
