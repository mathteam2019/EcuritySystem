package com.nuctech.ecuritycheckitem.service.settingmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;

import java.util.List;

public interface PlatformCheckService {
    List<SerPlatformCheckParams> findAll();

    void modifyPlatform(SerPlatformCheckParams serPlatformCheckParams);
}
