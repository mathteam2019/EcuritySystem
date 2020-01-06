package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 主要为手检站产生的数据
 *
 * @author ChuiGuangCheng
 * @version v1.0
 * @since 2019-12-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(value = "SecurityRemoteModel", description = "主要为手检站产生的数据")
public class SecurityRemoteModel {

    @ApiModelProperty(value = "GUID")
    private String guid;

    @ApiModelProperty(value = "远程控制端GUID")
    private String remoteGuid;

    @ApiModelProperty(value = "控制信息（Json字符串）")
    private String command;

}
