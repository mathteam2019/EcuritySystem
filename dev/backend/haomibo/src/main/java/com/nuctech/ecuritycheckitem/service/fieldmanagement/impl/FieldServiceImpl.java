package com.nuctech.ecuritycheckitem.service.fieldmanagement.impl;

import com.nuctech.ecuritycheckitem.models.db.QSysDevice;
import com.nuctech.ecuritycheckitem.models.db.QSysField;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceRepository;
import com.nuctech.ecuritycheckitem.repositories.SysFieldRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.fieldmanagement.FieldService;
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
public class FieldServiceImpl implements FieldService {

    @Autowired
    SysFieldRepository sysFieldRepository;

    @Autowired
    SysDeviceRepository sysDeviceRepository;


    @Autowired
    AuthenticationFacade authenticationFacade;

    @Override
    @Transactional
    public void createField(SysField sysField) {
        if(sysField.getParentFieldId() == 0) {
            sysField.setStatus(SysField.Status.ACTIVE);
        }
        // Add createdInfo.
        sysField.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysFieldRepository.save(sysField);
    }

    @Override
    public boolean checkFieldExist(Long fieldId) {
        return sysFieldRepository.exists(QSysField.sysField.fieldId.eq(fieldId));
    }

    @Override
    public boolean checkDeviceExist(Long fieldId) {
        return sysDeviceRepository.exists(QSysDevice.sysDevice.fieldId.eq(fieldId));
    }

    @Override
    public boolean checkHasChild(Long fieldId) {
        return sysFieldRepository.exists(QSysField.sysField.parentFieldId.eq(fieldId));
    }

    @Override
    @Transactional
    public void modifyField(SysField sysField) {
        SysField oldSysField = sysFieldRepository.findOne(QSysField.sysField.fieldId.eq(sysField.getFieldId())).orElse(null);

        //Don't modify created by and created time
        sysField.setCreatedBy(oldSysField.getCreatedBy());
        sysField.setCreatedTime(oldSysField.getCreatedTime());

        // Add edited info.
        sysField.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysFieldRepository.save(sysField);
    }

    @Override
    @Transactional
    public void removeField(Long fieldId) {
        sysFieldRepository.delete(SysField.builder().fieldId(fieldId).build());
    }

    @Override
    @Transactional
    public void updateStatus(Long fieldId, String status) {
        Optional<SysField> optionalSysField = sysFieldRepository.findOne(QSysField.sysField.fieldId.eq(fieldId));
        SysField sysField = optionalSysField.get();

        // Update status.
        sysField.setStatus(status);

        // Add edited info.
        sysField.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysFieldRepository.save(sysField);
    }

    @Override
    public List<SysField> findAll() {
        List<SysField> sysFieldList = sysFieldRepository.findAll();


        //set parent's  parent to null so prevent recursion
        for(int i = 0; i < sysFieldList.size(); i ++) {
            SysField field = sysFieldList.get(i);
            field.setParentDesignation("");
            if(field.getParent() != null) {
                field.setParentDesignation(field.getParent().getFieldDesignation());
            }
        }
        return sysFieldList;
    }

    private BooleanBuilder getPredicate(String fieldDesignation, String status, String parentDesignation) {
        QSysField builder = QSysField.sysField;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (!StringUtils.isEmpty(fieldDesignation)) {
            predicate.and(builder.fieldDesignation.contains(fieldDesignation));
        }
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.status.eq(status));
        }
        if (!StringUtils.isEmpty(parentDesignation)) {
            predicate.and(builder.parent.fieldDesignation.contains(parentDesignation));
        }
        return predicate;
    }

    @Override
    public PageResult<SysField> getDeviceListByFilter(String designation, String status, String parentDesignation, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(designation, status, parentDesignation);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysFieldRepository.count(predicate);
        List<SysField> data = sysFieldRepository.findAll(predicate, pageRequest).getContent();

        //set parent's  parent to null so prevent recursion
        for(int i = 0; i < data.size(); i ++) {
            SysField field = data.get(i);
            field.setParentDesignation("");
            if(field.getParent() != null) {
                field.setParentDesignation(field.getParent().getFieldDesignation());
            }
        }
        return new PageResult<SysField>(total, data);
    }

    private List<SysField> getExportList(List<SysField> fieldList, boolean isAll, String idList) {
        List<SysField> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < fieldList.size(); i ++) {
                SysField field = fieldList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(field.getFieldId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(field);
                }
            }
        } else {
            exportList = fieldList;
        }
        return exportList;
    }

    @Override
    public List<SysField> getExportList(String designation, String status, String parentDesignation, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(designation, status, parentDesignation);

        //get all field list
        List<SysField> fieldList = StreamSupport
                .stream(sysFieldRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysField> exportList = getExportList(fieldList, isAll, idList);
        return exportList;
    }
}
