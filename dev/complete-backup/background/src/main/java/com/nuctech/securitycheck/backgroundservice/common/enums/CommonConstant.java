package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.Getter;

/**
 * DefaultType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */

@Getter
public enum CommonConstant {

    RESULT_SUCCESS(1),
    RESULT_FAIL(0),
    EXPIRE_TIME(8 * 60 * 60),
    MAX_PROCESS(10),
    MAX_MANUAL_REDIS_LOCK(60),
    DEFAULT_JUDGE_PROCESSING_TIME(30),
    DEFAULT_JUDGE_ASSIGN_TIME(10);

    private CommonConstant(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private final int value;

}
