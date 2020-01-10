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
 * 登录信息
 *
 * @author LovleyXing
 * @version v1.0
 * @since 2019-11-29
 */
@Data
@ApiModel(value = "SysLoginModel", description = "登录信息")
public class SysLoginModel {

    @ApiModelProperty(value = "安检仪GUID")
    private String guid;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "登录时间")
    private String loginTime;

    public int checkValid() {
        if(StringUtils.isBlank(guid) || StringUtils.isBlank(userName) || StringUtils.isBlank(password) || StringUtils.isBlank(loginTime)) {
            return 1;
        }
        if(DateUtil.stringDateToDate(loginTime) == null) {
            return 2;
        }
        return 0;
    }

}
