/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AccessLogService）
 * 文件名：	AccessLogService.java
 * 描述：	AccessLogService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.logmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.es.EsSysAccessLog;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;


public interface AccessLogService {

    /**
     * get paginated and filtered access log list
     * @param clientIp
     * @param operateAccount
     * @param operateStartTime
     * @param operateEndTime
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysAccessLog> getAccessLogListByFilter(String sortBy, String order, String clientIp, String operateAccount, String action, String operateResult, Date operateStartTime,
                                                        Date operateEndTime, int currentPage, int perPage);

    /**
     * get access log export list
     * @param clientIp
     * @param operateAccount
     * @param operateStartTime
     * @param operateEndTime
     * @param isAll
     * @param idList
     * @return
     */
    List<SysAccessLog> getExportList(String sortBy, String order, String clientIp, String action, String operateResult, String operateAccount, Date operateStartTime,
                                  Date operateEndTime, boolean isAll, String idList);

    /**
     *
     * @param action: 操作
     * @param result: 操作结果
     * @param reason: 失败原因代码
     * @param onlineTime: 在线时长(秒)
     * @return
     */
    boolean saveAccessLog(SysUser user, String action, String result, String reason, Long onlineTime);
}
