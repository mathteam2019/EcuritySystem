package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Statistics Response Body
 */
@Getter
@Setter
@ToString
public class ScanStatistics {

    String axisLabel;
    long id;
    long time;
    long workingSeconds;
    long totalScan;
    long invalidScan;
    double invalidScanRate;
    long validScan;
    double validScanRate;
    long passedScan;
    double passedScanRate;
    long alarmScan;
    double alarmScanRate;

}