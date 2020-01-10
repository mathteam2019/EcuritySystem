package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import sun.reflect.annotation.ExceptionProxy;

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

    @ApiModelProperty(value = "安检仪 GUID")
    private String guid;

    @ApiModelProperty(value = "PLC")
    private String PLC;

    @ApiModelProperty(value = "主采集卡")
    private String CaptureCardMainStatus;

    @ApiModelProperty(value = "从采集卡")
    private String CaptureCardSecondStatus;

    @ApiModelProperty(value = "伺服")
    private String ServoStatus;

    @ApiModelProperty(value = "急停")
    private String EmergencyStop;

    @ApiModelProperty(value = "脚部检测")
    private String FootAlarmOnLine;

    private boolean checkValidValue(String str) {
        try {
            int val = Integer.parseInt(str);
            if(val > -2 && val < 2) {
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public int checkValid() {
        if(StringUtils.isBlank(guid) || StringUtils.isBlank(PLC) || StringUtils.isBlank(CaptureCardMainStatus) || StringUtils.isBlank(CaptureCardSecondStatus)
                || StringUtils.isBlank(ServoStatus) || StringUtils.isBlank(EmergencyStop) || StringUtils.isBlank(FootAlarmOnLine)) {
            return 1;
        }

        if(!checkValidValue(PLC) || !checkValidValue(CaptureCardMainStatus) || !checkValidValue(CaptureCardSecondStatus)
                || !checkValidValue(ServoStatus) || !checkValidValue(EmergencyStop) || !checkValidValue(FootAlarmOnLine)) {
            return 2;
        }
        return 0;
    }



}
