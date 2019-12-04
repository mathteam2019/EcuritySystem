package com.nuctech.ecuritycheckitem.models.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Judge statistics Response Body
 */
@Getter
@Setter
@ToString
public class JudgeStatisticsResponseModel {

    int time; //时间段
    long artificialJudge; //人工判图
    long assignTimeout; //分派超时
    long judgeTimeout; //判图超时
    long atrResult; //ATR
    long suspiction; //嫌疑
    long noSuspiction; //无嫌疑
    double avgDuration; //平均时长
    double maxDuration; //最高时长
    double minDuration; //最低时长
    long total; //判图总量
    long artificialResult; //"人工结论量	"
    double artificialResultRate; //人工结论率
    double assignTimeoutResultRate; //分派超时结论率
    double judgeTimeoutResultRate; //"判图超时结论率	"
    long scanResult; //扫描结论量
    double scanResultRate; //扫描结论量
    double noSuspictionRate; //无嫌疑率
    double suspictionRate; //嫌疑率
    double limitedArtificialDuration;  //人工判图时长阈值
    double avgArtificialJudgeDuration; //人工判图平均时长
    double maxArtificialJudgeDuration; //人工判图最高时长
    double minArtificialJudgeDuration; //人工判图最低时长

}
