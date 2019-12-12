package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.InvalidTaskController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatisticsResponse;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.Map;

public interface StatisticsService {

    TotalStatisticsResponse getStatisticsByUser(Long modeId, String userName, Date startDate, Date endDate, Integer currentPage, Integer perPage);
    TotalStatisticsResponse getStatisticsByDevice(Long deviceCategoryId, Long deviceId, Date startDate, Date endDate, Integer currentPage, Integer perPage);

}
