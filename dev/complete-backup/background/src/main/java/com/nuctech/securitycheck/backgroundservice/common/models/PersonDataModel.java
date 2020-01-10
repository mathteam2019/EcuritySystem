package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
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
    @ApiModelProperty(value = "证件类型*", example = "0")
    private String type;

    @ApiModelProperty(value = "证件号")
    private String number;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "人脸")
    private String face;

    @ApiModelProperty(value = "性别", example = "0")
    private String sex;

    private boolean checkValidValue(String str, boolean isSex) {
        try {
            long value = Long.valueOf(str);
            if(isSex) {
                if(value != 0 && value != 1) {
                    return false;
                }
            }
            return true;
        } catch(Exception ex) {
            return false;
        }
    }

    public int checkValid() {
        if(StringUtils.isBlank(type) || StringUtils.isBlank(number) || StringUtils.isBlank(name) || StringUtils.isBlank(address)
                || StringUtils.isBlank(face) || StringUtils.isBlank(face)) {
            return 1;
        }
        if(!checkValidValue(type, false) || !checkValidValue(sex, true)) {
            return 2;
        }
        return 0;
    }
}
