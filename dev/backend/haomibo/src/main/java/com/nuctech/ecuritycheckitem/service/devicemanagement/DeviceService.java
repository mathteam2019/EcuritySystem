/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceService）
 * 文件名：	DeviceService.java
 * 描述：	DeviceService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DeviceService {

    /**
     * check if device exists
     * @param deviceId
     * @return
     */
    boolean checkDeviceExist(Long deviceId);

    /**
     * check if archive exists
     * @param archiveId
     * @return
     */
    boolean checkArchiveExist(Long archiveId);



    /**
     * check if device name exists
     * @param deviceName
     * @param deviceId
     * @return
     */
    boolean checkDeviceNameExist(String deviceName, Long deviceId);

    /**
     * check if device serial exists
     * @param deviceSerial
     * @param deviceId
     * @return
     */
    boolean checkDeviceSerialExist(String deviceSerial, Long deviceId);

    /**
     * check if device have field or not
     * @param deviceId
     * @return
     */
    boolean checkDeviceContainField(Long deviceId);

    /**
     * check if device is security and it's config setting is active or not.
     * @param deviceId
     * @return
     */
    int checkDeviceConfigActive(Long deviceId);

    /**
     * check if device guid exists
     * @param guid
     * @param deviceId
     * @return
     */
    boolean checkDeviceGuidExist(String guid, Long deviceId);

    /**
     * check if device status and it used in history
     * @param deviceId
     * @return
     */
    int checkDeviceStatus(Long deviceId);

    /**
     * get filtered and paginated device list
     * @param archiveName
     * @param deviceName
     * @param status
     * @param fieldId
     * @param categoryId
     * @param startIndex
     * @param endIndex
     * @return
     */
    PageResult<SysDevice> getFilterDeviceList(String sortBy, String order, String archiveName, String deviceName, String status, Long fieldId, Long categoryId,
                                              int startIndex, int endIndex);

    /**
     * get export device list
     * @param archiveName
     * @param deviceName
     * @param status
     * @param fieldId
     * @param categoryId
     * @param isAll
     * @param idList
     * @return
     */
    List<SysDevice> getExportDataList(String sortBy, String order, String archiveName, String deviceName, String status, Long fieldId, Long categoryId,
                                      boolean isAll, String idList);

    /**
     * update device status
     * @param deviceId
     * @param status
     */
    void updateStatus(Long deviceId, String status);

    /**
     * create device
     * @param sysDevice
     * @param portraitFile
     */
    void createDevice(SysDevice sysDevice, MultipartFile portraitFile);

    /**
     * edit device
     * @param sysDevice
     * @param portraitFile
     */
    void modifyDevice(SysDevice sysDevice, MultipartFile portraitFile);

    /**
     * remove device
     * @param deviceId
     */
    boolean removeDevice(Long deviceId);

    /**
     * edit device field id
     * @param deviceList
     */
    void modifyDeviceField(List<SysDevice> deviceList);

    /**
     * find all device
     * @return
     */
    List<SysDevice> findAll();

    /**
     * get empty field device
     * @param categoryId
     * @return
     */
    List<SysDevice> getEmptyFieldDevice(Long categoryId);

    /**
     * get device by field
     * @param fieldId
     * @param categoryId
     * @return
     */
    List<SysDevice> getDeviceByField(Long fieldId, Long categoryId);



}
