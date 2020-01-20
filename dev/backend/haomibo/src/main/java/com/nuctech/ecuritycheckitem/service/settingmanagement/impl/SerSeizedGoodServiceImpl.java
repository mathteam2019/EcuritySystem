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
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.QSerSeizedGood;
import com.nuctech.ecuritycheckitem.models.db.SerSeizedGood;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.repositories.SerSeizedGoodRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.settingmanagement.SerSeizedGoodService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SerSeizedGoodServiceImpl implements SerSeizedGoodService {

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SerSeizedGoodRepository serSeizedGoodRepository;


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
    }

    /**
     * modify seized goods
     * @param serSeizedGood
     */
    @Override
    public void modifyGood(SerSeizedGood serSeizedGood) {
        SerSeizedGood oldSerSeizedGood = serSeizedGoodRepository.findOne(QSerSeizedGood.serSeizedGood.goodsId.eq(serSeizedGood.getGoodsId())).orElse(null);

        //Don't modify created by and created time
        serSeizedGood.setCreatedBy(oldSerSeizedGood.getCreatedBy());
        serSeizedGood.setCreatedTime(oldSerSeizedGood.getCreatedTime());

        // Add edited info.
        serSeizedGood.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serSeizedGoodRepository.save(serSeizedGood);
    }

    /**
     * remove seized good
     * @param goodsId
     */
    @Override
    public void removeGood(Long goodsId) {
        serSeizedGoodRepository.delete(SerSeizedGood.builder().goodsId(goodsId).build());
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
            predicate.and(builder.seizedGoodsCode.eq(goodsCode));
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
        long total = serSeizedGoodRepository.count(predicate);
        List<SerSeizedGood> data = serSeizedGoodRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }
}
