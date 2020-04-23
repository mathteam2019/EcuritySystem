/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（EvaluateJudgeStatisticsPaginationResponse）
 * 文件名：	EvaluateJudgeStatisticsPaginationResponse.java
 * 描述：	EvaluateJudge Statistics Pagination Model
 * 作者名：	Tiny
 * 日期：	2019/11/15
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.TreeMap;

@Getter
@Setter
public class EvaluateJudgeStatisticsPaginationResponse {

    long total; //total count
    long per_page; //record count per page
    long current_page; //current page number
    long last_page; //last page number
    long from; //start index of current page
    long to; //last index of current page

    List<EvaluateJudgeResponseModel> detailedStatistics; //detailed evaluate judge statistics

}
