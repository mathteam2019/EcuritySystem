package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface DeviceCategoryService {
    PageResult<SysDeviceCategory> getDeviceCategoryListByPage(String categoryName, String status, String parentCategoryName, int currentPage, int perPage);

    boolean checkArchiveTemplateExist(long categoryId);

    boolean checkCategoryExist(long categoryId);

    boolean checkChildernCategoryExist(long categoryId);

    void updateStatus(long categoryId, String status);

    void createSysDeviceCategory(SysDeviceCategory deviceCategory);

    void modifySysDeviceCategory(SysDeviceCategory deviceCategory);

    void removeSysDeviceCategory(long categoryId);

    List<SysDeviceCategory> findAll();

    List<SysDeviceCategory> getExportListByFilter(String categoryName, String status, String parentCategoryName, boolean isAll, String idList);

}
