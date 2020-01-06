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
public enum DefaultType {

    TRUE("1000000601"),
    FALSE("1000000602");

    private final String value;

}
