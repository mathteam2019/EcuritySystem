/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ArchiveServiceImpl）
 * 文件名：	ArchiveServiceImpl.java
 * 描述：	Archive Service implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */


package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.QSerArchive;
import com.nuctech.ecuritycheckitem.models.db.SerArchive;
import com.nuctech.ecuritycheckitem.models.db.QSerArchiveTemplate;
import com.nuctech.ecuritycheckitem.models.db.QSysDevice;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveValue;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveRepository;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveTemplateRepository;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveValueRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceRepository;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.devicemanagement.ArchiveService;

import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;

import com.querydsl.core.BooleanBuilder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    @Autowired
    SerArchiveRepository serArchiveRepository;

    @Autowired
    SerArchiveTemplateRepository serArchiveTemplateRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    Utils utils;

    @Autowired
    SerArchiveValueRepository serArchiveValueRepository;

    @Autowired
    SysDeviceRepository sysDeviceRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.ENGLISH;


    /**
     * get predicate from filter parameters
     * @param archiveName
     * @param status
     * @param categoryId
     * @return
     */
    private BooleanBuilder getPredicate(String archiveName, String status, Long categoryId) {
        QSerArchive builder = QSerArchive.serArchive;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(archiveName)) {
            predicate.and(builder.archivesName.contains(archiveName));
        }
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.status.eq(status));
        }
        if (categoryId != null) {
            predicate.and(builder.archiveTemplate.deviceCategory.categoryId.eq(categoryId));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        return predicate;
    }

    /**
     * get paginated and filtered result
     * @param archiveName
     * @param status
     * @param categoryId
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SerArchive> getArchiveListByPage(String sortBy, String order, String archiveName, String status, Long categoryId, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(archiveName, status, categoryId);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }

        long total = serArchiveRepository.count(predicate);
        List<SerArchive> data = serArchiveRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<SerArchive>(total, data);
    }

    /**
     * check if archive exists
     * @param archiveId
     * @return
     */
    @Override
    public boolean checkArchiveExist(long archiveId) {
        return serArchiveRepository.exists(QSerArchive.
                serArchive.archiveId.eq(archiveId));
    }

    /**
     * check if archive name exist
     * @param archiveName
     * @param archiveId
     * @return
     */
    @Override
    public boolean checkArchiveNameExist(String archiveName, Long archiveId) {
        if(archiveId == null) {
            return serArchiveRepository.exists(QSerArchive.serArchive.archivesName.eq(archiveName));
        }
        return serArchiveRepository.exists(QSerArchive.serArchive.archivesName.eq(archiveName)
                .and(QSerArchive.serArchive.archiveId.ne(archiveId)));
    }

    /**
     * check if archive number exists
     * @param archiveNumber
     * @param archiveId
     * @return
     */
    @Override
    public boolean checkArchiveNumberExist(String archiveNumber, Long archiveId) {
        if(archiveId == null) {
            return serArchiveRepository.exists(QSerArchive.serArchive.archivesNumber.eq(archiveNumber));
        }
        return serArchiveRepository.exists(QSerArchive.serArchive.archivesNumber.eq(archiveNumber)
                .and(QSerArchive.serArchive.archiveId.ne(archiveId)));
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
     * check if device exists
     * @param archiveId
     * @return
     */
    @Override
    public boolean checkDeviceExist(long archiveId) {
        return sysDeviceRepository.exists(QSysDevice.
                sysDevice.archiveId.eq(archiveId));
    }

    public String getJsonFromArchive(SerArchive archive) {
        SerArchive newArchive = SerArchive.builder()
                .archiveId(archive.getArchiveId())
                .archivesTemplateId(archive.getArchivesTemplateId())
                .archivesName(archive.getArchivesName())
                .archivesNumber(archive.getArchivesNumber())
                .status(archive.getStatus())
                .imageUrl(archive.getImageUrl())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newArchive);
        } catch(Exception ex) {
        }
        return answer;
    }

    /**
     * update archive status
     * @param archiveId
     * @param status
     */
    @Override
    @Transactional
    public void updateStatus(long archiveId, String status) {
        Optional<SerArchive> optionalSerArchive = serArchiveRepository.findOne(QSerArchive.
                serArchive.archiveId.eq(archiveId));
        SerArchive serArchive = optionalSerArchive.get();
        String valueBefore = getJsonFromArchive(serArchive);

        // Update status.
        serArchive.setStatus(status);

        // Add edited info.
        serArchive.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveRepository.save(serArchive);
        String valueAfter = getJsonFromArchive(serArchive);
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Archive", null, currentLocale), "", serArchive.getArchiveId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * create archive value
     * @param json
     * @param serArchive
     */
    private void createArchiveValue(String json, SerArchive serArchive) {
        ObjectMapper mapper = new ObjectMapper();
        serArchive.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        serArchiveRepository.save(serArchive);
        try {
            SerArchive jsonArchive = mapper.readValue(json, SerArchive.class);
            List<SerArchiveValue> archiveValueList = jsonArchive.getArchiveValueList();
            if(archiveValueList != null) {
                for(int i = 0; i < archiveValueList.size(); i ++) {
                    SerArchiveValue archiveValue = archiveValueList.get(i);
                    archiveValue.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                    archiveValue.setArchiveId(serArchive.getArchiveId());
                    serArchiveValueRepository.save(archiveValue);
                }
            }
            String valueBefore = "";
            String valueAfter = getJsonFromArchive(serArchive);
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale), "", serArchive.getArchiveId().toString(), null, true, valueBefore, valueAfter);
        }catch(Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * create serArchive
     * @param portraitFile
     * @param serArchive
     * @param json
     */
    @Override
    @Transactional
    public void createSerArchive(MultipartFile portraitFile, SerArchive serArchive, String json) {

        serArchive.setImageUrl(utils.saveImageFile(portraitFile));

        // Add created info.
        serArchive.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveRepository.save(serArchive);

        createArchiveValue(json, serArchive);
    }

    /**
     * modify SerArchive
     * @param portraitFile
     * @param serArchive
     * @param json
     */
    @Override
    @Transactional
    public void modifySerArchive(MultipartFile portraitFile, SerArchive serArchive, String json) {

        String fileName = utils.saveImageFile(portraitFile);
        if(!fileName.equals("")) {
            serArchive.setImageUrl(fileName);
        }


        // Add edit info.
        serArchive.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        SerArchive oldSerArchive = serArchiveRepository.findOne(QSerArchive.serArchive
                .archiveId.eq(serArchive.getArchiveId())).orElse(null);
        String valueBefore = getJsonFromArchive(oldSerArchive);
        serArchive.setCreatedBy(oldSerArchive.getCreatedBy());
        serArchive.setCreatedTime(oldSerArchive.getCreatedTime());
        serArchive.setStatus(oldSerArchive.getStatus());

        //remove original indicators value
        if(oldSerArchive.getArchiveValueList() != null) {
            for(int i = 0; i < oldSerArchive.getArchiveValueList().size(); i ++) {
                SerArchiveValue archiveValue = oldSerArchive.getArchiveValueList().get(i);
                serArchiveValueRepository.delete(archiveValue);
            }
        }

        //add new indicators value
        createArchiveValue(json, serArchive);
        serArchiveRepository.save(serArchive);

        String valueAfter = getJsonFromArchive(serArchive);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Archive", null, currentLocale), "", serArchive.getArchiveId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * remove SerArchive
     * @param archiveId
     */
    @Override
    @Transactional
    public boolean removeSerArchive(long archiveId) {
        SerArchive serArchive = serArchiveRepository.findOne(QSerArchive.serArchive
                .archiveId.eq(archiveId)).orElse(null);
        if(serArchive != null) {
            if(serArchive.getStatus().equals(SerArchive.Status.ACTIVE)) {
                return false;
            }
            String valueBefore = getJsonFromArchive(serArchive);
            String valueAfter = "";
            String archiveIdStr = serArchive.getArchiveId().toString();
            if(serArchive.getArchiveValueList() != null) {
                for(int i = 0; i < serArchive.getArchiveValueList().size(); i ++) {
                    serArchiveValueRepository.delete(serArchive.getArchiveValueList().get(i));
                }
            }

            serArchiveRepository.delete(serArchive);
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale), "", archiveIdStr, null, true, valueBefore, valueAfter);
            return true;
        }
        return false;

    }

    /**
     * find all Archives
     * @return
     */
    @Override
    public List<SerArchive> findAll() {
        QSerArchive builder = QSerArchive.serArchive;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.status.eq(SerArchive.Status.ACTIVE));

        return StreamSupport
                .stream(serArchiveRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * get export list of archives
     * @param archiveList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SerArchive> getExportList(List<SerArchive> archiveList, boolean isAll, String idList) {
        List<SerArchive> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < archiveList.size(); i ++) {
                SerArchive archive = archiveList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(archive.getArchiveId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(archive);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < archiveList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(archiveList.get(i));
            }
        }
        return exportList;
    }

    /**
     * get filtered archive list
     * @param archiveName
     * @param status
     * @param categoryId
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SerArchive> getExportListByFilter(String sortBy, String order, String archiveName, String status, Long categoryId, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(archiveName, status, categoryId);

        //get all archive list
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = new Sort(Sort.Direction.ASC, sortBy);
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = new Sort(Sort.Direction.DESC, sortBy);
            }
        }
        String[] splits = idList.split(",");
        List<Long> archiveIdList = new ArrayList<>();
        for(String idStr: splits) {
            archiveIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QSerArchive.serArchive.archiveId.in(archiveIdList));
        List<SerArchive> archiveList;
        if(sort != null) {
            archiveList = StreamSupport
                    .stream(serArchiveRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            archiveList = StreamSupport
                    .stream(serArchiveRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        //List<SerArchive> exportList = getExportList(archiveList, isAll, idList);
        return archiveList;
    }
}
