package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

/**
 * SysDeviceStatusModel
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "SysDeviceStatusModel", description = "设备状态信息")
public class SysDeviceStatusModel {

    @ApiModelProperty(value = "设备 GUID")
    String guid;

    @ApiModelProperty("用户")
    String loginName;

    public int checkValid() {
        if (StringUtils.isBlank(guid) || StringUtils.isBlank(loginName)) {
            return 1;
        }
        return 0;
    }

}
