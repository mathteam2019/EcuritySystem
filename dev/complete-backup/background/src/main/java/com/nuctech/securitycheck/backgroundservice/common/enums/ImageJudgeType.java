package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ImageJudgeType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum ImageJudgeType {

    MISJUDGE("1000001802"),
    LEAKJUDGE("1000001801");

    private final String value;
    
}
