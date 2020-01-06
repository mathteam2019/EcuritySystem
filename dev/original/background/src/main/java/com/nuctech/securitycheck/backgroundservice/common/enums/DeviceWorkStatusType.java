package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DeviceWorkStatusType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum DeviceWorkStatusType {

    BUSY("1000002101"),
    FREE("1000002102");

    private final String value;
    
}
