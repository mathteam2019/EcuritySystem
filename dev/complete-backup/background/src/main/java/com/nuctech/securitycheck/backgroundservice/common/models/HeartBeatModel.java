package com.nuctech.securitycheck.backgroundservice.common.models;

import com.nuctech.securitycheck.backgroundservice.common.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 心跳信息
 *
 * @author CheGuangXing
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "HeartBeatModel", description = "心跳信息")
public class HeartBeatModel {

    @NotBlank
    private String guid;

    @NotBlank
    private String heartbeatTime;

    public int checkValid() {
        if(StringUtils.isBlank(guid) || StringUtils.isBlank(heartbeatTime)) {
            return 1;
        }
        if(DateUtil.stringDateToDate(heartbeatTime) == null) {
            return 2;
        }
        return 0;
    }
    
}
