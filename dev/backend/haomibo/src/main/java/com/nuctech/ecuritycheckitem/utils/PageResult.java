/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PageResult<T>)
 * 文件名：	PageResult.java
 * 描述：	Paginated Result Template
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResult<T> {

    private long total; //total
    private List<T> dataList; //data list

    public PageResult(long total, List<T> dataList) {
        this.total = total;
        this.dataList = dataList;
    }
}
