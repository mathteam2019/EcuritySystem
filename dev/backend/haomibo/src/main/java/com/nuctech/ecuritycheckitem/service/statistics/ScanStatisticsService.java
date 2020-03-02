/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ScanStatisticsService）
 * 文件名：	ScanStatisticsService.java
 * 描述：	ScanStatisticsService interface
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

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
    ScanStatisticsResponse getStatistics(String sortBy, String order, Long fieldId, Long deviceId, Long userCategory, String userName, String workMode,
                                         Date startTime, Date endTime, String statWidth, Integer currentPage, Integer perPage);

}
