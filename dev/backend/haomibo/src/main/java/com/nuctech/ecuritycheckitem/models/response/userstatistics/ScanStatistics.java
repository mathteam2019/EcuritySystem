/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ScanStatistics）
 * 文件名：	ScanStatistics.java
 * 描述：	ScanStatistics model
 * 作者名：	Tiny
 * 日期：	2019/12/04
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Statistics Response Body
 */
@Getter
@Setter
@ToString
public class ScanStatistics {

    String axisLabel; //axisLabel
    long id; //id
    long time; //time
    double workingSeconds; //workingSeconds
    long totalScan; //totalScan count
    long invalidScan; //invalidScan count
    double invalidScanRate; //invalidScan Rate (%)
    long validScan; //validScan count
    double validScanRate; //validScan Rate (%)
    long passedScan; //passed Scan
    double passedScanRate; //passedScan Rate(%)
    long alarmScan; //alarmScan count
    double alarmScanRate; //alarmScan Rate (%)

}