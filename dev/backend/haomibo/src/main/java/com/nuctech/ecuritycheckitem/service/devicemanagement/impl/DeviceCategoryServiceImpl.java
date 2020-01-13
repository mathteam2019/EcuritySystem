/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceCategoryServiceImpl）
 * 文件名：	DeviceCategoryServiceImpl.java
 * 描述：	DeviceCategoryService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.QSysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.db.QSerArchiveTemplate;
import com.nuctech.ecuritycheckitem.models.db.SysUser;

import com.nuctech.ecuritycheckitem.repositories.SerArchiveTemplateRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceCategoryRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceCategoryService;
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
public class DeviceCategoryServiceImpl implements DeviceCategoryService {

    @Autowired
    SysDeviceCategoryRepository sysDeviceCategoryRepository;

    @Autowired
    SerArchiveTemplateRepository serArchiveTemplateRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    /**
     * get prediate from filter parameters
     * @param categoryName
     * @param status
     * @param parentCategoryName
     * @return
     */
    private BooleanBuilder getPredicate(String categoryName, String status, String parentCategoryName) {
        QSysDeviceCategory builder = QSysDeviceCategory.sysDeviceCategory;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(categoryName)) {
            predicate.and(builder.categoryName.contains(categoryName));
        }
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.status.eq(status));
        }
        if (!StringUtils.isEmpty(parentCategoryName)) {
            predicate.and(builder.parent.categoryName.contains(parentCategoryName));
        }
        return predicate;
    }

    /**
     * get pagniated and filtered device category list
     * @param categoryName
     * @param status
     * @param parentCategoryName
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysDeviceCategory> getDeviceCategoryListByPage(String sortBy, String order, String categoryName, String status, String parentCategoryName, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(categoryName, status, parentCategoryName);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sortBy = "archivesTemplateNumber";
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }
        long total = sysDeviceCategoryRepository.count(predicate);
        List<SysDeviceCategory> data = sysDeviceCategoryRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<SysDeviceCategory>(total, data);
    }

    /**
     * check if archive template exists
     * @param categoryId
     * @return
     */
    @Override
    public boolean checkArchiveTemplateExist(long categoryId) {
        if (!serArchiveTemplateRepository.exists(QSerArchiveTemplate.serArchiveTemplate
                .categoryId.eq(categoryId))) {
            return false;
        }
        return true;
    }

    /**
     * check if device category name exists
     * @param categoryName
     * @param categoryId
     * @return
     */
    @Override
    public boolean checkCategoryNameExist(String categoryName, Long categoryId) {
        if (categoryId == null) {
            return sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory.categoryName.eq(categoryName));
        }
        return sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory.categoryName.eq(categoryName)
                .and(QSysDeviceCategory.sysDeviceCategory.categoryId.ne(categoryId)));
    }

    /**
     * check if category number exists
     * @param categoryNumber
     * @param categoryId
     * @return
     */
    @Override
    public boolean checkCategoryNumberExist(String categoryNumber, Long categoryId) {
        if (categoryId == null) {
            return sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory.categoryNumber.eq(categoryNumber));
        }
        return sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory.categoryNumber.eq(categoryNumber)
                .and(QSysDeviceCategory.sysDeviceCategory.categoryId.ne(categoryId)));
    }

    /**
     * check if category exists
     * @param categoryId
     * @return
     */
    @Override
    public boolean checkCategoryExist(long categoryId) {
        return sysDeviceCategoryRepository.exists(QSysDeviceCategory.
                sysDeviceCategory.categoryId.eq(categoryId));
    }

    /**
     * check if children device category exists
     * @param categoryId
     * @return
     */
    @Override
    public boolean checkChildernCategoryExist(long categoryId) {
        return sysDeviceCategoryRepository.exists(QSysDeviceCategory.
                sysDeviceCategory.parentCategoryId.eq(categoryId));
    }

    /**
     * update device category status
     * @param categoryId
     * @param status
     */
    @Override
    @Transactional
    public void updateStatus(long categoryId, String status) {
        Optional<SysDeviceCategory> optionalSysDeviceCategory = sysDeviceCategoryRepository.findOne(QSysDeviceCategory.
                sysDeviceCategory.categoryId.eq(categoryId));
        SysDeviceCategory sysDeviceCategory = optionalSysDeviceCategory.get();

        // Update status.
        sysDeviceCategory.setStatus(status);

        // Add edited info.
        sysDeviceCategory.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceCategoryRepository.save(sysDeviceCategory);
    }

    /**
     * create new device category
     * @param deviceCategory
     */
    @Override
    @Transactional
    public void createSysDeviceCategory(SysDeviceCategory deviceCategory) {

        if (deviceCategory.getParentCategoryId() == 0) {
            deviceCategory.setStatus(SysDeviceCategory.Status.ACTIVE);
        }

        // Add createdInfo.
        deviceCategory.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceCategoryRepository.save(deviceCategory);
    }

    /**
     * modify device category
     * @param deviceCategory
     */
    @Override
    @Transactional
    public void modifySysDeviceCategory(SysDeviceCategory deviceCategory) {

        SysDeviceCategory oldSysDeviceCategory = sysDeviceCategoryRepository.findOne(QSysDeviceCategory.sysDeviceCategory
                .categoryId.eq(deviceCategory.getCategoryId())).orElse(null);

        deviceCategory.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        deviceCategory.setCreatedBy(oldSysDeviceCategory.getCreatedBy());
        deviceCategory.setCreatedTime(oldSysDeviceCategory.getCreatedTime());

        sysDeviceCategoryRepository.save(deviceCategory);
    }

    /**
     * remove device category
     * @param categoryId
     */
    @Override
    @Transactional
    public void removeSysDeviceCategory(long categoryId) {
        sysDeviceCategoryRepository.delete(SysDeviceCategory.builder().categoryId(categoryId).build());
    }

    private List<SysDeviceCategory> getExportList(List<SysDeviceCategory> categoryList, boolean isAll, String idList) {
        List<SysDeviceCategory> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < categoryList.size(); i++) {
                SysDeviceCategory category = categoryList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(category.getCategoryId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(category);
                }
            }
        } else {
            exportList = categoryList;
        }
        return exportList;
    }

    /**
     * find all device category
     * @return
     */
    @Override
    public List<SysDeviceCategory> findAll() {
        QSysDeviceCategory builder = QSysDeviceCategory.sysDeviceCategory;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.status.eq(SysDeviceCategory.Status.ACTIVE));

        return StreamSupport
                .stream(sysDeviceCategoryRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * get device category export list with filter params
     * @param categoryName
     * @param status
     * @param parentCategoryName
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SysDeviceCategory> getExportListByFilter(String sortBy, String order, String categoryName, String status, String parentCategoryName, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(categoryName, status, parentCategoryName);

        //get all archive list
        List<SysDeviceCategory> deviceCategoryList = StreamSupport
                .stream(sysDeviceCategoryRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysDeviceCategory> exportList = getExportList(deviceCategoryList, isAll, idList);
        return exportList;
    }
}
