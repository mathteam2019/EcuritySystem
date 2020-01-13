package com.nuctech.securitycheck.backgroundservice.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 后台服务向远程短下发配置信息-请求报文定义
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "SerDevRemAssignInfoModel", description = "后台服务向远程短下发配置信息-请求报文定义")
public class SerDevRemAssignInfoModel {

    @NotBlank
    @ApiModelProperty(value = "判图站 GUID")
    private String guid;

    @ApiModelProperty(value = "判图站参数 list")
    private List<Object> params;

    @ApiModelProperty(value = "设备编号")
    private String deviceNumber;

    @ApiModelProperty(value = "ATR 嫌疑框颜色")
    private String ATRColor;

    @ApiModelProperty(value = "手工嫌疑框颜色")
    private String ManualColor;

    @ApiModelProperty(value = "删除嫌疑框颜色")
    private String DeleteColor;

    @JsonProperty("ATRColor")
    public void setATRColor(String ATRColor) {
        this.ATRColor = ATRColor;
    }

    @JsonProperty("ManualColor")
    public void setManualColor(String ManualColor) {
        this.ManualColor = ManualColor;
    }

    @JsonProperty("DeleteColor")
    public void setDeleteColor(String DeleteColor) {
        this.DeleteColor = DeleteColor;
    }
    
}
