package com.nuctech.securitycheck.backgroundservice.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ApiModel(value = "DictDataModel", description = "字典信息")
public class FileUploadModel {

    @NotBlank
    @ApiModelProperty(value = "文档路径")
    private String fileName;
}
