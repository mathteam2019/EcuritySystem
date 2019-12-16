package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatisticsResponse;

import java.util.Date;

public interface StatisticsByUserService {

    /**
     * get statistics by user
     * @param modeId
     * @param userName
     * @param startTime
     * @param endTime
     * @param currentPage
     * @param perPage
     * @return
     */
    TotalStatisticsResponse getStatistics(Long modeId, String userName, Date startTime, Date endTime,  Integer currentPage, Integer perPage);

}
