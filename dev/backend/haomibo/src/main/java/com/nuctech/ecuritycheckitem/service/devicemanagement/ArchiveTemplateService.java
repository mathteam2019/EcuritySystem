/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ArchiveTemplateService）
 * 文件名：	ArchiveTemplateService.java
 * 描述：	ArchiveTemplate Service interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SerArchiveIndicators;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveTemplate;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface ArchiveTemplateService {

    /**
     * get pagniated and filteredarchive template
     * @param archiveName
     * @param status
     * @param categoryId
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SerArchiveTemplate> getArchiveTemplateListByPage(String sortBy, String order, String archiveName, String status, Long categoryId, int currentPage, int perPage);

    /**
     * check if archive exists
     * @param archiveTemplateId
     * @return
     */
    boolean checkArchiveExist(long archiveTemplateId);

    /**
     * check if archiveTemplate exists
     * @param archiveTemplateId
     * @return
     */
    boolean checkArchiveTemplateExist(long archiveTemplateId);

    /**
     * check archiveTemplateName exists
     * @param templateName
     * @param templateId
     * @return
     */
    boolean checkTemplateNameExist(String templateName, Long templateId);

    /**
     * check template number exists
     * @param templateNumber
     * @param templateId
     * @return
     */
    boolean checkTemplateNumberExist(String templateNumber, Long templateId);

    /**
     * check device category exists
     * @param categoryId
     * @return
     */
    boolean checkCategoryExist(long categoryId);

    /**
     * update archive template status
     * @param templateId
     * @param status
     */
    void updateStatus(long templateId, String status);

    /**
     * create archive indicator
     * @param archiveIndicators
     */
    void createArchiveIndicator(SerArchiveIndicators archiveIndicators);

    /**
     * update archive indicator status
     * @param indicatorId
     * @param isNull
     * @return
     */
    int updateIndicatorStatus(long indicatorId, String isNull);

    /**
     * create archive template
     * @param serArchiveTemplate
     */
    void createSerArchiveTemplate(SerArchiveTemplate serArchiveTemplate);

    /**
     * edit archive template
     * @param serArchiveTemplate
     */
    void modifySerArchiveTemplate(SerArchiveTemplate serArchiveTemplate);

    /**
     * remove archive template
     * @param archiveTemplateId
     */
    boolean removeSerArchiveTemplate(long archiveTemplateId);

    /**
     * remove archive indicator
     * @param archiveIndicatorId
     * @return
     */
    int removeSerArchiveIndicator(long archiveIndicatorId);

    /**
     * find all archive templates
     * @return
     */
    List<SerArchiveTemplate> findAll();

    /**
     * get filtered archive template export list
     * @param archiveName
     * @param status
     * @param categoryId
     * @param isAll
     * @param idList
     * @return
     */
    List<SerArchiveTemplate> getExportListByFilter(String sortBy, String order, String archiveName, String status, Long categoryId, boolean isAll, String idList);
}
