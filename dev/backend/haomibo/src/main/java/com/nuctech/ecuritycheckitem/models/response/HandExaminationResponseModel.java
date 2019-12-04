package com.nuctech.ecuritycheckitem.models.response;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class HandExaminationResponseModel {

    Integer time;
    long total;
    long seizure;
    long noSeizure;
    long totalJudge;
    long missingReport;
    long mistakeReport;
    long artificialJudge;
    long artificialJudgeMissing;
    long artificialJudgeMistake;
    long intelligenceJudge;
    long intelligenceJudgeMissing;
    long intelligenceJudgeMistake;

    double missingReportRate;
    double mistakeReportRate;
    double artificialJudgeMissingRate;
    double artificialJudgeMistakeRate;
    double intelligenceJudgeMissingRate;
    double intelligenceJudgeMistakeRate;

    double maxDuration;
    double minDuration;
    double avgDuration;

}
