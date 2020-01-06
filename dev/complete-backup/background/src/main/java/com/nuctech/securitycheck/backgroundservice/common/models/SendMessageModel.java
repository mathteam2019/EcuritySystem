package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(value = "SendMessageModel", description = "发送消息到设备")
public class SendMessageModel {

    @NotBlank
    @ApiModelProperty(value = "安检仪GUID")
    private String guid;

    @ApiModelProperty(value = "图片 guid")
    private String imageGuid;

    @NotBlank
    @ApiModelProperty(value = "判图结果")
    private int result;

    @ApiModelProperty(value = "运行模式")
    private int mode;
}
