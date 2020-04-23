/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（TotalStatistics）
 * 文件名：	TotalTimeStatistics.java
 * 描述：	statistics model for preview
 * 作者名：	Tiny
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailTimeStatistics {

    String time;
    long workingTime;
    String userName;
    String deviceType;

}
