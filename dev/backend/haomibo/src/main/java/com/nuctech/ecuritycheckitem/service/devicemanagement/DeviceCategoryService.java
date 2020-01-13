/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceCategoryService）
 * 文件名：	DeviceCategoryService.java
 * 描述：	DeviceCategoryService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface DeviceCategoryService {

    /**
     * get paginated and filtered device category list
     * @param categoryName
     * @param status
     * @param parentCategoryName
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysDeviceCategory> getDeviceCategoryListByPage(String sortBy, String order, String categoryName, String status, String parentCategoryName, int currentPage, int perPage);

    /**
     * check if archive template exists
     * @param categoryId
     * @return
     */
    boolean checkArchiveTemplateExist(long categoryId);

    /**
     * check if device category exists
     * @param categoryId
     * @return
     */
    boolean checkCategoryExist(long categoryId);

    /**
     * check if category name exists
     * @param categoryName
     * @param categoryId
     * @return
     */
    boolean checkCategoryNameExist(String categoryName, Long categoryId);

    /**
     * check if category number exists
     * @param categoryNumber
     * @param categoryId
     * @return
     */
    boolean checkCategoryNumberExist(String categoryNumber, Long categoryId);

    /**
     * check if children category exists
     * @param categoryId
     * @return
     */
    boolean checkChildernCategoryExist(long categoryId);

    /**
     * update category status
     * @param categoryId
     * @param status
     */
    void updateStatus(long categoryId, String status);

    /**
     * create device category
     * @param deviceCategory
     */
    void createSysDeviceCategory(SysDeviceCategory deviceCategory);

    /**
     * edit device category
     * @param deviceCategory
     */
    void modifySysDeviceCategory(SysDeviceCategory deviceCategory);

    /**
     * remove device category
     * @param categoryId
     */
    void removeSysDeviceCategory(long categoryId);

    /**
     * find all device category
     * @return
     */
    List<SysDeviceCategory> findAll();

    /**
     * get filtered device catergory export list
     * @param categoryName
     * @param status
     * @param parentCategoryName
     * @param isAll
     * @param idList
     * @return
     */
    List<SysDeviceCategory> getExportListByFilter(String sortBy, String order, String categoryName, String status, String parentCategoryName, boolean isAll, String idList);

}
