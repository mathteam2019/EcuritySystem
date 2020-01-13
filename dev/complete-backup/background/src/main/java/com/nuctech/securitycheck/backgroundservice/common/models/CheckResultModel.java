package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 主要为手检站产生的数据
 *
 * @author ChuiGuangCheng
 * @version v1.0
 * @since 2019-12-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(value = "CheckResultModel", description = "主要为手检站产生的数据")
public class CheckResultModel {

    @ApiModelProperty(value = "图片 guid")
    private String imageGuid;

    @ApiModelProperty(value = "手检结论")
    private String result;

    @ApiModelProperty(value = "时间")
    private String time;

    @ApiModelProperty(value = "查获物品")
    private String checklist;

    @ApiModelProperty(value = "评价判图1")
    private String imageJudge1;

    @ApiModelProperty(value = "评价判图2")
    private String imageJudge2;

    @ApiModelProperty(value = "收藏")
    private String imageKeep;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "图片、视频")
    private String files;

    @ApiModelProperty(value = "手检提交的嫌疑框信息")
    private List<SubmitRectInfoModel> submitRects;

    @ApiModelProperty(value = "手检提交的嫌疑框信息")
    private List<SubmitRectInfoModel> submitCartoonRects;

    public int checkValid() {
        if(StringUtils.isBlank(imageGuid) || submitRects == null) {
            return 1;
        }
        return 0;
    }
}
