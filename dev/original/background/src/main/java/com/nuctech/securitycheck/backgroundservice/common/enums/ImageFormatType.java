package com.nuctech.securitycheck.backgroundservice.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ImageFormat
 *
 * @author benja
 * @version v1.0
 * @since 2019-12-01
 */
@AllArgsConstructor
@Getter
public enum ImageFormatType {

    IMAGE("1000002201"),
    CARTOON("1000002202");

    private final String value;
    
}
