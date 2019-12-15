package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatisticsResponse;

import java.util.Date;

public interface StatisticsByDeviceService {

    /**
     * get statistics by device
     * @param deviceCategoryId
     * @param deviceId
     * @param startTime
     * @param endTime
     * @param currentPage
     * @param perPage
     * @return
     */
    TotalStatisticsResponse getStatistics(Long deviceCategoryId, Long deviceId, Date startTime, Date endTime, Integer currentPage, Integer perPage);

}
