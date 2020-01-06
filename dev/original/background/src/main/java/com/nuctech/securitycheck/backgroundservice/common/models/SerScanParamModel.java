package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统参数-安检仪
 *
 * @author CheGuangXing
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "SerScanParamModel", description = "系统参数-安检仪")
@Builder(toBuilder = true)
public class SerScanParamModel {

    @ApiModelProperty(value = "罩板校正时间", example = "0")
    private Long AirCaliWarnTime;

    @ApiModelProperty(value = "设备端待机时间", example = "0")
    private Long StandbyTime;

    @ApiModelProperty(value = "报警提示")
    private String AlarmSound;

    @ApiModelProperty(value = "通过提示")
    private String PassSound;

    @ApiModelProperty(value = "站姿错误提示")
    private String PoserrorSound;

    @ApiModelProperty(value = "开始扫描提醒")
    private String StandSound;

    @ApiModelProperty(value = "扫描过程")
    private String ScanSound;

    @ApiModelProperty(value = "扫描完成提示")
    private String ScanOverUseSound;

    @ApiModelProperty(value = "辅助识别")
    private String AutoRecognise;

    @ApiModelProperty(value = "辅助识别等级", example = "0")
    private Long RecognitionRate;

    @ApiModelProperty(value = "保存历史图像")
    private String SaveScanData;

    @ApiModelProperty(value = "仅保存嫌疑图像")
    private String SaveSuspectData;

    @ApiModelProperty(value = "面部模糊处理")
    private String FacialBlurring;

    @ApiModelProperty(value = "胸部模糊处理")
    private String ChestBlurring;

    @ApiModelProperty(value = "臀部模糊处理")
    private String HipBlurring;

    @ApiModelProperty(value = "腹股沟模糊处理")
    private String GroinBlurring;

}
