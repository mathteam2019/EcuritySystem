package com.nuctech.ecuritycheckitem.models.redis;

import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceConfig;
import com.nuctech.ecuritycheckitem.models.db.SysJudgeGroup;
import com.nuctech.ecuritycheckitem.models.db.SysManualGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 设备配置信息
 *
 * @author CheGuangXing
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerSecurityDeviceDetailModel {

    @ApiModelProperty(value = "安检仪 GUID")
    private SysDeviceConfigSimple deviceConfig;

    @ApiModelProperty(value = "设备编号")
    private List<SysJudgeGroupSimple> judgeGroups;

    @ApiModelProperty(value = "设备编号")
    private List<SysManualGroupSimple> manualGroups;


}
