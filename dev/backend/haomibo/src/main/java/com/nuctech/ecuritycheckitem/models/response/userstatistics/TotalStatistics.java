package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

/**
 * Total Statistics Response Body
 */
@Getter
@Setter
public class TotalStatistics {

    long id;
    long time;
    String name; //user or device name

    ScanStatistics scanStatistics;
    JudgeStatisticsModelForPreview judgeStatistics;
    HandExaminationStatisticsForPreview handExaminationStatistics;

}
