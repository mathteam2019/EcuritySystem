package com.nuctech.securitycheck.backgroundservice.common.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * 注销信息
 *
 * @author LovleyXing
 * @version v1.0
 * @since 2019-11-29
 */
@Data
@ApiModel(value = "SysUnregisterModel", description = "注销信息")
public class SysUnregisterModel {

    @ApiModelProperty(value = "安检仪GUID")
    private String guid;

    @ApiModelProperty(value = "时间")
    private String Time;

    @JsonProperty("Time")
    public void setTime(String Time) {
        this.Time = Time;
    }

    public int checkValid() {
        if(StringUtils.isBlank(guid) || StringUtils.isBlank(Time)) {
            return 1;
        }
        if(DateUtil.stringDateToDate(Time) == null) {
            return 2;
        }
        return 0;
    }

}
