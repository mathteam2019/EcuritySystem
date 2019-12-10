package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Hand Examination Response Body
 */
@Getter
@Setter
@ToString
public class HandExaminationStatisticsForPreview {

    String axisLabel;
    long id;
    long time;
    double workingSeconds;
    long totalHandExamination;
    long seizureHandExamination;
    double seizureHandExaminationRate;
    long noSeizureHandExamination;
    double noSeizureHandExaminationRate;

    long checkDuration;

}