package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

/**
 * Scan Statistics Response Body
 */
@Getter
@Setter
public
class ScanStatisticsResponse {

    ScanStatistics totalStatistics;
    TreeMap<Integer, ScanStatistics> detailedStatistics;

    long total;
    long per_page;
    long current_page;
    long last_page;
    long from;
    long to;

}