/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（TotalStatisticsResponse）
 * 文件名：	TotalStatisticsResponse.java
 * 描述：	Paginated TotalStatistics Response
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.TreeMap;

/**
 * Total Statistics Response Body
 */
@Getter
@Setter
public class TotalTimeStatisticsResponse {

    TotalTimeStatistics totalStatistics;
    TreeMap<Long, TotalTimeStatistics> detailedStatistics; //detailed statistics

    long total; //total count
    long per_page; //record count per page
    long current_page; //current page number
    long last_page; //last page number
    long from; //start index of current page
    long to; //end index of current page
}
