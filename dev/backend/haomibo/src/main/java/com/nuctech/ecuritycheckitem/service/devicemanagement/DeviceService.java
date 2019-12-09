package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.controllers.devicemanagement.DeviceControlController;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;

import java.util.List;

public interface DeviceService {
    List<SysDevice> getFilterDeviceList(DeviceControlController.DeviceGetByFilterAndPageRequestBody.Filter filter);
}
