package com.nuctech.securitycheck.backgroundservice.common.models;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典信息
 *
 * @author Choe
 * @version v1.0
 * @since 2020-01-13
 */
@Data
@ApiModel(value = "KeyPointModel", description = "关键点信息")
public class KeyPointModel {

    @ApiModelProperty(value = "", example="true")
    private Boolean isAnyone;

    @ApiModelProperty(value = "", example="true")
    private Boolean isPostureCorrect;

    @ApiModelProperty(value = "高度", example="0")
    @JsonProperty("nHeight")
    private Integer nHeight;

    @ApiModelProperty(value = "宽度", example="0")
    @JsonProperty("nWidth")
    private Integer nWidth;

    @ApiModelProperty(value = "", example="")
    private String keypointsBack;

    @ApiModelProperty(value = "", example="")
    private String keypointsFront;

}
