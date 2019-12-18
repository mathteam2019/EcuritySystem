package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveRepository;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveTemplateRepository;
import com.nuctech.ecuritycheckitem.repositories.SerArchiveValueRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.devicemanagement.ArchiveService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
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
        return predicate;
    }

    @Override
    public PageResult<SerArchive> getArchiveListByPage(String archiveName, String status, Long categoryId, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(archiveName, status, categoryId);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serArchiveRepository.count(predicate);
        List<SerArchive> data = serArchiveRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<SerArchive>(total, data);
    }

    @Override
    public boolean checkArchiveExist(long archiveId) {
        return serArchiveRepository.exists(QSerArchive.
                serArchive.archiveId.eq(archiveId));
    }

    @Override
    public boolean checkArchiveNameExist(String archiveName, Long archiveId) {
        if(archiveId == null) {
            return serArchiveRepository.exists(QSerArchive.serArchive.archivesName.eq(archiveName));
        }
        return serArchiveRepository.exists(QSerArchive.serArchive.archivesName.eq(archiveName)
                .and(QSerArchive.serArchive.archiveId.ne(archiveId)));
    }

    @Override
    public boolean checkArchiveNumberExist(String archiveNumber, Long archiveId) {
        if(archiveId == null) {
            return serArchiveRepository.exists(QSerArchive.serArchive.archivesNumber.eq(archiveNumber));
        }
        return serArchiveRepository.exists(QSerArchive.serArchive.archivesNumber.eq(archiveNumber)
                .and(QSerArchive.serArchive.archiveId.ne(archiveId)));
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
    public boolean checkDeviceExist(long archiveId) {
        return sysDeviceRepository.exists(QSysDevice.
                sysDevice.archiveId.eq(archiveId));
    }

    @Override
    @Transactional
    public void updateStatus(long archiveId, String status) {
        Optional<SerArchive> optionalSerArchive = serArchiveRepository.findOne(QSerArchive.
                serArchive.archiveId.eq(archiveId));
        SerArchive serArchive = optionalSerArchive.get();

        // Update status.
        serArchive.setStatus(status);

        // Add edited info.
        serArchive.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveRepository.save(serArchive);
    }

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
        }catch(Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    @Transactional
    public void createSerArchive(MultipartFile portraitFile, SerArchive serArchive, String json) {

        serArchive.setImageUrl(utils.saveImageFile(portraitFile));

        // Add created info.
        serArchive.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveRepository.save(serArchive);

        createArchiveValue(json, serArchive);
    }

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

        serArchive.setCreatedBy(oldSerArchive.getCreatedBy());
        serArchive.setCreatedTime(oldSerArchive.getCreatedTime());;

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
    }

    @Override
    @Transactional
    public void removeSerArchive(long archiveId) {
        SerArchive serArchive = serArchiveRepository.findOne(QSerArchive.serArchive
                .archiveId.eq(archiveId)).orElse(null);
        if(serArchive.getArchiveValueList() != null) {
            for(int i = 0; i < serArchive.getArchiveValueList().size(); i ++) {
                serArchiveValueRepository.delete(serArchive.getArchiveValueList().get(i));
            }
        }

        serArchiveRepository.delete(serArchive);
    }

    @Override
    public List<SerArchive> findAll() {
        QSerArchive builder = QSerArchive.serArchive;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.status.eq(SerArchive.Status.ACTIVE));

        return StreamSupport
                .stream(serArchiveRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

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
                }
            }
        } else {
            exportList = archiveList;
        }
        return exportList;
    }

    @Override
    public List<SerArchive> getExportListByFilter(String archiveName, String status, Long categoryId, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(archiveName, status, categoryId);

        //get all archive list
        List<SerArchive> archiveList = StreamSupport
                .stream(serArchiveRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SerArchive> exportList = getExportList(archiveList, isAll, idList);
        return exportList;
    }
}
