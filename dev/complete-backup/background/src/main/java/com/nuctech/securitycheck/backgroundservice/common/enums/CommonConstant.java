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
    RESULT_EMPTY(502),
    RESULT_INVALID_PARAM_DATA(501),
    RESULT_INVALID_LOGIC_DATA(503),
    RESULT_INVALID_DEVICE(503),
    RESULT_INVALID_USER(504),
    RESULT_INVALID_PASSWORD(505),
    RESULT_BLOCK_USER(506),
    RESULT_INVALID_DEVICE_SECURITY(601),
    RESULT_ATR_RESULT(602),
    RESULT_ATR_SWITCH(603),
    RESULT_EXIST_IMAGE_GUID(604),
    EXPIRE_TIME(24 * 60 * 60),
    MAX_PROCESS(100),
    MAX_MANUAL_REDIS_LOCK(30),
    DEFAULT_JUDGE_PROCESSING_TIME(30),
    DEFAULT_JUDGE_ASSIGN_TIME(10),
    DEFAULT_SCHEDULER_ZABBIX_TIME(60),
    DEFAULT_SCHEDULER_HISTORY_TIME(0),
    PERMUTION_OVERTIME(60),
    LIMIT_OFFLINE_TIME(10);

    private CommonConstant(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    private final Integer value;

}
