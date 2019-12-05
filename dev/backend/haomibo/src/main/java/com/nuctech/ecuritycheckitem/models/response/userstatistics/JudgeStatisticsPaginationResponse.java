package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Tiny 2019/12/04
 *
 * Model to send judge graph statistics
 *
 */
@Getter
@Setter
public class JudgeStatisticsPaginationResponse {

    long total;
    long per_page;
    long current_page;
    long last_page;
    long from;
    long to;

    JudgeStatisticsResponseModel totalStatistics;
    TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics;

}

