package com.nuctech.ecuritycheckitem.service.fieldmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysField;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface FieldService {
    void createField(SysField sysField);

    boolean checkFieldExist(Long fieldId);

    boolean checkDeviceExist(Long fieldId);

    boolean checkFieldSerial(String fieldSerial, Long fieldId);

    boolean checkFieldDesignation(String fieldDesignation, Long fieldId);

    boolean checkHasChild(Long fieldId);

    void modifyField(SysField sysField);

    void removeField(Long fieldId);

    void updateStatus(Long fieldId, String status);

    List<SysField> findAll();

    PageResult<SysField> getDeviceListByFilter(String designation, String status, String parentDesignation, int currentPage, int perPage);

    List<SysField> getExportList(String designation, String status, String parentDesignation, boolean isAll, String idList);
}
