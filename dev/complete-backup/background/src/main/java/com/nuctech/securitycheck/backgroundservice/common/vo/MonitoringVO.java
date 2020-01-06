package com.nuctech.securitycheck.backgroundservice.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MonitoringVO
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-12-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringVO {

    String msgContent;

    List<SysMonitoringDeviceStatusInfoVO> deviceListToRest;
    
}
