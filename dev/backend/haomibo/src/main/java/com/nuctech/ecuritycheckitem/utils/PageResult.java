package com.nuctech.ecuritycheckitem.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResult<T> {
    private long total;
    private List<T> dataList;
    public PageResult(long total, List<T> dataList) {
        this.total = total;
        this.dataList = dataList;
    }
}
