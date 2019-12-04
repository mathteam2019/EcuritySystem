package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

@Getter
@Setter
public class SuspicionHandGoodsPaginationResponse {

    long total;
    long per_page;
    long current_page;
    long last_page;
    long from;
    long to;

    TreeMap<String, Long> totalStatistics;
    TreeMap<Integer, TreeMap<String, Long>> detailedStatistics;

}
