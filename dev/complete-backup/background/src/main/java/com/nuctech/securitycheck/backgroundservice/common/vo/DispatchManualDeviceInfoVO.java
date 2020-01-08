package com.nuctech.securitycheck.backgroundservice.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DispatchManualDeviceInfo
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-12-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchManualDeviceInfoVO {

    private String guid;

    private String imageGuid;

    private String recheckNumber;

    private Boolean localRecheck;

    private Long assignId;

}