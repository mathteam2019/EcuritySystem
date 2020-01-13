/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2020。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DictionaryServiceImpl）
 * 文件名：	DictionaryServiceImpl.java
 * 描述：	DictionaryService implement
 * 作者名：	Choe
 * 日期：	2020/01/10
 */

package com.nuctech.ecuritycheckitem.service.settingmanagement.impl;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.repositories.SerSeizedGoodRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDictionaryDataRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDictionaryRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.settingmanagement.DictionaryService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.SerSeizedGoodService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    @Autowired
    SysDictionaryDataRepository sysDictionaryDataRepository;


    /**
     * check dictionary name already exist
     * @param dictionaryName
     * @param dictionaryId
     * @return
     */
    @Override
    public boolean checkDictionary(String dictionaryName, Long dictionaryId) {
        if (dictionaryId == null) {
            return sysDictionaryRepository.exists(QSysDictionary.sysDictionary.dictionaryName.eq(dictionaryName));
        }
        return sysDictionaryRepository.exists(QSysDictionary.sysDictionary.dictionaryName.eq(dictionaryName)
                .and(QSysDictionary.sysDictionary.dictionaryId.ne(dictionaryId)));
    }

    /**
     * check dictionary data name already exist
     * @param dataValue
     * @param dataId
     * @return
     */
    @Override
    public boolean checkDictionaryData(String dataValue, Long dataId) {
        if (dataId == null) {
            return sysDictionaryDataRepository.exists(QSysDictionaryData.sysDictionaryData.dataValue.eq(dataValue));
        }
        return sysDictionaryRepository.exists(QSysDictionaryData.sysDictionaryData.dataValue.eq(dataValue)
                .and(QSysDictionaryData.sysDictionaryData.dataId.ne(dataId)));
    }

    /**
     * check dictionary data name already exist
     * @param dataCode
     * @param dataId
     * @return
     */
    @Override
    public boolean checkDictionaryDataCode(String dataCode, Long dataId) {
        if (dataId == null) {
            return sysDictionaryDataRepository.exists(QSysDictionaryData.sysDictionaryData.dataCode.eq(dataCode));
        }
        return sysDictionaryRepository.exists(QSysDictionaryData.sysDictionaryData.dataCode.eq(dataCode)
                .and(QSysDictionaryData.sysDictionaryData.dataId.ne(dataId)));
    }

    /**
     * create Dictionary
     * @param sysDictionary
     */
    @Override
    public void createDictionary(SysDictionary sysDictionary) {
        sysDictionary.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysDictionaryRepository.save(sysDictionary);
    }

    /**
     * create Dictionary Data
     * @param sysDictionaryData
     */
    @Override
    public void createDictionaryData(SysDictionaryData sysDictionaryData) {
        sysDictionaryData.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysDictionaryDataRepository.save(sysDictionaryData);
    }

    /**
     * modify dictionary
     * @param sysDictionary
     */
    @Override
    public void modifyDictionary(SysDictionary sysDictionary) {
        SysDictionary oldSysDictionary = sysDictionaryRepository.findOne(QSysDictionary.sysDictionary.dictionaryId.eq(sysDictionary.getDictionaryId())).orElse(null);

        //Don't modify created by and created time
        sysDictionary.setCreatedBy(oldSysDictionary.getCreatedBy());
        sysDictionary.setCreatedTime(oldSysDictionary.getCreatedTime());

        // Add edited info.
        sysDictionary.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDictionaryRepository.save(sysDictionary);
    }

    /**
     * modify dictionary data
     * @param sysDictionaryData
     */
    @Override
    public void modifyDictionaryData(SysDictionaryData sysDictionaryData) {
        SysDictionaryData oldSysDictionaryData = sysDictionaryDataRepository.findOne(QSysDictionaryData.sysDictionaryData.dataId.eq(sysDictionaryData.getDataId())).orElse(null);

        //Don't modify created by and created time
        sysDictionaryData.setCreatedBy(oldSysDictionaryData.getCreatedBy());
        sysDictionaryData.setCreatedTime(oldSysDictionaryData.getCreatedTime());

        // Add edited info.
        sysDictionaryData.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDictionaryDataRepository.save(sysDictionaryData);
    }

    /**
     * remove seized dictioanry
     * @param dictionaryId
     */
    @Override
    public void removeDictionary(Long dictionaryId) {
        sysDictionaryRepository.delete(SysDictionary.builder().dictionaryId(dictionaryId).build());
    }

    /**
     * remove seized dictioanry data
     * @param dataId
     */
    @Override
    public void removeDictionaryData(Long dataId) {
        sysDictionaryDataRepository.delete(SysDictionaryData.builder().dictionaryId(dataId).build());
    }

    /**
     * check if dictionary exists
     * @param dictionaryId
     * @return
     */
    @Override
    public boolean checkDictionaryExist(Long dictionaryId) {
        return sysDictionaryRepository.exists(QSysDictionary.sysDictionary.dictionaryId.eq(dictionaryId));
    }

    /**
     * check if dictionary data exists
     * @param dataId
     * @return
     */
    @Override
    public boolean checkDictionaryDataExist(Long dataId) {
        return sysDictionaryDataRepository.exists(QSysDictionaryData.sysDictionaryData.dataId.eq(dataId));
    }

    /**
     * check if dictionary contain dictionary data
     * @param dictionaryId
     * @return
     */
    @Override
    public boolean checkDictionaryChildExist(Long dictionaryId) {
        return sysDictionaryDataRepository.exists(QSysDictionaryData.sysDictionaryData.dictionaryId.eq(dictionaryId));
    }

    /**
     * filter dictionary
     * @param dictionaryName
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysDictionary> getDictionaryListByFilter(String sortBy, String order, String dictionaryName, int currentPage, int perPage) {
        QSysDictionary builder = QSysDictionary.sysDictionary;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (!StringUtils.isEmpty(dictionaryName)) {
            predicate.and(builder.dictionaryName.contains(dictionaryName));
        }
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }
        long total = sysDictionaryRepository.count(predicate);
        List<SysDictionary> data = sysDictionaryRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    /**
     * filter dictionary data
     * @param dataCode
     * @param dataValue
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysDictionaryData> getDictionaryDataListByFilter(String sortBy, String order, String dataCode, String dataValue, Long dictionaryId,
                                                               int currentPage, int perPage) {
        QSysDictionaryData builder = QSysDictionaryData.sysDictionaryData;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (!StringUtils.isEmpty(dataCode)) {
            predicate.and(builder.dataCode.contains(dataCode));
        }

        if (!StringUtils.isEmpty(dataValue)) {
            predicate.and(builder.dataValue.contains(dataValue));
        }

        predicate.and(builder.dictionaryId.eq(dictionaryId));
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }
        long total = sysDictionaryDataRepository.count(predicate);
        List<SysDictionaryData> data = sysDictionaryDataRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }
}
