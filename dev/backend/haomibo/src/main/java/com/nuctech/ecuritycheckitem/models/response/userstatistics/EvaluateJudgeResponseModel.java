/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（EvaluateJudgeResponseModel）
 * 文件名：	EvaluateJudgeResponseModel.java
 * 描述：	EvaluateJudge Statistics Model
 * 作者名：	Tiny
 * 日期：	2019/11/15
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

/**
 * Model to process Evaluate Judge Statistics
 */
@Getter
@Setter
public class EvaluateJudgeResponseModel {

    int time; //time
    long total; //total judge count
    long seizure; //seizure count
    long noSeizure; //noSeizure count
    long totalJudge; //total Judge count
    long missingReport; //missing Report count
    long mistakeReport; //mistake Report count
    long artificialJudge; //artificialJudge Report count
    long artificialJudgeMissing; //artificial Judge Missing count
    long artificialJudgeMistake; //artificial Judge Mistake count
    long intelligenceJudge; //intelligence Judge count
    long intelligenceJudgeMissing; //intelligence Judge Missing count
    long intelligenceJudgeMistake; //intelligence Judge Mistake count
    double maxDuration; //maxDuration
    double minDuration; //minDuration
    double avgDuration; //avgDuration

    double missingReportRate; //missingReport Rate (%)
    double mistakeReportRate; //mistakeReport Rate (%)
    double artificialJudgeMissingRate; //artificial Judge Missing Rate  (%)
    double artificialJudgeMistakeRate; //artificial Judge Mistake Rate (%)
    double intelligenceJudgeMissingRate; //intelligence Judge Missing Rate (%)
    double intelligenceJudgeMistakeRate; //intelligence Judge Mistake Rate (%)


}
