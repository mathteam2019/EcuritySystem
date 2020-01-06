package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TaskType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum TaskType {

    JUDGE("1000010001"),
    HAND("1000010002"),
    SECURITY("1000010003");

    private final String value;
    
}
