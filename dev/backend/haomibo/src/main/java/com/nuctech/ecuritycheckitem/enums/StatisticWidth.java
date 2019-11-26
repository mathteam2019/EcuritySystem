package com.nuctech.ecuritycheckitem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatisticWidth {

    HOUR("hour"),
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    QUARTER("quarter"),
    YEAR("year");

    private final String value;

}
