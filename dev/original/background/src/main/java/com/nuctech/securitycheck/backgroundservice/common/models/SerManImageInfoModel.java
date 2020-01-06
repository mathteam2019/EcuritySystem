package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;

/**
 * 后台服务向手检站推送业务数据-请求报文定义
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(value = "SerManImageInfoModel", description = "后台服务向手检站推送业务数据-请求报文定义")
public class SerManImageInfoModel implements Serializable {

    @NotBlank
    @ApiModelProperty(value = "安检仪 GUID")
    private String guid;

    @ApiModelProperty(value = "离线标记", example = "0")
    private Integer offline;

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

    @NotBlank
    @ApiModelProperty(value = "证件类型*", example = "0")
    private Integer type;

    @NotBlank
    @ApiModelProperty(value = "证件号")
    private String number;

    @NotBlank
    @ApiModelProperty(value = "姓名")
    private String name;

    @NotBlank
    @ApiModelProperty(value = "地址")
    private String address;

    @NotBlank
    @ApiModelProperty(value = "人脸")
    private String face;

    @ApiModelProperty(value = "性别", example = "0")
    private Integer sex;

    @ApiModelProperty(value = "判图结论")
    private String ImageResult;

    @ApiModelProperty(value = "提交的嫌疑框信息")
    private List<SubmitRectInfoModel> submitRects;
    
}
