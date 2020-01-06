package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 心跳信息
 *
 * @author CheGuangXing
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "HeartBeatReplyModel", description = "心跳返回信息")
public class HeartBeatReplyModel {

    @ApiModelProperty(value = "Guid")
    private String guid;

    @ApiModelProperty(value = "结果", example = "0")
    private Integer result;

    @ApiModelProperty(value = "发出心跳时间")
    private String heartbeatTime;

}
