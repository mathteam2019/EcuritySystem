package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * HandExamination Statistics Response Body
 */
@Getter
@Setter
public class HandExaminationStatisticsResponse {
    HandExaminationStatisticsForPreview totalStatistics;
    Map<Integer, HandExaminationStatisticsForPreview> detailedStatistics;

    long total;
    long per_page;
    long current_page;
    long last_page;
    long from;
    long to;

}
