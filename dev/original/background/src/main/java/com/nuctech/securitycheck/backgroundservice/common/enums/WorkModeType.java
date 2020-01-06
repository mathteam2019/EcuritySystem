package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * WorkModeType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum WorkModeType {

    SECURITY("1000001301"),
    SECURITY_MANUAL("1000001302"),
    SECURITY_JUDGE("1000001303"),
    SECURITY_JUDGE_MANUAL("1000001304");

    private final String value;
    
}
