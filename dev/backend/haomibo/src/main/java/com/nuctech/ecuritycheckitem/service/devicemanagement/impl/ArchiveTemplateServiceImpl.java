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


import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.QSerArchiveTemplate;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveTemplate;
import com.nuctech.ecuritycheckitem.models.db.QSerArchive;
import com.nuctech.ecuritycheckitem.models.db.QSysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveIndicators;
import com.nuctech.ecuritycheckitem.models.db.QSerArchiveIndicators;

import com.nuctech.ecuritycheckitem.repositories.SerArchiveRepository;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveTemplateRepository;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveIndicatorsRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceCategoryRepository;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.devicemanagement.ArchiveTemplateService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        return predicate;
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

        // Update status.
        serArchiveTemplate.setStatus(status);

        // Add edited info.
        serArchiveTemplate.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);
    }

    /**
     * create new archive indicator
     * @param archiveIndicators
     */
    @Override
    @Transactional
    public void createArchiveIndicator(SerArchiveIndicators archiveIndicators) {
        archiveIndicators.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        serArchiveIndicatorsRepository.save(archiveIndicators);
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

        // Update status.
        serArchiveIndicators.setIsNull(isNull);

        // Add edited info.
        serArchiveIndicators.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveIndicatorsRepository.save(serArchiveIndicators);
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
        serArchiveTemplate.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        if (serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for (int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i++) {
                SerArchiveIndicators archiveIndicators = serArchiveTemplate.getArchiveIndicatorsList().get(i);
                archiveIndicators.setArchivesTemplateId(serArchiveTemplate.getArchivesTemplateId());
                archiveIndicators.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                serArchiveIndicatorsRepository.save(serArchiveTemplate.getArchiveIndicatorsList().get(i));
            }
        }
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
        serArchiveTemplate.setCreatedBy(oldSerArchiveTemplate.getCreatedBy());
        serArchiveTemplate.setCreatedTime(oldSerArchiveTemplate.getCreatedTime());


        // Add editInfo.
        serArchiveTemplate.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        if (serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for (int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i++) {
                SerArchiveIndicators archiveIndicators = serArchiveTemplate.getArchiveIndicatorsList().get(i);
                //get indicator from it's indicator id
                SerArchiveIndicators oldArchiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.serArchiveIndicators
                        .indicatorsId.eq(archiveIndicators.getIndicatorsId())).orElse(null);
                if (oldArchiveIndicators != null) {
                    oldArchiveIndicators.setArchivesTemplateId(serArchiveTemplate.getArchivesTemplateId());
                    oldArchiveIndicators.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                    serArchiveIndicatorsRepository.save(oldArchiveIndicators);
                }

            }
        }
    }

    /**
     * remove ser archive template
     * @param archiveTemplateId
     */
    @Override
    @Transactional
    public void removeSerArchiveTemplate(long archiveTemplateId) {
        SerArchiveTemplate serArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(archiveTemplateId)).orElse(null);

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
        if (checkArchiveExist(archiveTemplateId)) {
            return 1;

        }
        serArchiveIndicatorsRepository.delete(serArchiveIndicators);
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
            sort = new Sort(Sort.Direction.ASC, sortBy);
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = new Sort(Sort.Direction.DESC, sortBy);
            }
        }
        //get all archive list
        List<SerArchiveTemplate> archiveTemplateList;
        if(sort != null) {
            archiveTemplateList = StreamSupport
                    .stream(serArchiveTemplateRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            archiveTemplateList = StreamSupport
                    .stream(serArchiveTemplateRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        List<SerArchiveTemplate> exportList = getExportList(archiveTemplateList, isAll, idList);
        return exportList;
    }

}
