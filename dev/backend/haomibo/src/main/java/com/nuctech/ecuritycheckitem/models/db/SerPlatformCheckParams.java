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

    public static class HistoryData {
        public static final String BUSINESS = "1000002201";
        public static final String CARTOON = "1000002202";
        public static final String CONVERSION = "1000002203";
        public static final String ORIGINAL = "1000002204";
    }


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
    private String historyDataExport;

    @Column(name = "DISPLAY_DELETE_SUSPICION", length = 10)
    private Long displayDeleteSuspicion;

    @Column(name = "DISPLAY_DELETE_SUSPICION_COLOUR", length = 255)
    private String displayDeleteSuspicionColour;

    @javax.persistence.Transient
    String[] historyDataStorageList;

    @javax.persistence.Transient
    String[] historyDataExportList;


}
