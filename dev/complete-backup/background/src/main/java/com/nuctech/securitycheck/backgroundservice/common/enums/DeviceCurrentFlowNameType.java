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
public enum DeviceCurrentFlowNameType {

    LIFE_ZERO("0"),
    LIFE_LOAD("1"),
    LIFE_LOGIN_WAIT("2"),
    DEVICE_INIT("3"),
    SCAN("4"),
    RESCAN("5"),
    AIR_CALIBRATE("6"),
    DEVICE_RESET("7"),
    LIFE_LOGOUT("8"),
    LIFE_EXIT("9"),
    LIFE_DEAD("10");

    private final String value;

}
