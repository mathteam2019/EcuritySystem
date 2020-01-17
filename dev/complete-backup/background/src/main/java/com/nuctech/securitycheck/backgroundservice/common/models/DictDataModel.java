package com.nuctech.securitycheck.backgroundservice.common.models;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerSeizedGood;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 字典信息
 *
 * @author CheGuangXing
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "DictDataModel", description = "字典信息")
public class DictDataModel {

    @ApiModelProperty(value = "Guid")
    private String guid;

    @ApiModelProperty(value = "查获物列表")
    private List<SeizedGoodModel> checklist;

    @ApiModelProperty(value = "评价判图列表")
    private String imageJudge;

}
