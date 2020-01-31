/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（FieldService）
 * 文件名：	FieldService.java
 * 描述：	FieldService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.fieldmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysField;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface FieldService {

    /**
     * create new field
     * @param sysField
     */
    void createField(SysField sysField);

    /**
     * check if field exists
     * @param fieldId
     * @return
     */
    boolean checkFieldExist(Long fieldId);

    /**
     * check if device exists
     * @param fieldId
     * @return
     */
    boolean checkDeviceExist(Long fieldId);

    /**
     * check if field serial exists
     * @param fieldSerial
     * @param fieldId
     * @return
     */
    boolean checkFieldSerial(String fieldSerial, Long fieldId);

    /**
     * check if field designation exists
     * @param fieldDesignation
     * @param fieldId
     * @return
     */
    boolean checkFieldDesignation(String fieldDesignation, Long fieldId);

    /**
     * check if has children
     * @param fieldId
     * @return
     */
    boolean checkHasChild(Long fieldId);

    /**
     * edit field
     * @param sysField
     */
    void modifyField(SysField sysField);

    /**
     * remove field
     * @param fieldId
     */
    boolean removeField(Long fieldId);

    /**
     * update field status
     * @param fieldId
     * @param status
     */
    void updateStatus(Long fieldId, String status);

    /**
     * find all fields
     * @param isAll
     * @return
     */
    List<SysField> findAll(boolean isAll);

    /**
     * get paginated and filtered field list
     * @param designation
     * @param status
     * @param parentDesignation
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysField> getDeviceListByFilter(String sortBy, String order, String designation, String status, String parentDesignation, int currentPage, int perPage);

    /**
     * get filtered export field list
     * @param designation
     * @param status
     * @param parentDesignation
     * @param isAll
     * @param idList
     * @return
     */
    List<SysField> getExportList(String sortBy, String order, String designation, String status, String parentDesignation, boolean isAll, String idList);
}
