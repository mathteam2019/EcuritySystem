package com.nuctech.securitycheck.backgroundservice.common.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 主要为判图站产生的数据
 *
 * @author ChuiGuangCheng
 * @version v1.0
 * @since 2019-12-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(value = "ImageResultModel", description = "主要为判图站产生的数据")
public class ImageResultModel {

    @NotBlank
    @ApiModelProperty(value = "图片 guid")
    private String imageGuid;

    @NotBlank
    @ApiModelProperty(value = "判图站用户")
    private String userName;

    @NotBlank
    @ApiModelProperty(value = "判图结果")
    private String result;

    @NotBlank
    @ApiModelProperty(value = "提交结论时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    @NotBlank
    @ApiModelProperty(value = "是否超时")
    private String isTimeout;

    @NotBlank
    @ApiModelProperty(value = "提交的嫌疑框信息")
    private List<SubmitRectInfoModel> submitRects;
}
