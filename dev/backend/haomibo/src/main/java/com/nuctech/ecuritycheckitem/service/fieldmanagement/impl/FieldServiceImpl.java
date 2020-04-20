/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（FieldServiceImpl）
 * 文件名：	FieldServiceImpl.java
 * 描述：	FieldService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.fieldmanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceRepository;
import com.nuctech.ecuritycheckitem.repositories.SysFieldRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;

import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.fieldmanagement.FieldService;
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
public class FieldServiceImpl implements FieldService {

    @Autowired
    SysFieldRepository sysFieldRepository;

    @Autowired
    SysDeviceRepository sysDeviceRepository;


    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.CHINESE;

    public static String defaultSort = "fieldSerial";

    public String getJsonFromField(SysField field) {
        SysField newField = SysField.builder()
                .fieldId(field.getFieldId())
                .orgId(field.getOrgId())
                .parentFieldId(field.getParentFieldId())
                .fieldSerial(field.getFieldSerial())
                .fieldDesignation(field.getFieldDesignation())
                .leader(field.getLeader())
                .mobile(field.getMobile())
                .status(field.getStatus())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newField);
        } catch(Exception ex) {
        }
        return answer;
    }

    /**
     * create new field
     * @param sysField
     */
    @Override
    @Transactional
    public void createField(SysField sysField) {

        if (sysField.getParentFieldId() == 0) {
            sysField.setStatus(SysField.Status.ACTIVE);
        }
        // Add createdInfo.
        sysField.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

        sysFieldRepository.save(sysField);
        String valueAfter = getJsonFromField(sysField);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Field", null, currentLocale), "", sysField.getFieldId().toString(), null, true, "", valueAfter);
    }

    /**
     * check if field exists
     * @param fieldId
     * @return
     */
    @Override
    public boolean checkFieldExist(Long fieldId) {
        return sysFieldRepository.exists(QSysField.sysField.fieldId.eq(fieldId));
    }

    /**
     * check field serial exists
     * @param fieldSerial
     * @param fieldId
     * @return
     */
    @Override
    public boolean checkFieldSerial(String fieldSerial, Long fieldId) {
        if (fieldId == null) {
            return sysFieldRepository.exists(QSysField.sysField.fieldSerial.eq(fieldSerial));
        }
        return sysFieldRepository.exists(QSysField.sysField.fieldSerial.eq(fieldSerial)
                .and(QSysField.sysField.fieldId.ne(fieldId)));
    }

    /**
     * check field designation exists
     * @param fieldDesignation
     * @param fieldId
     * @return
     */
    @Override
    public boolean checkFieldDesignation(String fieldDesignation, Long fieldId) {
        if (fieldId == null) {
            return sysFieldRepository.exists(QSysField.sysField.fieldDesignation.eq(fieldDesignation));
        }
        return sysFieldRepository.exists(QSysField.sysField.fieldDesignation.eq(fieldDesignation)
                .and(QSysField.sysField.fieldId.ne(fieldId)));
    }

    /**
     * check if device exists
     * @param fieldId
     * @return
     */
    @Override
    public boolean checkDeviceExist(Long fieldId) {
        return sysDeviceRepository.exists(QSysDevice.sysDevice.fieldId.eq(fieldId));
    }

    /**
     * check if has children
     * @param fieldId
     * @return
     */
    @Override
    public boolean checkHasChild(Long fieldId) {
        return sysFieldRepository.exists(QSysField.sysField.parentFieldId.eq(fieldId));
    }

    /**
     * edit field
     * @param sysField
     */
    @Override
    @Transactional
    public void modifyField(SysField sysField) {
        SysField oldSysField = sysFieldRepository.findOne(QSysField.sysField.fieldId.eq(sysField.getFieldId())).orElse(null);
        String valueBefore = getJsonFromField(oldSysField);
        //Don't modify created by and created time
        sysField.setCreatedBy(oldSysField.getCreatedBy());
        sysField.setCreatedTime(oldSysField.getCreatedTime());
        sysField.setStatus(oldSysField.getStatus());

        // Add edited info.
        sysField.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

        sysFieldRepository.save(sysField);
        String valueAfter = getJsonFromField(sysField);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Field", null, currentLocale), "", sysField.getFieldId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * remove field
     * @param fieldId
     */
    @Override
    @Transactional
    public boolean removeField(Long fieldId) {
        SysField oldSysField = sysFieldRepository.findOne(QSysField.sysField.fieldId.eq(fieldId)).orElse(null);
        if(oldSysField.getStatus().equals(SysField.Status.ACTIVE)) {
            return false;
        }
        String valueBefore = getJsonFromField(oldSysField);
        sysFieldRepository.delete(SysField.builder().fieldId(fieldId).build());
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Field", null, currentLocale), "", String.valueOf(fieldId), null, true, valueBefore, "");
        return true;
    }

    /**
     * update field status
     * @param fieldId
     * @param status
     */
    @Override
    @Transactional
    public void updateStatus(Long fieldId, String status) {
        Optional<SysField> optionalSysField = sysFieldRepository.findOne(QSysField.sysField.fieldId.eq(fieldId));
        SysField sysField = optionalSysField.get();
        String valueBefore = getJsonFromField(sysField);
        // Update status.
        sysField.setStatus(status);

        // Add edited info.
        sysField.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

        sysFieldRepository.save(sysField);
        String valueAfter = getJsonFromField(sysField);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Field", null, currentLocale), "", String.valueOf(fieldId), null, true, valueBefore, valueAfter);
    }

    /**
     * find all fields
     * @param isAll
     * @return
     */
    @Override
    public List<SysField> findAll(boolean isAll) {
        QSysField builder = QSysField.sysField;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (isAll == false) {
            predicate.and(builder.status.eq(SysField.Status.ACTIVE));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }


        List<SysField> sysFieldList = StreamSupport
                .stream(sysFieldRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());


        //set parent's  parent to null so prevent recursion
        for (int i = 0; i < sysFieldList.size(); i++) {
            SysField field = sysFieldList.get(i);
            field.setParentDesignation("");
            if (field.getParent() != null) {
                field.setParentDesignation(field.getParent().getFieldDesignation());
            }
        }
        return sysFieldList;
    }

    /**
     * get prediate from filter parameters
     * @param designation
     * @param status
     * @param parentFieldId
     * @return
     */
    private BooleanBuilder getPredicate(String  designation, String status, Long parentFieldId) {
        QSysField builder = QSysField.sysField;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (parentFieldId != null) {
            predicate.and(builder.parentFieldId.eq(parentFieldId));
        }
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.status.eq(status));
        }
        if (!StringUtils.isEmpty(designation)) {
            predicate.and(builder.fieldDesignation.contains(designation));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }

        return predicate;
    }

    /**
     * get pagniated and filtered device list
     * @param designation
     * @param status
     * @param parentFieldId
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysField> getDeviceListByFilter(String sortBy, String order, String  designation, String status, Long parentFieldId, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(designation, status, parentFieldId);

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

        long total = sysFieldRepository.count(predicate);
        List<SysField> data = sysFieldRepository.findAll(predicate, pageRequest).getContent();

        //set parent's  parent to null so prevent recursion
        for (int i = 0; i < data.size(); i++) {
            SysField field = data.get(i);
            field.setParentDesignation("");
            if (field.getParent() != null) {
                field.setParentDesignation(field.getParent().getFieldDesignation());
            }
        }
        return new PageResult<SysField>(total, data);
    }

    /**
     * extract field export list
     * @param fieldList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysField> getExportList(List<SysField> fieldList, boolean isAll, String idList) {
        List<SysField> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < fieldList.size(); i++) {
                SysField field = fieldList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(field.getFieldId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(field);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < fieldList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(fieldList.get(i));
            }
        }
        return exportList;
    }

    /**
     * get field export list
     * @param designation
     * @param status
     * @param parentFieldId
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SysField> getExportList(String sortBy, String order, String  designation, String status, Long parentFieldId, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(designation, status, parentFieldId);
        String[] splits = idList.split(",");
        List<Long> fieldIdList = new ArrayList<>();
        for(String idStr: splits) {
            fieldIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QSysField.sysField.fieldId.in(fieldIdList));
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by(defaultSort).ascending();
        }
        //get all field list
        List<SysField> fieldList;
        if(sort != null) {
            fieldList = StreamSupport
                    .stream(sysFieldRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            fieldList = StreamSupport
                    .stream(sysFieldRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }


        //List<SysField> exportList = getExportList(fieldList, isAll, idList);
        return fieldList;
    }
}
