package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 安检仪产生的数据，预留
 *
 * @author ChuiGuangCheng
 * @version v1.0
 * @since 2019-12-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(value = "PersonDataModel", description = "安检仪产生的数据，预留")
public class PersonDataModel {
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
}
