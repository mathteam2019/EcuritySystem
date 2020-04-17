package com.nuctech.ecuritycheckitem.models.redis;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手检战向后台服务发送注销信息-请求报文定义
 *
 * @author 朱龙虎
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HardwareStatusModel {

    @ApiModelProperty(value = "安检仪 GUID")
    private String guid;

    @ApiModelProperty(value = "PLC")
    private String PLC;

    @JsonProperty("PLC")
    public void setPLC(String PLC) {
        this.PLC = PLC;
    }

    @ApiModelProperty(value = "主采集卡")
    private String CaptureCardMainStatus;

    @JsonProperty("CaptureCardMainStatus")
    public void setCaptureCardMainStatus(String CaptureCardMainStatus) {
        this.CaptureCardMainStatus = CaptureCardMainStatus;
    }

    @ApiModelProperty(value = "从采集卡")
    private String CaptureCardSecondStatus;

    @JsonProperty("CaptureCardSecondStatus")
    public void setCaptureCardSecondStatus(String CaptureCardSecondStatus) {
        this.CaptureCardSecondStatus = CaptureCardSecondStatus;
    }

    @ApiModelProperty(value = "伺服")
    private String ServoStatus;

    @JsonProperty("ServoStatus")
    public void setServoStatus(String ServoStatus) {
        this.ServoStatus = ServoStatus;
    }

    @ApiModelProperty(value = "急停")
    private String EmergencyStop;

    @JsonProperty("EmergencyStop")
    public void setEmergencyStop(String EmergencyStop) {
        this.EmergencyStop = EmergencyStop;
    }

    @ApiModelProperty(value = "脚部检测")
    private String FootAlarmOnLine;

    @JsonProperty("FootAlarmOnLine")
    public void setFootAlarmOnLine(String FootAlarmOnLine) {
        this.FootAlarmOnLine = FootAlarmOnLine;
    }





}
