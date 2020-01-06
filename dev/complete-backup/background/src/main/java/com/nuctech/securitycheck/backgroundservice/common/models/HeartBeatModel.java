package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 心跳信息
 *
 * @author CheGuangXing
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "HeartBeatModel", description = "心跳信息")
public class HeartBeatModel {

    @NotBlank
    @ApiModelProperty(value = "Guid")
    private String guid;

    @NotBlank
    @ApiModelProperty(value = "发出心跳时间")
    private String heartbeatTime;
    
}
