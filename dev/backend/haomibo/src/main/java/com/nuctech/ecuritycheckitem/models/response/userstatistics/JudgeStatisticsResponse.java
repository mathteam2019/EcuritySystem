package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Judge Statistics Response Body
 */
@Getter
@Setter
public class JudgeStatisticsResponse {
    JudgeStatisticsModelForPreview totalStatistics;
    Map<Integer, JudgeStatisticsModelForPreview> detailedStatistics;

    long total;
    long per_page;
    long current_page;
    long last_page;
    long from;
    long to;

}

