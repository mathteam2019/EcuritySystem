package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
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

    @NotBlank
    @ApiModelProperty(value = "设备 GUID")
    String guid;

    @NotBlank
    @ApiModelProperty("用户")
    String loginName;

}
