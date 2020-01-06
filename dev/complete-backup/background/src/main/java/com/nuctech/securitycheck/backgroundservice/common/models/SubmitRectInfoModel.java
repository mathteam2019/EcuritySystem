package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 提交的嫌疑框信息
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "SubmitRectInfo", description = "提交的嫌疑框信息")
public class SubmitRectInfoModel {

    @NotBlank
    @ApiModelProperty(value = "新增加的嫌疑框")
    private List<ImageRectModel> rectsAdded;

    @NotBlank
    @ApiModelProperty(value = "删除的 ATR 嫌疑框")
    private List<ImageRectModel> rectsDeleted;

}
