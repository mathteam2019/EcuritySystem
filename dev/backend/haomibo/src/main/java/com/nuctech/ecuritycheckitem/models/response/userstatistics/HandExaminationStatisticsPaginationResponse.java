package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HandExaminationStatisticsPaginationResponse {

    long total;
    long per_page;
    long current_page;
    long last_page;
    long from;
    long to;

    HandExaminationResponseModel totalStatistics;
    Map<Integer, HandExaminationResponseModel> detailedStatistics;

}