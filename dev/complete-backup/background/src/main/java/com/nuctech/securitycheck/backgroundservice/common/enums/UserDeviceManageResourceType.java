package com.nuctech.securitycheck.backgroundservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DeviceStatusType
 *
 * @author CheGuangXing
 * @version v1.0
 * @since 2019-12-09
 */
@AllArgsConstructor
@Getter
public enum UserDeviceManageResourceType {

    DEVICE("180"),    //安检仪管理员
    MANUAL("182"),     //判图站管理员
    JUDGE("181");     //手检站管理员

    private final String value;

}
