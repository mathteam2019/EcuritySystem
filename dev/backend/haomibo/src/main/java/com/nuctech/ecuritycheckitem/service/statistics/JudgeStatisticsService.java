/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（JudgeStatisticsService）
 * 文件名：	JudgeStatisticsService.java
 * 描述：	JudgeStatisticsService interface
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsPaginationResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;

import java.util.Date;

public interface JudgeStatisticsService {

    /**
     * get judge statistics
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
    JudgeStatisticsPaginationResponse getStatistics(String sortBy, String order, Long fieldId, Long deviceId, Long userCategory, String userName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage);

    JudgeStatisticsPaginationResponse getChartStatistics(Long fieldId, Long deviceId, Long userCategory, String userName,
                                              Date startTime, Date endTime, String statWidth);

    JudgeStatisticsResponseModel getTotalStatistics(Long fieldId, Long deviceId, Long userCategory, String userName,
                                                    Date startTime, Date endTime, String statWidth);

}
