package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ArchiveTemplateServiceImpl implements ArchiveTemplateService{


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
        return predicate;
    }

    @Override
    public PageResult<SerArchiveTemplate> getArchiveTemplateListByPage(String templateName, String status, Long categoryId, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(templateName, status, categoryId);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serArchiveTemplateRepository.count(predicate);
        List<SerArchiveTemplate> data = serArchiveTemplateRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<SerArchiveTemplate>(total, data);
    }

    @Override
    public boolean checkArchiveExist(long archiveTemplateId) {
        return serArchiveRepository.exists(QSerArchive.
                serArchive.archivesTemplateId.eq(archiveTemplateId));
    }

    @Override
    public boolean checkArchiveTemplateExist(long archiveTemplateId) {
        if (!serArchiveTemplateRepository.exists(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(archiveTemplateId))) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkCategoryExist(long categoryId) {
        return sysDeviceCategoryRepository.exists(QSysDeviceCategory.
                sysDeviceCategory.categoryId.eq(categoryId));
    }


    @Override
    @Transactional
    public void updateStatus(long templateId, String status) {
        Optional<SerArchiveTemplate> optionalSerArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.
                serArchiveTemplate.archivesTemplateId.eq(templateId));
        SerArchiveTemplate serArchiveTemplate = optionalSerArchiveTemplate.get();

        // Update status.
        serArchiveTemplate.setStatus(status);

        // Add edited info.
        serArchiveTemplate.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);
    }

    @Override
    @Transactional
    public void createArchiveIndicator(SerArchiveIndicators archiveIndicators) {
        archiveIndicators.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        serArchiveIndicatorsRepository.save(archiveIndicators);
    }

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
        if(archiveTemplateId != null && checkArchiveExist(archiveTemplateId)) {
            return 1;

        }

        // Update status.
        serArchiveIndicators.setIsNull(isNull);

        // Add edited info.
        serArchiveIndicators.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveIndicatorsRepository.save(serArchiveIndicators);
        return 2;
    }


    @Override
    @Transactional
    public void createSerArchiveTemplate(SerArchiveTemplate serArchiveTemplate) {

        // Add createdInfo.
        serArchiveTemplate.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        if(serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for(int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i ++) {
                SerArchiveIndicators archiveIndicators = serArchiveTemplate.getArchiveIndicatorsList().get(i);
                archiveIndicators.setArchivesTemplateId(serArchiveTemplate.getArchivesTemplateId());
                archiveIndicators.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                serArchiveIndicatorsRepository.save(serArchiveTemplate.getArchiveIndicatorsList().get(i));
            }
        }
    }

    @Override
    @Transactional
    public void modifySerArchiveTemplate(SerArchiveTemplate serArchiveTemplate) {

        SerArchiveTemplate oldSerArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(serArchiveTemplate.getArchivesTemplateId())).orElse(null);
        serArchiveTemplate.setCreatedBy(oldSerArchiveTemplate.getCreatedBy());
        serArchiveTemplate.setCreatedTime(oldSerArchiveTemplate.getCreatedTime());



        // Add editInfo.
        serArchiveTemplate.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        if(serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for(int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i ++) {
                SerArchiveIndicators archiveIndicators = serArchiveTemplate.getArchiveIndicatorsList().get(i);
                //get indicator from it's indicator id
                SerArchiveIndicators oldArchiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.serArchiveIndicators
                        .indicatorsId.eq(archiveIndicators.getIndicatorsId())).orElse(null);
                if(oldArchiveIndicators != null) {
                    oldArchiveIndicators.setArchivesTemplateId(serArchiveTemplate.getArchivesTemplateId());
                    oldArchiveIndicators.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                    serArchiveIndicatorsRepository.save(oldArchiveIndicators);
                }

            }
        }
    }

    @Override
    @Transactional
    public void removeSerArchiveTemplate(long archiveTemplateId) {
        SerArchiveTemplate serArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(archiveTemplateId)).orElse(null);

        //remove it's indicators
        if(serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for(int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i ++) {
                SerArchiveIndicators archiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.serArchiveIndicators
                        .indicatorsId.eq(serArchiveTemplate.getArchiveIndicatorsList().get(i).getIndicatorsId())).orElse(null);
                if(archiveIndicators != null) {
                    serArchiveIndicatorsRepository.delete(archiveIndicators);
                }
            }
        }

        serArchiveTemplateRepository.delete(serArchiveTemplate);
    }

    @Override
    @Transactional
    public int removeSerArchiveIndicator(long archiveIndicatorId) {
        SerArchiveIndicators serArchiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.serArchiveIndicators
                .indicatorsId.eq(archiveIndicatorId)).orElse(null);

        //check indicators exist or not
        if(serArchiveIndicators == null) {
            return 0;

        }

        Long archiveTemplateId = serArchiveIndicators.getArchivesTemplateId();
        if(checkArchiveExist(archiveTemplateId)) {
            return 1;

        }
        serArchiveIndicatorsRepository.delete(serArchiveIndicators);
        return 2;
    }

    @Override
    public List<SerArchiveTemplate> findAll() {
        return serArchiveTemplateRepository.findAll();
    }

    private List<SerArchiveTemplate> getExportList(List<SerArchiveTemplate> archiveTemplateList, boolean isAll, String idList) {
        List<SerArchiveTemplate> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < archiveTemplateList.size(); i ++) {
                SerArchiveTemplate archiveTemplate = archiveTemplateList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(archiveTemplate.getArchivesTemplateId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(archiveTemplate);
                }
            }
        } else {
            exportList = archiveTemplateList;
        }
        return exportList;
    }



    @Override
    public List<SerArchiveTemplate> getExportListByFilter(String templateName, String status, Long categoryId, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(templateName, status, categoryId);

        //get all archive list
        List<SerArchiveTemplate> archiveTemplateList = StreamSupport
                .stream(serArchiveTemplateRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SerArchiveTemplate> exportList = getExportList(archiveTemplateList, isAll, idList);
        return exportList;
    }

}
