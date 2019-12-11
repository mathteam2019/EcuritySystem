package com.nuctech.ecuritycheckitem.service.settingmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerPlatformOtherParams;

import java.util.List;

public interface PlatformOtherService {
    List<SerPlatformOtherParams> findAll();

    void modifyPlatform(SerPlatformOtherParams serPlatformOtherParams);
}
