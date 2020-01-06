package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@ApiModel(value = "DeviceOvertimeModel", description = "主要为手检站产生的数据")
public class DeviceOvertimeModel {

    @ApiModelProperty(value = "GUID")
    private String guid;

    @ApiModelProperty(value = "提醒")
    private Boolean remind;

}
