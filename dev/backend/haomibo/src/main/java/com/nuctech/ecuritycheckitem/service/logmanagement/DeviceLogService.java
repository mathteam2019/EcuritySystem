package com.nuctech.ecuritycheckitem.service.logmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;

public interface DeviceLogService {
    PageResult<SerDevLog> getDeviceLogListByFilter(String deviceType, String deviceName, String userName, Long category, Long level, Date operateStartTime,
                                                   Date operateEndTime, int currentPage, int perPage);

    List<SerDevLog> getExportList(String deviceType, String deviceName, String userName, Long category, Long level, Date operateStartTime,
                                  Date operateEndTime, boolean isAll, String idList);
}
