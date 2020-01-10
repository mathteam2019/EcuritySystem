/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerPlatformCheckParamsSimplifiedForTaskManagement）
 * 文件名：	SerPlatformCheckParamsSimplifiedForTaskManagement.java
 * 描述：	Simplified SerPlatFormCheckParams Model for TaskManagement
 * 作者名：	Tiny
 * 日期：	2019/12/27
 */


package com.nuctech.ecuritycheckitem.models.simplifieddb;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SerPlatformCheckParamsSimplifiedForTaskManagement {

    private String scanRecogniseColour;

    private String judgeRecogniseColour;

    private String handRecogniseColour;

    private String displayDeleteSuspicionColour;

    private String displayDeleteSuspicion;

}
