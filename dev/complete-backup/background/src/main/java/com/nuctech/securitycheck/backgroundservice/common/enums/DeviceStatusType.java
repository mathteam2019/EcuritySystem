package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DeviceStatusType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum DeviceStatusType {

    REGISTER("1000002001"),
    UNREGISTER("1000002002"),
    LOGIN("1000002003"),
    LOGOUT("1000002004"),
    START("1000002005"),
    STOP("1000002006");

    private final String value;

}
