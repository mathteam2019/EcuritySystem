/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（HandExaminationResponseModel）
 * 文件名：	HandExaminationResponseModel.java
 * 描述：	HandExamination Statistics Model
 * 作者名：	Tiny
 * 日期：	2019/11/15
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

/**
 * HandExamination Statistics Model
 */
@Getter
@Setter
public class HandExaminationResponseModel {

    int time; //time
    long total; //total hand count
    long seizure; //seizure  hand count
    long noSeizure; //noSeizure hand count
    long totalJudge; //totalJudge hand count
    long missingReport; //missingReport hand count
    long mistakeReport; //mistakeReport hand count
    long artificialJudge; //artificialJudge hand count
    long artificialJudgeMissing; //artificialJudgeMissing hand count
    long artificialJudgeMistake; //artificialJudgeMistake hand count
    long intelligenceJudge; //intelligenceJudge hand count
    long intelligenceJudgeMissing; //intelligenceJudgeMissing hand count
    long intelligenceJudgeMistake; //intelligenceJudgeMistake hand count

    double missingReportRate; // missing Report Rate (%)
    double mistakeReportRate; // mistake Report Rate (%)
    double artificialJudgeMissingRate; // artificial Judge Missing Rate (%)
    double artificialJudgeMistakeRate; // artificial Judge Mistake Rate (%)
    double intelligenceJudgeMissingRate; // intelligence Judge Missing Rate (%)
    double intelligenceJudgeMistakeRate; // intelligence Judge Mistake Rate (%)

    double seizureRate; // seizure hand rate
    double noSeizureRate; //no seizure hand rate

    double maxDuration; //max hand examination duration
    double minDuration; //min hand examination duration
    double avgDuration; //avg hand examination duration

}
