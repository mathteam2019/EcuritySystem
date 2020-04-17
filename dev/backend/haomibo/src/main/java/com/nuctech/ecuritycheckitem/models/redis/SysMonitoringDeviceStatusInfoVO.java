package com.nuctech.ecuritycheckitem.models.redis;

import com.nuctech.ecuritycheckitem.models.db.SysUser;
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

    private Integer deviceOnline;

    private String workStatus;

    private Date loginTime;

    private Date deviceLoginTime;

    private Date heartBeatTime;

    private String ipAddress;

    private SysUser loginUser;

}
