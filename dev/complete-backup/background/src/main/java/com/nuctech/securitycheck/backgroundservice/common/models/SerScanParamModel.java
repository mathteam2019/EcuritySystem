package com.nuctech.securitycheck.backgroundservice.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("AirCaliWarnTime")
    public void setAirCaliWarnTime(Long AirCaliWarnTime) {
        this.AirCaliWarnTime = AirCaliWarnTime;
    }


    @ApiModelProperty(value = "设备端待机时间", example = "0")
    private Long StandbyTime;

    @JsonProperty("StandbyTime")
    public void setStandbyTime(Long StandbyTime) {
        this.StandbyTime = StandbyTime;
    }

    @ApiModelProperty(value = "报警提示")
    private String AlarmSound;

    @JsonProperty("AlarmSound")
    public void setAlarmSound(String AlarmSound) {
        this.AlarmSound = AlarmSound;
    }

    @ApiModelProperty(value = "通过提示")
    private String PassSound;

    @JsonProperty("PassSound")
    public void setPassSound(String PassSound) {
        this.PassSound = PassSound;
    }

    @ApiModelProperty(value = "站姿错误提示")
    private String PoserrorSound;

    @JsonProperty("PoserrorSound")
    public void setPoserrorSound(String PoserrorSound) {
        this.PoserrorSound = PoserrorSound;
    }

    @ApiModelProperty(value = "开始扫描提醒")
    private String StandSound;

    @JsonProperty("StandSound")
    public void setStandSound(String StandSound) {
        this.StandSound = StandSound;
    }

    @ApiModelProperty(value = "扫描过程")
    private String ScanSound;

    @JsonProperty("ScanSound")
    public void setScanSound(String ScanSound) {
        this.ScanSound = ScanSound;
    }

    @ApiModelProperty(value = "扫描完成提示")
    private String ScanOverUseSound;

    @JsonProperty("ScanOverUseSound")
    public void setScanOverUseSound(String ScanOverUseSound) {
        this.ScanOverUseSound = ScanOverUseSound;
    }

    @ApiModelProperty(value = "辅助识别")
    private String AutoRecognise;

    @JsonProperty("AutoRecognise")
    public void setAutoRecognise(String AutoRecognise) {
        this.AutoRecognise = AutoRecognise;
    }

    @ApiModelProperty(value = "辅助识别等级", example = "0")
    private Long RecognitionRate;

    @JsonProperty("RecognitionRate")
    public void setRecognitionRate(Long RecognitionRate) {
        this.RecognitionRate = RecognitionRate;
    }

    @ApiModelProperty(value = "保存历史图像")
    private String SaveScanData;

    @JsonProperty("SaveScanData")
    public void setSaveScanData(String SaveScanData) {
        this.SaveScanData = SaveScanData;
    }

    @ApiModelProperty(value = "仅保存嫌疑图像")
    private String SaveSuspectData;

    @JsonProperty("SaveSuspectData")
    public void setSaveSuspectData(String SaveSuspectData) {
        this.SaveSuspectData = SaveSuspectData;
    }

    @ApiModelProperty(value = "面部模糊处理")
    private String FacialBlurring;

    @JsonProperty("FacialBlurring")
    public void setFacialBlurring(String FacialBlurring) {
        this.FacialBlurring = FacialBlurring;
    }

    @ApiModelProperty(value = "胸部模糊处理")
    private String ChestBlurring;

    @JsonProperty("ChestBlurring")
    public void setChestBlurring(String ChestBlurring) {
        this.ChestBlurring = ChestBlurring;
    }

    @ApiModelProperty(value = "臀部模糊处理")
    private String HipBlurring;

    @JsonProperty("HipBlurring")
    public void setHipBlurring(String HipBlurring) {
        this.HipBlurring = HipBlurring;
    }

    @ApiModelProperty(value = "腹股沟模糊处理")
    private String GroinBlurring;

    @JsonProperty("GroinBlurring")
    public void setGroinBlurring(String GroinBlurring) {
        this.GroinBlurring = GroinBlurring;
    }

}
