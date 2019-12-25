/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（CommonResponseBody）
 * 文件名：	CommonResponseBody.java
 * 描述：	this class is used for filtering and pagination result which is used in datatable.
 * 作者名：	Sandy
 * 日期：	2019/10/24
 */


package com.nuctech.ecuritycheckitem.models.reusables;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Builder;

/**
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder()
public class FilteringAndPaginationResult {

    @JsonProperty("total")
    long total;

    @JsonProperty("per_page")
    int perPage;

    @JsonProperty("current_page")
    int currentPage;

    @JsonProperty("last_page")
    int lastPage;

    @JsonProperty("from")
    int from;

    @JsonProperty("to")
    int to;

    Object data; // will hold list<>;

}
