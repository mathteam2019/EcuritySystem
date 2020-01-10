package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 手检站后台服务请求提交手检结论-请求报文定义
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "HandSerResultModel", description = "手检站后台服务请求提交手检结论-请求报文定义")
public class HandSerResultModel {

    @NotBlank
    @ApiModelProperty(value = "手检站 GUID")
    private String guid;

    @ApiModelProperty(value = "手检业务数据")
    private CheckResultModel checkResult;

    public int checkValid() {
        if(StringUtils.isBlank(guid) || checkResult.checkValid() == 1) {
            return 1;
        }
        if(checkResult.checkValid() == 2) {
            return 2;
        }
        return 0;
    }

//    @ApiModelProperty(value = "手检结论")
//    private String result;
//
//    @ApiModelProperty(value = "时间")
//    private String time;
//
//    @ApiModelProperty(value = "查获物品")
//    private String checklist;
//
//    @ApiModelProperty(value = "评价判图")
//    private String imageJudge;
//
//    @ApiModelProperty(value = "收藏")
//    private String imageKeep;
//
//    @ApiModelProperty(value = "备注")
//    private String note;
//
//    @ApiModelProperty(value = "图片、视频")
//    private String files;
//
//    @NotBlank
//    @ApiModelProperty(value = "手检提交的嫌疑框信息")
//    private List<SubmitRectInfoModel> submitRects;

}
