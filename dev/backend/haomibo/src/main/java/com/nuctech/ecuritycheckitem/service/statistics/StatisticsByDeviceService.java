/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（StatisticsByDeviceService）
 * 文件名：	StatisticsByDeviceService.java
 * 描述：	StatisticsByDeviceService interface
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.statistics;

import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatisticsResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalTimeStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalTimeStatisticsResponse;

import java.util.Date;

public interface StatisticsByDeviceService {

    /**
     * get statistics by device
     * @param deviceType
     * @param deviceName
     * @param startTime
     * @param endTime
     * @param currentPage
     * @param perPage
     * @return
     */
    TotalTimeStatisticsResponse getStatistics(String sortBy, String order, String deviceType, String deviceName, Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage);

    TotalTimeStatistics getChartStatistics(String deviceType, String deviceName, Date startTime, Date endTime, String statWidth);

}
