package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ApiModel(value = "SerDeviceStatusModel", description = "设备向后台服务发送 flow 信息。每次状态更新即发送。")
public class SerDeviceStatusModel {

    @NotBlank
    @ApiModelProperty(value = "安检仪 GUID")
    private String guid;

    @NotBlank
    @ApiModelProperty(value = "flowName")
    private String flowName;

    @NotBlank
    @ApiModelProperty(value = "flowStatus")
    private String flowStatus;

    @NotBlank
    @ApiModelProperty(name = "diskSpace")
    private String diskSpace;

}
