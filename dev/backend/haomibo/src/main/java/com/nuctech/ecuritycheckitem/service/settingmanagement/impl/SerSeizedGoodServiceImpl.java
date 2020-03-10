/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2020。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerSeizedGoodServiceImpl）
 * 文件名：	SerSeizedGoodServiceImpl.java
 * 描述：	SerSeizedGoodService implement
 * 作者名：	Choe
 * 日期：	2020/01/09
 */

package com.nuctech.ecuritycheckitem.service.settingmanagement.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerSeizedGoodRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.SerSeizedGoodService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class SerSeizedGoodServiceImpl implements SerSeizedGoodService {

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SerSeizedGoodRepository serSeizedGoodRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.ENGLISH;

    public String getJsonFromSeized(SerSeizedGood seizedGood) {
        SerSeizedGood newDictionary = SerSeizedGood.builder()
                .goodsId(seizedGood.getGoodsId())
                .seizedGoodsCode(seizedGood.getSeizedGoodsCode())
                .seizedGoodType(seizedGood.getSeizedGoodType())
                .seizedGoodsLevel(seizedGood.getSeizedGoodsLevel())
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

    /**
     * check goods name already exist
     * @param seizedGoodsCode
     * @param goodId
     * @return
     */
    @Override
    public boolean checkGood(String seizedGoodsCode, Long goodId) {
        if (goodId == null) {
            return serSeizedGoodRepository.exists(QSerSeizedGood.serSeizedGood.seizedGoodsCode.eq(seizedGoodsCode));
        }
        return serSeizedGoodRepository.exists(QSerSeizedGood.serSeizedGood.seizedGoodsCode.eq(seizedGoodsCode)
                .and(QSerSeizedGood.serSeizedGood.goodsId.ne(goodId)));
    }

    /**
     * create seize good
     * @param serSeizedGood
     */
    @Override
    public void createGood(SerSeizedGood serSeizedGood) {
        serSeizedGood.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        serSeizedGoodRepository.save(serSeizedGood);
        String valueAfter = getJsonFromSeized(serSeizedGood);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("SeizedGood", null, currentLocale), "", serSeizedGood.getGoodsId().toString(), null, true, "", valueAfter);
    }

    /**
     * modify seized goods
     * @param serSeizedGood
     */
    @Override
    public void modifyGood(SerSeizedGood serSeizedGood) {
        SerSeizedGood oldSerSeizedGood = serSeizedGoodRepository.findOne(QSerSeizedGood.serSeizedGood.goodsId.eq(serSeizedGood.getGoodsId())).orElse(null);
        String valueBefore = getJsonFromSeized(oldSerSeizedGood);
        //Don't modify created by and created time
        serSeizedGood.setCreatedBy(oldSerSeizedGood.getCreatedBy());
        serSeizedGood.setCreatedTime(oldSerSeizedGood.getCreatedTime());

        // Add edited info.
        serSeizedGood.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serSeizedGoodRepository.save(serSeizedGood);
        String valueAfter = getJsonFromSeized(serSeizedGood);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("SeizedGood", null, currentLocale), "", serSeizedGood.getGoodsId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * remove seized good
     * @param goodsId
     */
    @Override
    public void removeGood(Long goodsId) {
        SerSeizedGood oldSerSeizedGood = serSeizedGoodRepository.findOne(QSerSeizedGood.serSeizedGood.goodsId.eq(goodsId)).orElse(null);
        String valueBefore = getJsonFromSeized(oldSerSeizedGood);
        serSeizedGoodRepository.delete(SerSeizedGood.builder().goodsId(goodsId).build());
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("SeizedGood", null, currentLocale), "", String.valueOf(goodsId), null, true, valueBefore, "");
    }

    /**
     * check if seized good exists
     * @param goodsId
     * @return
     */
    @Override
    public boolean checkSeizedExist(Long goodsId) {
        return serSeizedGoodRepository.exists(QSerSeizedGood.serSeizedGood.goodsId.eq(goodsId));
    }

    /**
     * filter seized goods
     * @param goodsCode
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SerSeizedGood> getGoodsListByFilter(String sortBy, String order, String goodsCode, int currentPage, int perPage) {
        QSerSeizedGood builder = QSerSeizedGood.serSeizedGood;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (!StringUtils.isEmpty(goodsCode)) {
            predicate.and(builder.sysDictionaryData.dataValue.contains(goodsCode));
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
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by("goodsId").ascending());
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        long total = serSeizedGoodRepository.count(predicate);
        List<SerSeizedGood> data = serSeizedGoodRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }
}
