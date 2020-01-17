package com.nuctech.securitycheck.backgroundservice.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ImageFormat
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum JudgeResultType {

    TRUE("1000001202"),
    FALSE("1000001201");

    private final String value;
    
}
