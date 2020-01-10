package com.nuctech.securitycheck.backgroundservice.common.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nuctech.securitycheck.backgroundservice.common.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
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

    @ApiModelProperty(value = "图片 guid")
    private String imageGuid;

    @ApiModelProperty(value = "判图站用户")
    private String userName;

    @ApiModelProperty(value = "判图结果")
    private String result;

    @ApiModelProperty(value = "提交结论时间")
    private String time;

    @ApiModelProperty(value = "是否超时")
    private String isTimeout;

    @ApiModelProperty(value = "提交的嫌疑框信息")
    private List<SubmitRectInfoModel> submitRects;

    public int checkValid() {
        if(StringUtils.isBlank(imageGuid) || StringUtils.isBlank(userName) || StringUtils.isBlank(result) || StringUtils.isBlank(time) || StringUtils.isBlank(isTimeout)) {
            return 1;
        }
        if(DateUtil.stringDateToDate(time) == null || submitRects == null) {
            return 2;
        }
        return 0;
    }
}
