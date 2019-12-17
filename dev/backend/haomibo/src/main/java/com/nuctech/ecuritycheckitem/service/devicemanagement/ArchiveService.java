package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SerArchive;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArchiveService {
    PageResult<SerArchive> getArchiveListByPage(String archiveName, String status, Long categoryId, int currentPage, int perPage);

    boolean checkArchiveExist(long archiveId);

    boolean checkArchiveNameExist(String archiveName, Long archiveId);

    boolean checkArchiveNumberExist(String archiveNumber, Long archiveId);

    boolean checkArchiveTemplateExist(long archiveTemplateId);

    boolean checkDeviceExist(long archiveId);

    void updateStatus(long archiveId, String status);

    void createSerArchive(MultipartFile portraitFile, SerArchive serArchive, String json);

    void modifySerArchive(MultipartFile portraitFile, SerArchive serArchive, String json);

    void removeSerArchive(long archiveId);

    List<SerArchive> findAll();

    List<SerArchive> getExportListByFilter(String archiveName, String status, Long categoryId, boolean isAll, String idList);



}
