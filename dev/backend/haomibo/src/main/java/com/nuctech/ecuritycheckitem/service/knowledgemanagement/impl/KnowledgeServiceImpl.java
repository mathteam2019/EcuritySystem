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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerKnowledgeCaseDealRepository;
import com.nuctech.ecuritycheckitem.repositories.SerKnowledgeCaseRepository;
import com.nuctech.ecuritycheckitem.repositories.SerTaskTagRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.knowledgemanagement.KnowledgeService;
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

import javax.sql.rowset.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    SerTaskTagRepository serTaskTagRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.ENGLISH;

    public String getJsonFromKnowledge(SerKnowledgeCase knowledgeCase) {
        SerKnowledgeCase newKnowledge = SerKnowledgeCase.builder()
                .caseId(knowledgeCase.getCaseId())
                .caseDealId(knowledgeCase.getCaseDealId())
                .taskId(knowledgeCase.getTaskId())
                .caseStatus(knowledgeCase.getCaseStatus())
                .caseCollectUserId(knowledgeCase.getCaseCollectUserId())
                .caseApprovalUserId(knowledgeCase.getCaseApprovalUserId())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newKnowledge);
        } catch(Exception ex) {
        }
        return answer;
    }

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
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            predicate.and(builder.createdBy.in(categoryUser.getUserIdList()).or(builder.editedBy.in(categoryUser.getUserIdList())));
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
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < dealList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(dealList.get(i));
            }
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
    public PageResult<SerKnowledgeCaseDeal> getDealListByFilter(String sortBy, String order, String caseStatus, String taskNumber, String modeName, String taskResult,
                                                                String fieldDesignation, String handGoods, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(caseStatus, taskNumber, modeName, taskResult, fieldDesignation, handGoods);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sortBy = "task.taskNumber";
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }
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
    public List<SerKnowledgeCaseDeal> getDealExportList(String sortBy, String order, String caseStatus, String taskNumber, String modeName, String taskResult,
                                                        String fieldDesignation, String handGoods, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(caseStatus, taskNumber, modeName, taskResult, fieldDesignation, handGoods);
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sortBy = "task.taskNumber";
            sort = new Sort(Sort.Direction.ASC, sortBy);
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = new Sort(Sort.Direction.DESC, sortBy);
            }
        }
        List<SerKnowledgeCaseDeal> dealList;
        if(sort != null) {
            dealList = StreamSupport
                    .stream(serKnowledgeCaseDealRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            dealList = StreamSupport
                    .stream(serKnowledgeCaseDealRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

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

    @Override
    public void delete(Long caseDealId) {
        Optional<SerKnowledgeCaseDeal> optionalSerKnowledgeCase = serKnowledgeCaseDealRepository.findOne(QSerKnowledgeCaseDeal.serKnowledgeCaseDeal
                .caseDealId.eq(caseDealId));
        SerKnowledgeCaseDeal serKnowledgeCaseDeal = optionalSerKnowledgeCase.get();
        SerKnowledgeCase serKnowledgeCase = serKnowledgeCaseDeal.getKnowledgeCase();
        String valueBefore = getJsonFromKnowledge(serKnowledgeCase);
        serKnowledgeCaseRepository.delete(serKnowledgeCase);
        serKnowledgeCaseDealRepository.delete(serKnowledgeCaseDeal);
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("KnowledgeCase", null, currentLocale), "", serKnowledgeCase.getCaseId().toString(), null, true, valueBefore, "");
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
        String valueBefore = getJsonFromKnowledge(serKnowledgeCase);
        // Update status.
        serKnowledgeCase.setCaseStatus(status);

        // Add edited info.
        serKnowledgeCase.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serKnowledgeCaseRepository.save(serKnowledgeCase);
        String valueAfter = getJsonFromKnowledge(serKnowledgeCase);
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("KnowledgeCase", null, currentLocale), "", serKnowledgeCase.getCaseId().toString(), null, true, valueBefore, valueAfter);
    }

    /**
     * insert new knowledge case
     * @param knowledgeCase
     * @return
     */
    @Override
    @Transactional
    public Long insertNewKnowledgeCase(SerKnowledgeCase knowledgeCase, List<String> tagList) {

        SerKnowledgeCase existKnowledgeCase = checkIfTaskAlreadyExistInKnowledgeCase(knowledgeCase);

        if (existKnowledgeCase == null) {

            existKnowledgeCase = knowledgeCase;
            existKnowledgeCase.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        }
        else {

            Long knowledgeCaseId = existKnowledgeCase.getCaseId();
            knowledgeCase.setCaseId(knowledgeCaseId);
            existKnowledgeCase = knowledgeCase;
            existKnowledgeCase.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            Iterable<SerTaskTag> taskTagList = serTaskTagRepository.findAll(QSerTaskTag.serTaskTag.taskId.eq(knowledgeCase.getTaskId()));
            serTaskTagRepository.deleteAll(taskTagList);
        }
        if(tagList != null && tagList.size() > 0) {
            List<SerTaskTag> taskTagList = new ArrayList<>();
            for(int i = 0; i < tagList.size(); i ++) {
                SerTaskTag taskTag = new SerTaskTag();
                taskTag.setTaskId(knowledgeCase.getTaskId());
                taskTag.setTagId(tagList.get(i));
                taskTag.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                taskTagList.add(taskTag);
            }
            serTaskTagRepository.saveAll(taskTagList);
        }



        SerKnowledgeCase serKnowledgeCase = serKnowledgeCaseRepository.save(existKnowledgeCase);
        String valueAfter = getJsonFromKnowledge(serKnowledgeCase);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("KnowledgeCase", null, currentLocale), "", serKnowledgeCase.getCaseId().toString(), null, true, "", valueAfter);
        if (serKnowledgeCase != null) {
            return serKnowledgeCase.getCaseId();
        } else
            return null;
    }

    /**
     * return object if exist, otherwise null
     * @param knowledgeCase
     * @return
     */
    private SerKnowledgeCase checkIfTaskAlreadyExistInKnowledgeCase(SerKnowledgeCase knowledgeCase) {

        return serKnowledgeCaseRepository.findOne(QSerKnowledgeCase.serKnowledgeCase.taskId.eq(knowledgeCase.getTaskId())).orElse(null);
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
