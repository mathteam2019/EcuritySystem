package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Scan Statistics Response Body
 */
@Getter
@Setter
public
class ScanStatisticsResponse {

    ScanStatistics totalStatistics;
    Map<Integer, ScanStatistics> detailedStatistics;

    long total;
    long per_page;
    long current_page;
    long last_page;
    long from;
    long to;

}