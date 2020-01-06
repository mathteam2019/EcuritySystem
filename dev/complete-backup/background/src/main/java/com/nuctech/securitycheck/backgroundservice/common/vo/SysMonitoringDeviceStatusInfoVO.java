package com.nuctech.securitycheck.backgroundservice.common.vo;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SysMonitoringDeviceStatusInfo
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-12-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysMonitoringDeviceStatusInfoVO {

    private String guid;

    private Long deviceId;

    private String deviceType;

    private String status;

    private String currentStatus;

    private String workStatus;

    private Date loginTime;

    private SysUser loginUser;

}
