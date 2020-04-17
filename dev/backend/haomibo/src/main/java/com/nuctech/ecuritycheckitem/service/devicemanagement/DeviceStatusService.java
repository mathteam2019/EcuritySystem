/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceStatusService）
 * 文件名：	DeviceStatusService.java
 * 描述：	DeviceStatusService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SerDeviceStatus;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface DeviceStatusService {

    /**
     * get paginated and filtered device status
     * @param fieldId
     * @param deviceName
     * @param categoryId
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SerDeviceStatus> getDeviceStatusByFilter(Long fieldId, String deviceName, Long categoryId, int currentPage, int perPage);

    SerDeviceStatus getDeviceStatusById(Long statusId);

    List<SerDeviceStatus> getDeviceDetailByGuidList(String guid);
}
