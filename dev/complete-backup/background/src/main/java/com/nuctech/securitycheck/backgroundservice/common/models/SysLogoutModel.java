package com.nuctech.securitycheck.backgroundservice.common.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
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
    @NotBlank
    @Length(min = 1, max = 36)
    private String guid;

    @ApiModelProperty(value = "用户名")
    @NotBlank
    @Length(min = 1, max = 50)
    private String userName;

    @ApiModelProperty(value = "登出时间")
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logoutTime;

}
