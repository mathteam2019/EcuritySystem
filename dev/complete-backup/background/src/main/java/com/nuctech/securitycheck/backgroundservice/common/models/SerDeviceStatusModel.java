package com.nuctech.securitycheck.backgroundservice.common.models;

import com.nuctech.securitycheck.backgroundservice.common.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ApiModel(value = "SerDeviceStatusModel", description = "设备向后台服务发送 flow 信息。每次状态更新即发送。")
public class SerDeviceStatusModel {

    @ApiModelProperty(value = "安检仪 GUID")
    private String guid;

    @ApiModelProperty(value = "flowName")
    private String flowName;

    @ApiModelProperty(value = "flowStatus")
    private String flowStatus;

    @ApiModelProperty(name = "diskSpace")
    private String diskSpace;

    public int checkValid() {
        if(StringUtils.isBlank(guid) || StringUtils.isBlank(flowName) || StringUtils.isBlank(flowStatus)) {
            return 1;
        }
        return 0;
    }

}
