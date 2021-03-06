/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ArchiveTemplateServiceImpl）
 * 文件名：	ArchiveTemplateServiceImpl.java
 * 描述：	ArchiveTemplateService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.QSerArchiveTemplate;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveTemplate;
import com.nuctech.ecuritycheckitem.models.db.QSerArchive;
import com.nuctech.ecuritycheckitem.models.db.QSysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveIndicators;
import com.nuctech.ecuritycheckitem.models.db.QSerArchiveIndicators;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveRepository;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveTemplateRepository;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveIndicatorsRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceCategoryRepository;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.devicemanagement.ArchiveTemplateService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ArchiveTemplateServiceImpl implements ArchiveTemplateService {


    @Autowired
    SerArchiveRepository serArchiveRepository;

    @Autowired
    SerArchiveTemplateRepository serArchiveTemplateRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SerArchiveIndicatorsRepository serArchiveIndicatorsRepository;

    @Autowired
    SysDeviceCategoryRepository sysDeviceCategoryRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.CHINESE;

    public static String defaultSort = "archivesTemplateNumber";

    /**
     * get prediate from filter parameters
     * @param templateName
     * @param status
     * @param categoryId
     * @return
     */
    private BooleanBuilder getPredicate(String templateName, String status, Long categoryId) {
        QSerArchiveTemplate builder = QSerArchiveTemplate.serArchiveTemplate;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(templateName)) {
            predicate.and(builder.templateName.contains(templateName));
        }
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.status.eq(status));
        }
        if (categoryId != null) {
            predicate.and(builder.deviceCategory.categoryId.eq(categoryId));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        return predicate;
    }

    public String getJsonFromArchiveTemplate(SerArchiveTemplate archiveTemplate) {
        SerArchiveTemplate newArchiveTemplate = SerArchiveTemplate.builder()
                .templateName(archiveTemplate.getTemplateName())
                .archivesTemplateId(archiveTemplate.getArchivesTemplateId())
                .archivesTemplateNumber(archiveTemplate.getArchivesTemplateNumber())
                .categoryId(archiveTemplate.getCategoryId())
                .status(archiveTemplate.getStatus())
                .manufacturer(archiveTemplate.getManufacturer())
                .originalModel(archiveTemplate.getOriginalModel())
                .status(archiveTemplate.getStatus())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newArchiveTemplate);
        } catch(Exception ex) {
        }
        return answer;
    }

    public String getJsonFromArchiveIndicator(SerArchiveIndicators archiveIndicators) {
        SerArchiveIndicators newArchiveIndicator = SerArchiveIndicators.builder()
                .indicatorsId(archiveIndicators.getIndicatorsId())
                .archivesTemplateId(archiveIndicators.getArchivesTemplateId())
                .indicatorsName(archiveIndicators.getIndicatorsName())
                .indicatorsUnit(archiveIndicators.getIndicatorsUnit())
                .isNull(archiveIndicators.getIsNull())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newArchiveIndicator);
        } catch(Exception ex) {
        }
        return answer;
    }

    /**
     * get paginated and filtered archive template list
     * @param templateName
     * @param status
     * @param categoryId
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SerArchiveTemplate> getArchiveTemplateListByPage(String sortBy, String order, String templateName, String status, Long categoryId, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(templateName, status, categoryId);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by(defaultSort).ascending());
        }

        long total = serArchiveTemplateRepository.count(predicate);
        List<SerArchiveTemplate> data = serArchiveTemplateRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<SerArchiveTemplate>(total, data);
    }

    /**
     * check if archive exists
     * @param archiveTemplateId
     * @return
     */
    @Override
    public boolean checkArchiveExist(long archiveTemplateId) {
        return serArchiveRepository.exists(QSerArchive.
                serArchive.archivesTemplateId.eq(archiveTemplateId));
    }

    /**
     * check if archive template name exists
     * @param templateName
     * @param templateId
     * @return
     */
    @Override
    public boolean checkTemplateNameExist(String templateName, Long templateId) {
        if (templateId == null) {
            return serArchiveTemplateRepository.exists(QSerArchiveTemplate.serArchiveTemplate.templateName.eq(templateName));
        }
        return serArchiveTemplateRepository.exists(QSerArchiveTemplate.serArchiveTemplate.templateName.eq(templateName)
                .and(QSerArchiveTemplate.serArchiveTemplate.archivesTemplateId.ne(templateId)));
    }

    /**
     * chweck if archive template number exists
     * @param templateNumber
     * @param templateId
     * @return
     */
    @Override
    public boolean checkTemplateNumberExist(String templateNumber, Long templateId) {
        if (templateId == null) {
            return serArchiveTemplateRepository.exists(QSerArchiveTemplate.serArchiveTemplate.archivesTemplateNumber.eq(templateNumber));
        }
        return serArchiveTemplateRepository.exists(QSerArchiveTemplate.serArchiveTemplate.archivesTemplateNumber.eq(templateNumber)
                .and(QSerArchiveTemplate.serArchiveTemplate.archivesTemplateId.ne(templateId)));
    }

    /**
     * check if archive template exists
     * @param archiveTemplateId
     * @return
     */
    @Override
    public boolean checkArchiveTemplateExist(long archiveTemplateId) {
        if (!serArchiveTemplateRepository.exists(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(archiveTemplateId))) {
            return false;
        }
        return true;
    }

    /**
     * check if device category exists
     * @param categoryId
     * @return
     */
    @Override
    public boolean checkCategoryExist(long categoryId) {
        return sysDeviceCategoryRepository.exists(QSysDeviceCategory.
                sysDeviceCategory.categoryId.eq(categoryId));
    }

    /**
     * check if archive template id exists
     * @param templateId
     * @param status
     */
    @Override
    @Transactional
    public void updateStatus(long templateId, String status) {
        Optional<SerArchiveTemplate> optionalSerArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.
                serArchiveTemplate.archivesTemplateId.eq(templateId));
        SerArchiveTemplate serArchiveTemplate = optionalSerArchiveTemplate.get();
        String valueBefore = getJsonFromArchiveTemplate(serArchiveTemplate);

        // Update status.
        serArchiveTemplate.setStatus(status);

        // Add edited info.
        serArchiveTemplate.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        String valueAfter = getJsonFromArchiveTemplate(serArchiveTemplate);

        serArchiveTemplateRepository.save(serArchiveTemplate);
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("ArchiveTemplate", null, currentLocale), "", serArchiveTemplate.getArchivesTemplateId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * create new archive indicator
     * @param archiveIndicators
     */
    @Override
    @Transactional
    public void createArchiveIndicator(SerArchiveIndicators archiveIndicators) {
        archiveIndicators.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        serArchiveIndicatorsRepository.save(archiveIndicators);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("ArchiveIndicator", null, currentLocale), "", archiveIndicators.getIndicatorsId().toString(), null,
                true, "", getJsonFromArchiveIndicator(archiveIndicators));
    }

    /**
     * update archive indicator status
     * @param indicatorId
     * @param isNull
     * @return
     */
    @Override
    @Transactional
    public int updateIndicatorStatus(long indicatorId, String isNull) {
        Optional<SerArchiveIndicators> optionalSerArchiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.
                serArchiveIndicators.indicatorsId.eq(indicatorId));
        if (!optionalSerArchiveIndicators.isPresent()) {
            return 0;

        }

        SerArchiveIndicators serArchiveIndicators = optionalSerArchiveIndicators.get();

        Long archiveTemplateId = serArchiveIndicators.getArchivesTemplateId();
        if (archiveTemplateId != null && checkArchiveExist(archiveTemplateId)) {
            return 1;

        }
        String valueBefore = getJsonFromArchiveIndicator(serArchiveIndicators);

        // Update status.
        serArchiveIndicators.setIsNull(isNull);

        // Add edited info.
        serArchiveIndicators.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        String valueAfter = getJsonFromArchiveIndicator(serArchiveIndicators);

        serArchiveIndicatorsRepository.save(serArchiveIndicators);
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("ArchiveIndicator", null, currentLocale), "", String.valueOf(indicatorId), null,
                true, valueBefore, valueAfter);
        return 2;
    }

    /**
     * create ser archive template
     * @param serArchiveTemplate
     */
    @Override
    @Transactional
    public void createSerArchiveTemplate(SerArchiveTemplate serArchiveTemplate) {

        // Add createdInfo.
        serArchiveTemplate.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        if (serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for (int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i++) {
                SerArchiveIndicators archiveIndicators = serArchiveTemplate.getArchiveIndicatorsList().get(i);
                archiveIndicators.setArchivesTemplateId(serArchiveTemplate.getArchivesTemplateId());
                archiveIndicators.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
                serArchiveIndicatorsRepository.save(serArchiveTemplate.getArchiveIndicatorsList().get(i));
            }
        }
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("ArchiveTemplate", null, currentLocale), "", serArchiveTemplate.getArchivesTemplateId().toString(), null,
                true, "", getJsonFromArchiveTemplate(serArchiveTemplate));
    }

    /**
     * modify archive template
     * @param serArchiveTemplate
     */
    @Override
    @Transactional
    public void modifySerArchiveTemplate(SerArchiveTemplate serArchiveTemplate) {

        SerArchiveTemplate oldSerArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(serArchiveTemplate.getArchivesTemplateId())).orElse(null);
        String valueBefore = getJsonFromArchiveTemplate(oldSerArchiveTemplate);
        serArchiveTemplate.setCreatedBy(oldSerArchiveTemplate.getCreatedBy());
        serArchiveTemplate.setCreatedTime(oldSerArchiveTemplate.getCreatedTime());
        serArchiveTemplate.setStatus(oldSerArchiveTemplate.getStatus());


        // Add editInfo.
        serArchiveTemplate.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        if (serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for (int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i++) {
                SerArchiveIndicators archiveIndicators = serArchiveTemplate.getArchiveIndicatorsList().get(i);
                //get indicator from it's indicator id
                SerArchiveIndicators oldArchiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.serArchiveIndicators
                        .indicatorsId.eq(archiveIndicators.getIndicatorsId())).orElse(null);
                if (oldArchiveIndicators != null) {
                    oldArchiveIndicators.setArchivesTemplateId(serArchiveTemplate.getArchivesTemplateId());
                    oldArchiveIndicators.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
                    serArchiveIndicatorsRepository.save(oldArchiveIndicators);
                }

            }
        }
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("ArchiveTemplate", null, currentLocale), "", serArchiveTemplate.getArchivesTemplateId().toString(), null,
                true, valueBefore, getJsonFromArchiveTemplate(serArchiveTemplate));
    }

    /**
     * remove ser archive template
     * @param archiveTemplateId
     */
    @Override
    @Transactional
    public boolean removeSerArchiveTemplate(long archiveTemplateId) {
        SerArchiveTemplate serArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(archiveTemplateId)).orElse(null);
        if(serArchiveTemplate.getStatus().equals(SerArchiveTemplate.Status.ACTIVE)) {
            return false;
        }

        String valueBefore = getJsonFromArchiveTemplate(serArchiveTemplate);
        //remove it's indicators
        if (serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for (int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i++) {
                SerArchiveIndicators archiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.serArchiveIndicators
                        .indicatorsId.eq(serArchiveTemplate.getArchiveIndicatorsList().get(i).getIndicatorsId())).orElse(null);
                if (archiveIndicators != null) {
                    serArchiveIndicatorsRepository.delete(archiveIndicators);
                }
            }
        }

        serArchiveTemplateRepository.delete(serArchiveTemplate);
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("ArchiveTemplate", null, currentLocale), "", serArchiveTemplate.getArchivesTemplateId().toString(), null,
                true, valueBefore, "");
        return true;
    }

    /**
     * remove ser archive indicator
     * @param archiveIndicatorId
     * @return
     */
    @Override
    @Transactional
    public int removeSerArchiveIndicator(long archiveIndicatorId) {
        SerArchiveIndicators serArchiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.serArchiveIndicators
                .indicatorsId.eq(archiveIndicatorId)).orElse(null);

        //check indicators exist or not
        if (serArchiveIndicators == null) {
            return 0;

        }

        Long archiveTemplateId = serArchiveIndicators.getArchivesTemplateId();
        if (archiveTemplateId != null && checkArchiveExist(archiveTemplateId)) {
            return 1;

        }
        String valueBefore = getJsonFromArchiveIndicator(serArchiveIndicators);
        serArchiveIndicatorsRepository.delete(serArchiveIndicators);
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("ArchiveTemplate", null, currentLocale), "", String.valueOf(archiveIndicatorId), null,
                true, valueBefore, "");
        return 2;
    }

    /**
     * find all ser archive templates
     * @return
     */
    @Override
    public List<SerArchiveTemplate> findAll() {
        QSerArchiveTemplate builder = QSerArchiveTemplate.serArchiveTemplate;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.status.eq(SerArchiveTemplate.Status.ACTIVE));

        return StreamSupport
                .stream(serArchiveTemplateRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * get export list of archive template list
     * @param archiveTemplateList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SerArchiveTemplate> getExportList(List<SerArchiveTemplate> archiveTemplateList, boolean isAll, String idList) {
        List<SerArchiveTemplate> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < archiveTemplateList.size(); i++) {
                SerArchiveTemplate archiveTemplate = archiveTemplateList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(archiveTemplate.getArchivesTemplateId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(archiveTemplate);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < archiveTemplateList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(archiveTemplateList.get(i));
            }
        }
        return exportList;
    }

    /**
     * get archive template export list with filter parameters
     * @param templateName
     * @param status
     * @param categoryId
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SerArchiveTemplate> getExportListByFilter(String sortBy, String order, String templateName, String status, Long categoryId, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(templateName, status, categoryId);
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by(defaultSort).ascending();
        }

        if(isAll == false) {
            String[] splits = idList.split(",");
            List<Long> templateIdList = new ArrayList<>();
            for(String idStr: splits) {
                templateIdList.add(Long.valueOf(idStr));
            }
            predicate.and(QSerArchiveTemplate.serArchiveTemplate.archivesTemplateId.in(templateIdList));
        }
        //get all archive list
        List<SerArchiveTemplate> archiveTemplateList;
        archiveTemplateList = StreamSupport
                .stream(serArchiveTemplateRepository.findAll(predicate, sort).spliterator(), false)
                .collect(Collectors.toList());


        //List<SerArchiveTemplate> exportList = getExportList(archiveTemplateList, isAll, idList);
        return archiveTemplateList;
    }

}
