package com.nuctech.securitycheck.backgroundservice.common.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nuctech.securitycheck.backgroundservice.common.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 登出信息
 *
 * @author LovleyXing
 * @version v1.0
 * @since 2019-11-29
 */
@Data
@ApiModel(value = "SysLogoutModel", description = "登出信息")
public class SysLogoutModel {

    @ApiModelProperty(value = "安检仪GUID")
    private String guid;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "登出时间")
    private String logoutTime;

    public int checkValid() {
        if(StringUtils.isBlank(guid) || StringUtils.isBlank(userName) || StringUtils.isBlank(logoutTime)) {
            return 1;
        }
        if(DateUtil.stringDateToDate(logoutTime) == null) {
            return 2;
        }
        return 0;
    }



}
