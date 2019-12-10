package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Judge Statistics Body
 */
@Getter
@Setter
@ToString
public class JudgeStatisticsModelForPreview {

    String axisLabel;
    long id;
    long time;
    double workingSeconds;
    long totalJudge;
    long noSuspictionJudge;
    double noSuspictionJudgeRate;
    long suspictionJudge;
    double suspictionJudgeRate;

}
