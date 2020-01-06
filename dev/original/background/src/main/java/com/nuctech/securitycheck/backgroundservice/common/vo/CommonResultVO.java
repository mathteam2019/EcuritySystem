package com.nuctech.securitycheck.backgroundservice.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CommonResult
 *
 * @author benja
 * @version v1.0
 * @since 2019-11-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResultVO {

    private String guid;

    private Integer result;

}
