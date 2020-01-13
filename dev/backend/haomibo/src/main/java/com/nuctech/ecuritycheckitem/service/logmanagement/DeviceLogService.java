/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceLogService）
 * 文件名：	DeviceLogService.java
 * 描述：	DeviceLogService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.logmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;

public interface DeviceLogService {

    /**
     * get paginated and filtered device log list
     * @param deviceType
     * @param deviceName
     * @param userName
     * @param category
     * @param level
     * @param operateStartTime
     * @param operateEndTime
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SerDevLog> getDeviceLogListByFilter(String sortBy, String order, String deviceType, String deviceName, String userName, Long category, Long level, Date operateStartTime,
                                                   Date operateEndTime, int currentPage, int perPage);

    /**
     * get device log export list
     * @param deviceType
     * @param deviceName
     * @param userName
     * @param category
     * @param level
     * @param operateStartTime
     * @param operateEndTime
     * @param isAll
     * @param idList
     * @return
     */
    List<SerDevLog> getExportList(String sortBy, String order, String deviceType, String deviceName, String userName, Long category, Long level, Date operateStartTime,
                                  Date operateEndTime, boolean isAll, String idList);
}
