package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DeviceImageJudgeType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum DeviceImageJudgeType {

    MISJUDGE("0"),
    LEAKJUDGE("1");

    private final String value;

}
