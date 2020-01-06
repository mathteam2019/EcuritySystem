package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 后台服务向安检仪推送判图结论-请求报文定义
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "SerDevJudgeGraphResultModel", description = "后台服务向安检仪推送判图结论-请求报文定义")
public class SerDevJudgeGraphResultModel {

    @NotBlank
    @ApiModelProperty(value = "安检仪 GUID")
    private String guid;

    @NotBlank
    @ApiModelProperty(value = "判图站用户")
    private String userName;

    @NotBlank
    @ApiModelProperty(value = "图片 guid")
    private String imageGuid;

    @NotBlank
    @ApiModelProperty(value = "判图结果")
    private String result;

    @NotBlank
    @ApiModelProperty(value = "是否超时")
    private String isTimeout;

    @NotBlank
    @ApiModelProperty(value = "提交的嫌疑框信息")
    private List<SubmitRectInfoModel> submitRects;

}
