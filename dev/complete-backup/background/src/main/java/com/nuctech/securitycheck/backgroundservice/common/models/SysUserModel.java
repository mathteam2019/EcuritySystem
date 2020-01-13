package com.nuctech.securitycheck.backgroundservice.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户对象
 *
 * @author CheGuangXing
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@ApiModel(value = "SysUserModel", description = "用户对象")
public class SysUserModel {

    @ApiModelProperty(value = "登录名")
    private String LoginName;

    @ApiModelProperty(value = "密码")
    private String password;

    @JsonProperty("LoginName")
    public void setLoginName(String LoginName) {
        this.LoginName = LoginName;
    }

    
}
