/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（KnowledgeServiceImpl）
 * 文件名：	KnowledgeServiceImpl.java
 * 描述：	KnowledgeService impl
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.knowledgemanagement.impl;

import com.nuctech.ecuritycheckitem.models.db.QSerKnowledgeCaseDeal;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCase;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.QSerKnowledgeCase;

import com.nuctech.ecuritycheckitem.repositories.SerKnowledgeCaseDealRepository;
import com.nuctech.ecuritycheckitem.repositories.SerKnowledgeCaseRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.knowledgemanagement.KnowledgeService;
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
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    SerKnowledgeCaseDealRepository serKnowledgeCaseDealRepository;

    @Autowired
    SerKnowledgeCaseRepository serKnowledgeCaseRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    /**
     * get predicate from filter parameters
     * @param caseStatus
     * @param taskNumber
     * @param modeName
     * @param taskResult
     * @param fieldDesignation
     * @param handGoods
     * @return
     */
    private BooleanBuilder getPredicate(String caseStatus, String taskNumber, String modeName, String taskResult,
                                        String fieldDesignation, String handGoods) {
        QSerKnowledgeCaseDeal builder = QSerKnowledgeCaseDeal.serKnowledgeCaseDeal;
        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        predicate.and(builder.knowledgeCase.caseStatus.eq(caseStatus));
        if (!StringUtils.isEmpty(taskNumber)) {
            predicate.and(builder.task.taskNumber.contains(taskNumber));
        }
        if (!StringUtils.isEmpty(modeName)) {
            predicate.and(builder.workMode.modeName.eq(modeName));
        }

        if (!StringUtils.isEmpty(taskResult)) {
            predicate.and(builder.handTaskResult.eq(taskResult));
        }
        if (!StringUtils.isEmpty(fieldDesignation)) {
            predicate.and(builder.scanDevice.field.fieldDesignation.contains(fieldDesignation));
        }
        if (!StringUtils.isEmpty(handGoods)) {
            predicate.and(builder.handGoods.contains(handGoods));
        }

        return predicate;
    }

    /**
     * get knowledge case deal export list
     * @param dealList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SerKnowledgeCaseDeal> getExportList(List<SerKnowledgeCaseDeal> dealList, boolean isAll, String idList) {
        List<SerKnowledgeCaseDeal> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < dealList.size(); i++) {
                SerKnowledgeCaseDeal deal = dealList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(deal.getCaseDealId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(deal);
                }
            }
        } else {
            exportList = dealList;
        }
        return exportList;
    }

    /**
     * get filtered knowledge case deal list
     * @param caseStatus
     * @param taskNumber
     * @param modeName
     * @param taskResult
     * @param fieldDesignation
     * @param handGoods
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SerKnowledgeCaseDeal> getDealListByFilter(String caseStatus, String taskNumber, String modeName, String taskResult,
                                                                String fieldDesignation, String handGoods, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(caseStatus, taskNumber, modeName, taskResult, fieldDesignation, handGoods);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serKnowledgeCaseDealRepository.count(predicate);
        List<SerKnowledgeCaseDeal> data = serKnowledgeCaseDealRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    /**
     * get knowledge case deal export list
     * @param caseStatus
     * @param taskNumber
     * @param modeName
     * @param taskResult
     * @param fieldDesignation
     * @param handGoods
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SerKnowledgeCaseDeal> getDealExportList(String caseStatus, String taskNumber, String modeName, String taskResult,
                                                        String fieldDesignation, String handGoods, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(caseStatus, taskNumber, modeName, taskResult, fieldDesignation, handGoods);
        List<SerKnowledgeCaseDeal> dealList = StreamSupport
                .stream(serKnowledgeCaseDealRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SerKnowledgeCaseDeal> exportList = getExportList(dealList, isAll, idList);
        return exportList;
    }

    /**
     * check if knowledgecase exists
     * @param caseId
     * @return
     */
    @Override
    public boolean checkKnowledgeExist(Long caseId) {
        return serKnowledgeCaseRepository.exists(QSerKnowledgeCase.serKnowledgeCase.caseId.eq(caseId));
    }

    /**
     * update knowledgecase status
     * @param caseId
     * @param status
     */
    @Override
    @Transactional
    public void updateStatus(Long caseId, String status) {
        Optional<SerKnowledgeCase> optionalSerKnowledgeCase = serKnowledgeCaseRepository.findOne(QSerKnowledgeCase.serKnowledgeCase
                .caseId.eq(caseId));
        SerKnowledgeCase serKnowledgeCase = optionalSerKnowledgeCase.get();

        // Update status.
        serKnowledgeCase.setCaseStatus(status);

        // Add edited info.
        serKnowledgeCase.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serKnowledgeCaseRepository.save(serKnowledgeCase);
    }

    /**
     * insert new knowledge case
     * @param knowledgeCase
     * @return
     */
    public Long insertNewKnowledgeCase(SerKnowledgeCase knowledgeCase) {

        knowledgeCase.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        SerKnowledgeCase serKnowledgeCase = serKnowledgeCaseRepository.save(knowledgeCase);
        if (serKnowledgeCase != null) {
            return serKnowledgeCase.getCaseId();
        } else
            return null;
    }

    /**
     * insert new knowledge case deal
     * @param knowledgeCaseDeal
     * @return
     */
    public Long insertNewKnowledgeCaseDeal(SerKnowledgeCaseDeal knowledgeCaseDeal) {

        knowledgeCaseDeal.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        SerKnowledgeCaseDeal serKnowledgeCaseDeal = serKnowledgeCaseDealRepository.save(knowledgeCaseDeal);
        if (serKnowledgeCaseDeal != null) {
            return serKnowledgeCaseDeal.getCaseDealId();
        } else
            return null;
    }

    /**
     * update knowledge case
     * @param knowledgeId
     * @param knowledgeCase
     * @return
     */
    public Long updateKnowledgeCase(Long knowledgeId, SerKnowledgeCase knowledgeCase) {

        knowledgeCase.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        SerKnowledgeCase serKnowledgeCase = serKnowledgeCaseRepository.save(knowledgeCase);
        if (serKnowledgeCase != null) {
            return serKnowledgeCase.getCaseId();
        } else
            return null;
    }
}
