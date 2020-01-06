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
public enum DeviceCurrentFlowStatusType {

    WAIT_TO_START("0"),
    PREPARING("1"),
    EXECUTING("2"),
    WARN_CONTINUING("3"),
    OK_FINISHED("4"),
    OK_READY("4"),
    WARN_FINISHED("5"),
    EXPT_STOPPED("6"),
    CRASH_STOPPED("7"),
    CANCELLED("8"),
    PAUSED("9");

    private final String value;

}
