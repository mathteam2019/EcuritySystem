package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典信息
 *
 * @author Choe
 * @version v1.0
 * @since 2020-01-14
 */
@Data
@ApiModel(value = "SeizedGoodModel", description = "物品管理")
public class SeizedGoodModel {


    @ApiModelProperty(value = "查获物品值")
    private String goodName;

    @ApiModelProperty(value = "查获物品编码")
    private Long goodCode;

}
