/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName SerPlatformCheckParams.java
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
@JsonFilter(ModelJsonFilters.FILTER_SER_PLATFORM_CHECK_PARAMS)
@Table(name = "ser_platform_check_params")
public class SerPlatformCheckParams extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCAN_ID", length = 20, nullable = false)
    Long scanId;


    @Column(name = "SCAN_RECOGNISE_COLOUR", length = 255)
    String scanRecogniseColour;

    @Column(name = "SCAN_OVERTIME", length = 11)
    Long scanOverTime;

    @Column(name = "JUDGE_ASSIGN_TIME", length = 11)
    Long judgeAssignTime;

    @Column(name = "JUDGE_PROCESSING_TIME", length = 11)
    Long judgeProcessingTime;

    @Column(name = "JUDGE_SCAN_OVERTIME", length = 11)
    Long judgeScanOvertime;

    @Column(name = "JUDGE_RECOGNISE_COLOUR", length = 255)
    String judgeRecogniseColour;

    @Column(name = "HAND_OVERTIME", length = 11)
    Long handOverTime;

    @Column(name = "HAND_RECOGNISE_COLOUR", length = 255)
    String handRecogniseColour;

    @Column(name = "HISTORY_DATA_STORAGE", length = 255)
    String historyDataStorage;

    @Column(name = "HISTORY_DATA_EXPORT", length = 255)
    String displayDataExport;

    @Column(name = "DISPLAY_DELETE_SUSPICION", length = 10)
    Long displayDeleteSuspicion;

    @Column(name = "DISPLAY_DELETE_SUSPICION_COLOUR", length = 255)
    String displayDeleteSuspicionColour;


}
