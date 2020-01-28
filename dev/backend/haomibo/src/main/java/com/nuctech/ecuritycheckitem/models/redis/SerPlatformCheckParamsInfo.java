/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerPlatformCheckParams）
 * 文件名：	SerPlatformCheckParams.java
 * 描述：	SerPlatformCheckParams Model
 * 作者名：	Choe
 * 日期：	2019/11/26
 */


package com.nuctech.ecuritycheckitem.models.redis;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerPlatformCheckParamsInfo  {

    private Long scanId;

    private String scanRecogniseColour;

    private Long scanOverTime;

    private Long judgeAssignTime;

    private Long judgeProcessingTime;

    private Long judgeScanOvertime;

    private String judgeRecogniseColour;

    private Long handOverTime;

    private String handRecogniseColour;

    private String historyDataStorage;

    private String displayDataExport;

    private String displayDeleteSuspicion;

    private String displayDeleteSuspicionColour;


}
