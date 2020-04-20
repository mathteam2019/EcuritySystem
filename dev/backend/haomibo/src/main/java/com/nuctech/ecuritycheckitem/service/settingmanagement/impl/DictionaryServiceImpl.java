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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerSeizedGoodRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDictionaryDataRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDictionaryRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.DictionaryService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.SerSeizedGoodService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    @Autowired
    SysDictionaryDataRepository sysDictionaryDataRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.CHINESE;

    public static String defaultDictionarySort = "dictionaryName";

    public static String defaultDictionaryDataSort = "dataCode";

    public String getJsonFromDictionary(SysDictionary dictionary) {
        SysDictionary newDictionary = SysDictionary.builder()
                .dictionaryId(dictionary.getDictionaryId())
                .dictionaryName(dictionary.getDictionaryName())
                .dictionaryType(dictionary.getDictionaryType())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newDictionary);
        } catch(Exception ex) {
        }
        return answer;
    }

    public String getJsonFromDictionaryData(SysDictionaryData dictionaryData) {
        SysDictionaryData newDictionaryData = SysDictionaryData.builder()
                .dataId(dictionaryData.getDataId())
                .dictionaryId(dictionaryData.getDictionaryId())
                .dataCode(dictionaryData.getDataCode())
                .dataValue(dictionaryData.getDataValue())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newDictionaryData);
        } catch(Exception ex) {
        }
        return answer;
    }


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
        return sysDictionaryDataRepository.exists(QSysDictionaryData.sysDictionaryData.dataValue.eq(dataValue)
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
        return sysDictionaryDataRepository.exists(QSysDictionaryData.sysDictionaryData.dataCode.eq(dataCode)
                .and(QSysDictionaryData.sysDictionaryData.dataId.ne(dataId)));
    }

    /**
     * create Dictionary
     * @param sysDictionary
     */
    @Override
    public void createDictionary(SysDictionary sysDictionary) {
        sysDictionary.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        String valueAfter = getJsonFromDictionary(sysDictionary);
        sysDictionaryRepository.save(sysDictionary);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Dictionary", null, currentLocale), "", sysDictionary.getDictionaryId().toString(), null, true, "", valueAfter);
    }

    /**
     * create Dictionary Data
     * @param sysDictionaryData
     */
    @Override
    public void createDictionaryData(SysDictionaryData sysDictionaryData) {
        sysDictionaryData.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        sysDictionaryDataRepository.save(sysDictionaryData);
        String valueAfter = getJsonFromDictionaryData(sysDictionaryData);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("DictionaryData", null, currentLocale), "", sysDictionaryData.getDataId().toString(), null, true, "", valueAfter);
    }

    /**
     * modify dictionary
     * @param sysDictionary
     */
    @Override
    public void modifyDictionary(SysDictionary sysDictionary) {
        SysDictionary oldSysDictionary = sysDictionaryRepository.findOne(QSysDictionary.sysDictionary.dictionaryId.eq(sysDictionary.getDictionaryId())).orElse(null);
        String valueBefore = getJsonFromDictionary(oldSysDictionary);
        //Don't modify created by and created time
        sysDictionary.setCreatedBy(oldSysDictionary.getCreatedBy());
        sysDictionary.setCreatedTime(oldSysDictionary.getCreatedTime());

        // Add edited info.
        sysDictionary.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        String valueAfter = getJsonFromDictionary(sysDictionary);
        sysDictionaryRepository.save(sysDictionary);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Dictionary", null, currentLocale), "", sysDictionary.getDictionaryId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * modify dictionary data
     * @param sysDictionaryData
     */
    @Override
    public void modifyDictionaryData(SysDictionaryData sysDictionaryData) {
        SysDictionaryData oldSysDictionaryData = sysDictionaryDataRepository.findOne(QSysDictionaryData.sysDictionaryData.dataId.eq(sysDictionaryData.getDataId())).orElse(null);
        String valueBefore = getJsonFromDictionaryData(oldSysDictionaryData);
        //Don't modify created by and created time
        sysDictionaryData.setCreatedBy(oldSysDictionaryData.getCreatedBy());
        sysDictionaryData.setCreatedTime(oldSysDictionaryData.getCreatedTime());

        // Add edited info.
        sysDictionaryData.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

        sysDictionaryDataRepository.save(sysDictionaryData);
        String valueAfter = getJsonFromDictionaryData(sysDictionaryData);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("DictionaryData", null, currentLocale), "", sysDictionaryData.getDataId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * remove seized dictioanry
     * @param dictionaryId
     */
    @Override
    public void removeDictionary(Long dictionaryId) {
        SysDictionary sysDictionary = sysDictionaryRepository.findOne(QSysDictionary.sysDictionary.dictionaryId.eq(dictionaryId)).orElse(null);
        String valueBefore = getJsonFromDictionary(sysDictionary);
        sysDictionaryRepository.delete(SysDictionary.builder().dictionaryId(dictionaryId).build());
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Dictionary", null, currentLocale), "", dictionaryId.toString(), null, true, valueBefore, "");

    }

    /**
     * remove seized dictioanry data
     * @param dataId
     */
    @Override
    public void removeDictionaryData(Long dataId) {
        SysDictionaryData sysDictionaryData = sysDictionaryDataRepository.findOne(QSysDictionaryData.sysDictionaryData.dataId.eq(dataId)).orElse(null);
        String valueBefore = getJsonFromDictionaryData(sysDictionaryData);
        sysDictionaryDataRepository.delete(SysDictionaryData.builder().dataId(dataId).build());
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("DictionaryData", null, currentLocale), "", dataId.toString(), null, true, valueBefore, "");
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

    @Override
    public List<SysDictionaryData> getDictionaryListById(Long dictionaryId) {
        QSysDictionaryData builder = QSysDictionaryData.sysDictionaryData;
        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());
        predicate.and(builder.dictionaryId.eq(dictionaryId));
        return StreamSupport
                .stream(sysDictionaryDataRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
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
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by(defaultDictionarySort).ascending());
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
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by(defaultDictionaryDataSort).ascending());
        }
        long total = sysDictionaryDataRepository.count(predicate);
        List<SysDictionaryData> data = sysDictionaryDataRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }
}
