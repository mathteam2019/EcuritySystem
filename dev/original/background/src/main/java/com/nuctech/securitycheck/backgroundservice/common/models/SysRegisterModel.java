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
 * 注册信息
 *
 * @author LovleyXing
 * @version v1.0
 * @since 2019-11-29
 */
@Data
@ApiModel(value = "SysRegisterModel", description = "注册信息")
public class SysRegisterModel {

    @ApiModelProperty(value = "安检仪GUID")
    @NotBlank
    @Length(min = 1, max = 36)
    private String guid;

    @ApiModelProperty(value = "时间")
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    @ApiModelProperty(value = "安检仪IP")
    @NotBlank
    @Length(min = 1, max = 15)
    private String ip;

    @ApiModelProperty(value = "软件版本号")
    @Length(max = 30)
    private String softwareVersion;

    @ApiModelProperty(value = "算法版本号")
    @Length(max = 30)
    private String algorithmVersion;

}
