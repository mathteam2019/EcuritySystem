package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.controllers.devicemanagement.DeviceControlController;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DeviceService {

    boolean checkDeviceExist(Long deviceId);

    boolean checkArchiveExist(Long archiveId);

    PageResult<SysDevice> getFilterDeviceList(String archiveName, String deviceName, String status, Long fieldId, Long categoryId,
                                              int startIndex, int endIndex);

    List<SysDevice> getExportDataList(String archiveName, String deviceName, String status, Long fieldId, Long categoryId,
                                      boolean isAll, String idList);

    void updateStatus(Long deviceId, String status);

    void createDevice(SysDevice sysDevice, MultipartFile portraitFile);

    void modifyDevice(SysDevice sysDevice, MultipartFile portraitFile);

    void removeDevice(Long deviceId);

    void modifyDeviceField(List<SysDevice> deviceList);

    List<SysDevice> findAll();

    List<SysDevice> getEmptyFieldDevice(Long categoryId);

    List<SysDevice> getDeviceByField(Long fieldId, Long categoryId);



}
