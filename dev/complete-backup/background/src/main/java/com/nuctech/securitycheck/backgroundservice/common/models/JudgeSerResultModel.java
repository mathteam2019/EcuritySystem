package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 判图站向后台服务提交判图结论-请求报文定义
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "JudgeSerResultModel", description = "判图站向后台服务提交判图结论-请求报文定义")
public class JudgeSerResultModel {

    @NotBlank
    @ApiModelProperty(value = "判图站 GUID")
    private String guid;

    @ApiModelProperty(value = "判图结论数据")
    private ImageResultModel imageResult;

//    @NotBlank
//    @ApiModelProperty(value = "判图站用户")
//    private String loginName;
//
//    @NotBlank
//    @ApiModelProperty(value = "图片 guid")
//    private String imageGuid;
//
//    @NotBlank
//    @ApiModelProperty(value = "判图结果")
//    private String result;
//
//    @NotBlank
//    @ApiModelProperty(value = "判图时间")
//    private String time;
//
//    @NotBlank
//    @ApiModelProperty(value = "提交的嫌疑框信息")
//    private List<SubmitRectInfoModel> submitRects;
    
}
