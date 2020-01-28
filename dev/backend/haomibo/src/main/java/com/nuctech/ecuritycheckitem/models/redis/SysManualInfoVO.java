package com.nuctech.ecuritycheckitem.models.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SysManualInfo
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-12-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysManualInfoVO {

    private String guid;

    private Long deviceId;

    private String deviceName;

    private String deviceSerial;

    private String currentStatus;

    private String workStatus;

    private String deviceCheckerGender;

    private SysDeviceConfigInfoVO configInfo;

    private Long logInedUserId;

}
