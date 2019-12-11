package com.nuctech.ecuritycheckitem.service.logmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;
import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;

public interface AuditLogService {
    PageResult<SysAuditLog> getAuditLogListByFilter(String clientIp, String operateResult, String operateObject, Date operateStartTime,
                                                     Date operateEndTime, int currentPage, int perPage);

    List<SysAuditLog> getExportList(String clientIp, String operateResult, String operateObject, Date operateStartTime,
                                     Date operateEndTime, boolean isAll, String idList);
}
