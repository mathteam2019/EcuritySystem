package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 主要为安检仪产生的数据
 *
 * @author ChuiGuangCheng
 * @version v1.0
 * @since 2019-12-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(value = "ImageDataModel", description = "主要为安检仪产生的数据")
public class ImageDataModel {

    @ApiModelProperty(value = "离线标记", example = "0")
    private String offline;

    @ApiModelProperty(value = "图片 guid")
    private String imageGuid;

    @ApiModelProperty(value = "开始扫描时间")
    private String scanBeginTime;

    @ApiModelProperty(value = "结束扫描时间")
    private String scanEndTime;

    @ApiModelProperty(value = "操作员")
    private String loginName;

    @ApiModelProperty(value = "扫描图片性别", example = "0")
    private Integer imageGender;

    @ApiModelProperty(value = "ATR 自动识别辅助报警结果")
    private String atrResult;

    @ApiModelProperty(value = "无效扫描")
    private String invalidScan;

    @ApiModelProperty(value = "脚底检测结果")
    private String footAlarmed;

    @ApiModelProperty(value = "图像信息，包括嫌疑框")
    private List<DeviceImageModel> deviceImages;

    @ApiModelProperty(value = "原始数据")
    private String data;

    @ApiModelProperty(value = "随机报警标记")
    private String randomAlarm;

    public int checkValid() {
        if(StringUtils.isBlank(imageGuid) || StringUtils.isBlank(offline)) {
            return 1;
        }
        if(!offline.equals("0")) {
            return 2;
        }
        return 0;
    }
}
