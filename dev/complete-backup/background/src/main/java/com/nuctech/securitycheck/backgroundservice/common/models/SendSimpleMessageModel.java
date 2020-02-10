package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(value = "SendSimpleMessageModel", description = "发送消息到设备")
public class SendSimpleMessageModel {

    @NotBlank
    @ApiModelProperty(value = "安检仪GUID")
    private String guid;

    @ApiModelProperty(value = "图片 guid")
    private String imageGuid;

    public int checkValid() {
        if(StringUtils.isBlank(imageGuid) || StringUtils.isBlank(guid)) {
            return 1;
        }
        return 0;
    }

}
