package com.nuctech.ecuritycheckitem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * GenderType
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum GenderType {

    MALE("1000000001"),
    FEMALE("1000000002"),
    MALE_AND_FEMALE("1000000003");

    private final String value;
    
}
