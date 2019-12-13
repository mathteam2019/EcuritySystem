package com.nuctech.ecuritycheckitem.service.settingmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerScanParam;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface ScanParamService {

    SerScanParam getById(Long paramId);
    PageResult<SerScanParam> getScanParamListByFilter(String deviceName, String status, Integer currentPage, Integer perPage);
    List<SerScanParam> getAllWithFilter(String deviceName, String status);
    List<SerScanParam> getAllWithoutFilter();

    boolean modifyScanParam(Long paramDeviceId, SerScanParam serScanParamNew);

}
