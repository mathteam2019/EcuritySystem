package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatisticsResponse;

import java.util.Date;

public interface StatisticsByUserService {

    TotalStatisticsResponse getStatistics(Long modeId, String userName, Date startDate, Date endDate, Integer currentPage, Integer perPage);

}
