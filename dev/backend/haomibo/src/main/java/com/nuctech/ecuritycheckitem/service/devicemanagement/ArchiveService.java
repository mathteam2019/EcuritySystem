/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ArchiveService）
 * 文件名：	ArchiveService.java
 * 描述：	Archive Service interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SerArchive;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArchiveService {

    /**
     * get pagniated and filtered archive list
     * @param archiveName
     * @param status
     * @param categoryId
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SerArchive> getArchiveListByPage(String sortBy, String order, String archiveName, String templateName, String status, Long categoryId, int currentPage, int perPage);

    /**
     * check if archive exist
     * @param archiveId
     * @return
     */
    boolean checkArchiveExist(long archiveId);

    /**
     * check if archivename exist
     * @param archiveName
     * @param archiveId
     * @return
     */
    boolean checkArchiveNameExist(String archiveName, Long archiveId);

    /**
     * check if archivenumber exist
     * @param archiveNumber
     * @param archiveId
     * @return
     */
    boolean checkArchiveNumberExist(String archiveNumber, Long archiveId);

    /**
     * check if archive template exist
     * @param archiveTemplateId
     * @return
     */
    boolean checkArchiveTemplateExist(long archiveTemplateId);

    /**
     * check if device exists
     * @param archiveId
     * @return
     */
    boolean checkDeviceExist(long archiveId);

    /**
     * update archive status
     * @param archiveId
     * @param status
     */
    void updateStatus(long archiveId, String status);

    /**
     * create new archive
     * @param portraitFile
     * @param serArchive
     * @param json
     */
    void createSerArchive(MultipartFile portraitFile, SerArchive serArchive, String json);

    /**
     * edit archive
     * @param portraitFile
     * @param serArchive
     * @param json
     */
    void modifySerArchive(MultipartFile portraitFile, SerArchive serArchive, String json);

    /**
     * remove archive
     * @param archiveId
     */
    boolean removeSerArchive(long archiveId);

    /**
     * find all archive
     * @return
     */
    List<SerArchive> findAll();

    /**
     * get fitered archive export list
     * @param archiveName
     * @param status
     * @param categoryId
     * @param isAll
     * @param idList
     * @return
     */
    List<SerArchive> getExportListByFilter(String sortBy, String order, String archiveName, String templateName, String status, Long categoryId, boolean isAll, String idList);



}
