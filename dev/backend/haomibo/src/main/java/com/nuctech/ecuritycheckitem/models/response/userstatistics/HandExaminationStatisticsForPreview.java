/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（HandExaminationStatisticsForPreview）
 * 文件名：	HandExaminationStatisticsForPreview.java
 * 描述：	Simplified HandExamination Statistics Model
 * 作者名：	Tiny
 * 日期：	2019/11/15
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HandExaminationStatisticsForPreview {

    String axisLabel; // axisLabel
    long id; //key value of hand examination
    long time; // time
    double workingSeconds; // working Seconds
    long totalHandExamination; // total HandExamination count
    long seizureHandExamination; // seizure HandExamination count
    double seizureHandExaminationRate; // seizure HandExamination Rate (%)
    long noSeizureHandExamination; // noSeizure HandExamination count
    double noSeizureHandExaminationRate; // noSeizure HandExamination Rate (%)

    long checkDuration; //check duration

}