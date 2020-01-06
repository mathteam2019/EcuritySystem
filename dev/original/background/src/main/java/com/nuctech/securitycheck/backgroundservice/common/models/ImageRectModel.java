package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 图像嫌疑框
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "ImageRect", description = "图像嫌疑框")
public class ImageRectModel {

    @NotBlank
    @ApiModelProperty(value = "左上角横坐标", example = "0")
    private Integer x;

    @NotBlank
    @ApiModelProperty(value = "左上角纵坐标", example = "0")
    private Integer y;

    @NotBlank
    @ApiModelProperty(value = "宽度", example = "0")
    private Integer width;

    @NotBlank
    @ApiModelProperty(value = "高度", example = "0")
    private Integer height;

}
