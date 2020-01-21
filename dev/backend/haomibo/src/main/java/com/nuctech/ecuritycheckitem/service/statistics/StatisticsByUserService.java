/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（StatisticsByUserService）
 * 文件名：	StatisticsByUserService.java
 * 描述：	StatisticsByUserService interface
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */


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
    TotalStatisticsResponse getStatistics(String sortBy, String order, String modeId, String userName, Date startTime, Date endTime,  Integer currentPage, Integer perPage);

}
