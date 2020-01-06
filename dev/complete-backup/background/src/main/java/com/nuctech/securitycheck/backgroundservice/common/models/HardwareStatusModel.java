package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 手检战向后台服务发送注销信息-请求报文定义
 *
 * @author 朱龙虎
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "HardwareStatusModel", description = "设备向后台服务发送 flow 信息。每次状态更新即发送。")
public class HardwareStatusModel {

    @NotBlank
    @ApiModelProperty(value = "安检仪 GUID")
    private String guid;

    @ApiModelProperty(value = "PLC")
    private String PLC;

    @ApiModelProperty(value = "主采集卡")
    private String captureCardMainStatus;

    @ApiModelProperty(value = "从采集卡")
    private String captureCardSecondStatus;

    @ApiModelProperty(value = "伺服")
    private String servoStatus;

    @ApiModelProperty(value = "急停")
    private String emergencyStop;

    @ApiModelProperty(value = "脚部检测")
    private String footAlarmOnLine;

}
