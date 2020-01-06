package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 设备配置信息
 *
 * @author CheGuangXing
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "SerDeviceConfigModel", description = "设备配置信息")
public class SerDeviceConfigModel {

    @ApiModelProperty(value = "安检仪 GUID")
    private String guid;

    @ApiModelProperty(value = "运行模式", example = "0")
    private Long mode;

    @ApiModelProperty(value = "设备编号")
    private String deviceNumber;

    @ApiModelProperty(value = "ATR 嫌疑框颜色")
    private String ATRColor;

    @ApiModelProperty(value = "手工嫌疑框颜色")
    private String ManualColor;

    @ApiModelProperty(value = "删除嫌疑框颜色")
    private String DeleteColor;

    @ApiModelProperty(value = "系统参数-安检仪 params")
    private List<SerScanParamModel> params;

}
