package com.nuctech.securitycheck.backgroundservice.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysDeviceConfig;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysJudgeGroup;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysManualGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
public class SerDeviceConfigSettingModel {

    @ApiModelProperty(value = "安检仪 GUID")
    private SysDeviceConfig deviceConfig;

    @ApiModelProperty(value = "设备编号")
    private List<Long> judgeDeviceIdList;

    @ApiModelProperty(value = "设备编号")
    private List<Long> manualDeviceIdList;

    public void setJudgeList(List<SysJudgeGroup> judgeGroups) {
        List<Long> idList = new ArrayList<>();
        for(SysJudgeGroup judgeGroup: judgeGroups) {
            idList.add(judgeGroup.getJudgeDeviceId());
        }
        judgeDeviceIdList = idList;
    }

    public void setManualList(List<SysManualGroup> manualGroups) {
        List<Long> idList = new ArrayList<>();
        for(SysManualGroup manualGroup: manualGroups) {
            idList.add(manualGroup.getManualDeviceId());
        }
        manualDeviceIdList = idList;
    }
}
