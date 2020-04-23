/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（TotalStatistics）
 * 文件名：	TotalStatistics.java
 * 描述：	statistics model for preview
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalStatistics {

    long id; //user or device id
    String time; //time
    String name; //user or device name

    ScanStatistics scanStatistics; //simplified scan statistics
    JudgeStatisticsModelForPreview judgeStatistics; //simplified judge statistics
    HandExaminationStatisticsForPreview handExaminationStatistics; //simplified hand statistics

}
