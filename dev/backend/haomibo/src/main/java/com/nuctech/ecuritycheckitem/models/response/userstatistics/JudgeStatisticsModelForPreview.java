/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（Simplified JudgeStatistics Model）
 * 文件名：	JudgeStatisticsModelForPreview.java
 * 描述：	Simplified JudgeStatisticsModelForPreview Model
 * 作者名：	Tiny
 * 日期：	2019/11/15
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Judge Statistics Body
 */
@Getter
@Setter
@ToString
public class JudgeStatisticsModelForPreview {

    String axisLabel; //axisLabel
    long id; //id
    long time; //time
    double workingSeconds; //workingSeconds
    long totalJudge; //totalJudge count
    long noSuspictionJudge; //noSuspiction Judge count
    double noSuspictionJudgeRate; //noSuspiction Judge Rate (%)
    long suspictionJudge; //suspictionJudge count
    double suspictionJudgeRate; //suspictionJudge Rate (%)

}
