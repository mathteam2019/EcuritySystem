package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatisticsResponse;

import java.util.Date;

public interface PreviewStatisticsService {


    TotalStatisticsResponse getStatistics( Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perpage);

}
