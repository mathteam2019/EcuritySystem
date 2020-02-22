/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysAccessLog）
 * 文件名：	SysAccessLog.java
 * 描述：	SysAccessLog Model
 * 作者名：	Choe
 * 日期：	2019/11/21
 */


package com.nuctech.ecuritycheckitem.models.es;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_ACCESS_LOG)
@Document(indexName = "haomibo_access_log", type = "accesslog", shards = 1, replicas = 0)
public class EsSysAccessLog implements Serializable {


    @org.springframework.data.annotation.Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("operate_time")
    private Date operateTime;

    @JsonProperty("client_ip")
    private String clientIp;

    @JsonProperty("operate_id")
    private Long operateId;

    @JsonProperty("operate_account")
    private String operateAccount;

    @JsonProperty("action")
    private String action;

    @JsonProperty("operate_result")
    private String operateResult;

    @JsonProperty("reason_code")
    private String reasonCode;

    @JsonProperty("online_time")
    private Long onlineTime;

    @JsonProperty("createdby")
    private Long createdBy;

    @JsonProperty("editedby")
    private Long editBy;
    
}
