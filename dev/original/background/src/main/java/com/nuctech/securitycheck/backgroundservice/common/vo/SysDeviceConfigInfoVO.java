package com.nuctech.securitycheck.backgroundservice.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SysDeviceConfigInfo
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-12-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysDeviceConfigInfoVO {

    private Long configId;

    private Long modeId;

    private String manualSwitch;

    private String manRemoteGender;

    private String womanRemoteGender;

    private String manManualGender;

    private String womanManualGender;

    private String manDeviceGender;

    private String womanDeviceGender;

}
