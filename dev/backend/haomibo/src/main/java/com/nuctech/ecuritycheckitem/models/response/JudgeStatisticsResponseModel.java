package com.nuctech.ecuritycheckitem.models.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JudgeStatisticsResponseModel {

    int time;
    long total;
    long suspiction;
    long noSuspiction;
    long atrResult;
    long assignResult;
    long artificialResult;
    double maxDuration;
    double minDuration;
    double avgDuration;


}
