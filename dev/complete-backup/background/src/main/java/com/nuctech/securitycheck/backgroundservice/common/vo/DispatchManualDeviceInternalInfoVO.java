package com.nuctech.securitycheck.backgroundservice.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DispatchManualDeviceInternalInfo
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-12-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchManualDeviceInternalInfoVO {

    private String guid;

    private String imageGuid;

    private String recheckNumber;

    private Boolean localRecheck;

    private Long assignId;

}
