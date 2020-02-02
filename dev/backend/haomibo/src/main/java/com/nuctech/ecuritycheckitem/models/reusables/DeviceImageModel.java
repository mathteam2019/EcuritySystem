package com.nuctech.ecuritycheckitem.models.reusables;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 图像信息，包括嫌疑框
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "DeviceImage", description = "图像信息，包括嫌疑框")
public class DeviceImageModel {

    @ApiModelProperty(value = "图像宽度", example = "0")
    private Integer width;

    @ApiModelProperty(value = "图像高度", example = "0")
    private Integer height;

    @ApiModelProperty(value = "图像")
    private String image;

    @ApiModelProperty(value = "卡通图")
    private String cartoon;

    @ApiModelProperty(value = "原始图像嫌疑框")
    private List<ImageRectModel> imageRects;

    @ApiModelProperty(value = "卡通图像嫌疑框")
    private List<ImageRectModel> cartoonRects;



}
