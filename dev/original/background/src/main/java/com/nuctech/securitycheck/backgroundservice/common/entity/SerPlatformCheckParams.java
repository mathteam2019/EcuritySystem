package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerPlatformCheckParams
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
@Table(name = "ser_platform_check_params")
public class SerPlatformCheckParams extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCAN_ID", length = 20, nullable = false)
    private Long scanId;

    @Column(name = "SCAN_RECOGNISE_COLOUR", length = 255)
    private String scanRecogniseColour;

    @Column(name = "SCAN_OVERTIME", length = 11)
    private Long scanOverTime;

    @Column(name = "JUDGE_ASSIGN_TIME", length = 11)
    private Long judgeAssignTime;

    @Column(name = "JUDGE_PROCESSING_TIME", length = 11)
    private Long judgeProcessingTime;

    @Column(name = "JUDGE_SCAN_OVERTIME", length = 11)
    private Long judgeScanOvertime;

    @Column(name = "JUDGE_RECOGNISE_COLOUR", length = 255)
    private String judgeRecogniseColour;

    @Column(name = "HAND_OVERTIME", length = 11)
    private Long handOverTime;

    @Column(name = "HAND_RECOGNISE_COLOUR", length = 255)
    private String handRecogniseColour;

    @Column(name = "HISTORY_DATA_STORAGE", length = 255)
    private String historyDataStorage;

    @Column(name = "HISTORY_DATA_EXPORT", length = 255)
    private String displayDataExport;

    @Column(name = "DISPLAY_DELETE_SUSPICION", length = 10)
    private Long displayDeleteSuspicion;

    @Column(name = "DISPLAY_DELETE_SUSPICION_COLOUR", length = 255)
    private String displayDeleteSuspicionColour;

}
