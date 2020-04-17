package com.nuctech.ecuritycheckitem.models.redis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerDeviceStatusModel {

    @ApiModelProperty(value = "安检仪 GUID")
    private String guid;

    @ApiModelProperty(value = "flowName")
    private String flowName;

    @ApiModelProperty(value = "flowStatus")
    private String flowStatus;

    @ApiModelProperty(name = "diskSpace")
    private String diskSpace;



}
