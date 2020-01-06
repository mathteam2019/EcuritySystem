package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * SysDeviceVersionModel
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "SysDeviceVersionModel")
public class SysDeviceVersionModel {

    @NotBlank
    @ApiModelProperty(value = "安检仪GUID")
    private String guid;

    @ApiModelProperty(value = "软件版本号")
    private String softwareVersion;

    @ApiModelProperty(value = "算法版本号")
    private String algorithmVersion;

}
