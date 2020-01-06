package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 日志信息
 *
 * @author benja
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "SerDevLogModel", description = "设备向后台服务发送日志信息")
public class SerDevLogModel {

    @NotBlank
    @ApiModelProperty(value = "设备 GUID")
    private String guid;

    @ApiModelProperty(value = "操作员账号")
    private String loginName;

    @NotBlank
    @ApiModelProperty(value = "类别", example = "0")
    private Integer category;

    @ApiModelProperty(value = "日志级别", example = "0")
    private Integer level;

    @ApiModelProperty(value = "日志内容", notes = "日志内容")
    private String content;

    @NotBlank
    @ApiModelProperty(value = "时间", notes = "时间")
    private String time;

}
