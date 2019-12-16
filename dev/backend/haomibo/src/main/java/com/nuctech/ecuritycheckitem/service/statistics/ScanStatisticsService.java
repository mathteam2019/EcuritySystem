package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatisticsResponse;

import java.util.Date;

public interface ScanStatisticsService {

    /**
     * Get scan statistics
     * @param fieldId
     * @param deviceId
     * @param userCategory
     * @param userName
     * @param startTime
     * @param endTime
     * @param statWidth
     * @param currentPage
     * @param perPage
     * @return
     */
    ScanStatisticsResponse getStatistics(Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage);

}
