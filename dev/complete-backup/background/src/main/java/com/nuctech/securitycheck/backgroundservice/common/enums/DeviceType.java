package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DeviceType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum DeviceType {

    SECURITY("1000001901"),
    MANUAL("1000001903"),
    JUDGE("1000001902");

    private final String value;
    
}
