package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SysDeviceConfig;
import com.nuctech.ecuritycheckitem.models.db.SysJudgeDevice;
import com.nuctech.ecuritycheckitem.models.db.SysManualDevice;
import com.nuctech.ecuritycheckitem.models.db.SysWorkMode;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface DeviceConfigService {

    SysDeviceConfig findConfigById(Long configId);
    PageResult<SysDeviceConfig> findConfigByFilter(String deviceName, Long fieldId, Long categoryId, int currentPage, int perPage);
    void modifyDeviceConfig(SysDeviceConfig sysDeviceConfig, List<Long> manualDeviceIdList, List<Long> judgeDeviceIdList,
                            List<Long> configDeviceIdList);
    void removeDeviceConfig(SysDeviceConfig sysDeviceConfig);
    List<SysDeviceConfig> findAllDeviceConfigExceptId(Long deviceId);
    List<SysWorkMode> findAllWorkMode();
    List<SysManualDevice> findAllManualDevice();
    List<SysJudgeDevice> findAllJudgeDevice();

}
