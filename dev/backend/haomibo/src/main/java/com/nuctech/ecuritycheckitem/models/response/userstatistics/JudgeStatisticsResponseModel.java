/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（JudgeStatisticsResponseModel）
 * 文件名：	JudgeStatisticsResponseModel.java
 * 描述：	JudgeStatistics model
 * 作者名：	Tiny
 * 日期：	2019/12/04
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class JudgeStatisticsResponseModel {

    String time; //time
    long artificialJudge; //artificial Judge count
    long assignTimeout; //assign Timeout
    long judgeTimeout; //judge Timeout
    long atrResult; //atrResult
    long suspiction; //suspiction
    long noSuspiction; //noSuspiction
    double avgDuration; //avg Duration
    long maxDuration; //max Duration
    long minDuration; //min Duration
    long total; //total count
    long artificialResult; //artificial Result count
    double artificialResultRate; //artificial Result Rate
    double assignTimeoutResultRate; //assign Timeout Result Rate
    double judgeTimeoutResultRate; //judge Timeout Result Rate
    long scanResult; //scan Result
    double atrResultRate;
    double scanResultRate; //scanResult Rate
    double noSuspictionRate; //noSuspiction Rate
    double suspictionRate; //suspiction Rate
    long limitedArtificialDuration; //limited Artificial Duration
    double avgArtificialJudgeDuration; //avg Artificial Judge Duration
    long maxArtificialJudgeDuration; //max Artificial Judge Duration
    long minArtificialJudgeDuration; //min Artificial Judge Duration

    Map<String, Integer> handGoods; //suspiction hand goods

}

