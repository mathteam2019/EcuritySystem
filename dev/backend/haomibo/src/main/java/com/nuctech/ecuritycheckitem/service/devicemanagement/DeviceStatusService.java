package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SerDeviceStatus;
import com.nuctech.ecuritycheckitem.utils.PageResult;

public interface DeviceStatusService {
    PageResult<SerDeviceStatus> getFDeviceStatusByFilter(Long fieldId, String deviceName, Long categoryId, int currentPage, int perPage);
}
