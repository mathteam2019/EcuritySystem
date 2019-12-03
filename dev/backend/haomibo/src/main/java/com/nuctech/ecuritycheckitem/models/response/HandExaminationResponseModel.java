package com.nuctech.ecuritycheckitem.models.response;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class HandExaminationResponseModel {

    String time;
    long total;
    long seizure;
    long noSeizure;
    long totalJudge;
    long missingReport;
    long falseReport;
    long artificialJudge;
    long artificialJudgeMissing;
    long artificialJudgeMistake;
    long intelligenceJudge;
    long intelligenceJudgeMissing;
    long intelligenceJudgeMistake;
    double maxDuration;
    double minDuration;
    double avgDuration;

}
