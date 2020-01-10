package com.nuctech.securitycheck.backgroundservice.common.models;

import com.nuctech.securitycheck.backgroundservice.common.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 日志信息
 *
 * @author benja
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "SerDevLogModel", description = "设备向后台服务发送日志信息")
public class SerDevLogModel {

    @ApiModelProperty(value = "设备 GUID")
    private String guid;

    @ApiModelProperty(value = "操作员账号")
    private String loginName;

    @ApiModelProperty(value = "类别", example = "0")
    private String category;

    @ApiModelProperty(value = "日志级别", example = "0")
    private String level;

    @ApiModelProperty(value = "日志内容", notes = "日志内容")
    private String content;

    @ApiModelProperty(value = "时间", notes = "时间")
    private String time;

    private boolean checkLongValue(String str, boolean isLevel) {
        try {
            long value = Long.valueOf(str);
            if(isLevel == true) {
                if(value >= 0 && value <= 4) {
                    return true;
                }
                return false;
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public int checkValid() {
        if(StringUtils.isBlank(guid) || StringUtils.isBlank(category)) {
            return 1;
        }
        if(DateUtil.stringDateToDate(time) == null || !checkLongValue(category, false) || !checkLongValue(level, true)) {
            return 2;
        }
        return 0;
    }

}
