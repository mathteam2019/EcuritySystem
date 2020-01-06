package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TaskStatus
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum TaskStatusType {

    ALL("1000001101"),
    ASSIGN("1000001102"),
    JUDGE("1000001103"),
    HAND("1000001104"),
    SECURITY("1000001106");

    private final String value;
    
}
