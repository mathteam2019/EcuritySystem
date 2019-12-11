package com.nuctech.ecuritycheckitem.service.logmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;

public interface AccessLogService {
    PageResult<SysAccessLog> getAccessLogListByFilter(String clientIp, String operateAccount, Date operateStartTime,
                                                      Date operateEndTime, int currentPage, int perPage);

    List<SysAccessLog> getExportList(String clientIp, String operateAccount, Date operateStartTime,
                                  Date operateEndTime, boolean isAll, String idList);
}
