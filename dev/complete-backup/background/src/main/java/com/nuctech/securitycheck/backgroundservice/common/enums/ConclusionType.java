package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DefaultType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum ConclusionType {

    SYSTEM("1000002301"),
    MANUAL("1000002302"),
    HAND("1000002303");

    private final String value;

}
