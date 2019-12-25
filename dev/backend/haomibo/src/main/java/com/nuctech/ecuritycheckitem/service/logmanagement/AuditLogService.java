/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AuditLogService）
 * 文件名：	AuditLogService.java
 * 描述：	AuditLogService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.logmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;

public interface AuditLogService {

    /**
     * get paginated and filtered audit log
     * @param clientIp
     * @param operateResult
     * @param operateObject
     * @param operateStartTime
     * @param operateEndTime
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysAuditLog> getAuditLogListByFilter(String clientIp, String operateResult, String operateObject, Date operateStartTime,
                                                     Date operateEndTime, int currentPage, int perPage);

    /**
     * get audit log export list
     * @param clientIp
     * @param operateResult
     * @param operateObject
     * @param operateStartTime
     * @param operateEndTime
     * @param isAll
     * @param idList
     * @return
     */
    List<SysAuditLog> getExportList(String clientIp, String operateResult, String operateObject, Date operateStartTime,
                                     Date operateEndTime, boolean isAll, String idList);
}
