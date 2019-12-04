package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Total Statistics Response Body
 */
@Getter
@Setter
public class TotalStatisticsResponse {

    TotalStatistics totalStatistics;
    Map<Long, TotalStatistics> detailedStatistics;

    long total;
    long per_page;
    long current_page;
    long last_page;
    long from;
    long to;
}
