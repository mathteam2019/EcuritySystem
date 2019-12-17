package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SerArchiveIndicators;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveTemplate;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArchiveTemplateService {
    PageResult<SerArchiveTemplate> getArchiveTemplateListByPage(String archiveName, String status, Long categoryId, int currentPage, int perPage);

    boolean checkArchiveExist(long archiveTemplateId);

    boolean checkArchiveTemplateExist(long archiveTemplateId);

    boolean checkTemplateNameExist(String templateName, Long templateId);

    boolean checkTemplateNumberExist(String templateNumber, Long templateId);

    boolean checkCategoryExist(long categoryId);

    void updateStatus(long templateId, String status);

    void createArchiveIndicator(SerArchiveIndicators archiveIndicators);

    int updateIndicatorStatus(long indicatorId, String isNull);

    void createSerArchiveTemplate(SerArchiveTemplate serArchiveTemplate);

    void modifySerArchiveTemplate(SerArchiveTemplate serArchiveTemplate);

    void removeSerArchiveTemplate(long archiveTemplateId);

    int removeSerArchiveIndicator(long archiveIndicatorId);

    List<SerArchiveTemplate> findAll();

    List<SerArchiveTemplate> getExportListByFilter(String archiveName, String status, Long categoryId, boolean isAll, String idList);
}
