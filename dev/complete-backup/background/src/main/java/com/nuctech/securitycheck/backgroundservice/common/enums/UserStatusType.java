package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * UserStatusType
 *
 * @author Chui
 * @version v1.0
 * @since 2020-032-02
 */
@AllArgsConstructor
@Getter
public enum UserStatusType {
    ACTIVE("1000001701"),
    INACTIVE("1000001702"),
    PENDING("1000000304"),
    BLOCKED("1000000303");
    private final String value;
}