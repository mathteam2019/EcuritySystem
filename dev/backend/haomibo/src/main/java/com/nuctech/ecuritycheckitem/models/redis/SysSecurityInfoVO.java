package com.nuctech.ecuritycheckitem.models.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SysSecurityInfo
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-12-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysSecurityInfoVO {

    private String guid;

    private Long deviceId;

    private String deviceName;

    private String deviceSerial;

    private String currentStatus;

    private String workStatus;

    private List<SysManualInfoVO> manualDeviceModelList;

    private List<SysJudgeInfoVO> judgeDeviceModelList;

    private SysDeviceConfigInfoVO configInfo;

    private String workMode;

    private Long logInedUserId;

}
