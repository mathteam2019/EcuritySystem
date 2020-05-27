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
import com.nuctech.ecuritycheckitem.repositories.SerKnowledgeCaseDealImageRepository;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    SerKnowledgeCaseDealImageRepository serKnowledgeCaseDealImageRepository;

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

    public static Locale currentLocale = Locale.CHINESE;

    public String getJsonFromKnowledge(SerKnowledgeCase knowledgeCase) {
        SerKnowledgeCase newKnowledge = SerKnowledgeCase.builder()
                .caseId(knowledgeCase.getCaseId())
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
     * @param fieldId
     * @param handGoods
     * @return
     */
    private BooleanBuilder getPredicate(String caseStatus, String taskNumber, String modeName, String taskResult,
                                        Long fieldId, String handGoods) {
        QSerKnowledgeCase builder = QSerKnowledgeCase.serKnowledgeCase;
        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        predicate.and(builder.caseStatus.eq(caseStatus));
        if (!StringUtils.isEmpty(taskNumber)) {
            predicate.and(builder.taskNumber.contains(taskNumber));
        }
        if (!StringUtils.isEmpty(modeName)) {
            predicate.and(builder.modeName.eq(modeName));
        }

        if (!StringUtils.isEmpty(taskResult)) {
            if(SerKnowledgeCase.TaskResult.Seized.equals(taskResult)) {
                predicate.and(builder.handGoods.isNotEmpty());
            } else if(SerKnowledgeCase.TaskResult.NoSeized.equals(taskResult)) {
                predicate.and(builder.handTaskResult.isNotEmpty()).and(builder.handGoods.isEmpty());
            } else if(SerKnowledgeCase.TaskResult.Suspection.equals(taskResult)) {
                predicate.and(builder.handTaskResult.isNull());
                predicate.and((builder.judgeUserId.eq(Constants.DEFAULT_SYSTEM_USER).and(builder.scanAtrResult.eq(SerScan.ATRResult.TRUE)))
                        .or(builder.judgeUserId.ne(Constants.DEFAULT_SYSTEM_USER).and(builder.judgeResult.eq(SerJudgeGraph.Result.TRUE))));
            } else {
                predicate.and(builder.handTaskResult.isNull());
                predicate.and((builder.judgeUserId.eq(Constants.DEFAULT_SYSTEM_USER).and(builder.scanAtrResult.ne(SerScan.ATRResult.TRUE)))
                        .or(builder.judgeUserId.ne(Constants.DEFAULT_SYSTEM_USER).and(builder.judgeResult.ne(SerJudgeGraph.Result.TRUE))));
            }
        }
        if (fieldId != null) {
            predicate.and(builder.fieldId.eq(fieldId));
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
     * get filtered knowledge case deal list
     * @param caseStatus
     * @param taskNumber
     * @param modeName
     * @param taskResult
     * @param fieldId
     * @param handGoods
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SerKnowledgeCase> getDealListByFilter(String sortBy, String order, String caseStatus, String taskNumber, String modeName, String taskResult,
                                                                Long fieldId, String handGoods, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(caseStatus, taskNumber, modeName, taskResult, fieldId, handGoods);


        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sortBy = "taskNumber";
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by("caseId").descending());
        }
        long total = serKnowledgeCaseRepository.count(predicate);
        List<SerKnowledgeCase> data = serKnowledgeCaseRepository.findAll(predicate, pageRequest).getContent();
        Date endTime = new Date();
        return new PageResult<>(total, data);
    }

    /**
     * get knowledge case deal export list
     * @param caseStatus
     * @param taskNumber
     * @param modeName
     * @param taskResult
     * @param fieldId
     * @param handGoods
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SerKnowledgeCase> getDealExportList(String sortBy, String order, String caseStatus, String taskNumber, String modeName, String taskResult,
                                                        Long fieldId, String handGoods, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(caseStatus, taskNumber, modeName, taskResult, fieldId, handGoods);
        if(isAll == false) {
            String[] splits = idList.split(",");
            List<Long> caseDealIdList = new ArrayList<>();
            for(String idStr: splits) {
                caseDealIdList.add(Long.valueOf(idStr));
            }
            predicate.and(QSerKnowledgeCase.serKnowledgeCase.caseId.in(caseDealIdList));
        }

        Long limit = Constants.MAX_EXPORT_NUMBER;
        int currentPage = 0;
        int perPage = limit.intValue();
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by("caseId").descending());
        }


        List<SerKnowledgeCase> dealList;
        dealList = StreamSupport
                .stream(serKnowledgeCaseRepository.findAll(predicate, pageRequest).spliterator(), false)
                .collect(Collectors.toList());

        //List<SerKnowledgeCaseDeal> exportList = getExportList(dealList, isAll, idList);
        return dealList;
    }

    /**
     * get knowledge case deal export list
     * @param caseStatus
     * @param taskNumber
     * @param modeName
     * @param taskResult
     * @param fieldId
     * @param handGoods
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SerKnowledgeCaseDealImage> getDealImageList(String sortBy, String order, String caseStatus, String taskNumber, String modeName, String taskResult,
                                                        Long fieldId, String handGoods, boolean isAll, String idList) {
        QSerKnowledgeCaseDealImage builder = QSerKnowledgeCaseDealImage.serKnowledgeCaseDealImage;
        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.caseStatus.eq(caseStatus));
        if (!StringUtils.isEmpty(taskNumber)) {
            predicate.and(builder.taskNumber.contains(taskNumber));
        }
        if (!StringUtils.isEmpty(modeName)) {
            predicate.and(builder.modeName.eq(modeName));
        }

        if (!StringUtils.isEmpty(taskResult)) {
            if(SerKnowledgeCase.TaskResult.Seized.equals(taskResult)) {
                predicate.and(builder.handGoods.isNotEmpty());
            } else if(SerKnowledgeCase.TaskResult.NoSeized.equals(taskResult)) {
                predicate.and(builder.handTaskResult.isNotEmpty()).and(builder.handGoods.isEmpty());
            } else if(SerKnowledgeCase.TaskResult.Suspection.equals(taskResult)) {
                predicate.and(builder.handTaskResult.isNull());
                predicate.and((builder.judgeUserId.eq(Constants.DEFAULT_SYSTEM_USER).and(builder.scanAtrResult.eq(SerScan.ATRResult.TRUE)))
                        .or(builder.judgeUserId.ne(Constants.DEFAULT_SYSTEM_USER).and(builder.judgeResult.eq(SerJudgeGraph.Result.TRUE))));
            } else {
                predicate.and(builder.handTaskResult.isNull());
                predicate.and((builder.judgeUserId.eq(Constants.DEFAULT_SYSTEM_USER).and(builder.scanAtrResult.ne(SerScan.ATRResult.TRUE)))
                        .or(builder.judgeUserId.ne(Constants.DEFAULT_SYSTEM_USER).and(builder.judgeResult.ne(SerJudgeGraph.Result.TRUE))));
            }
        }
        if (fieldId != null) {
            predicate.and(builder.fieldId.eq(fieldId));
        }
        if (!StringUtils.isEmpty(handGoods)) {
            predicate.and(builder.handGoods.contains(handGoods));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            predicate.and(builder.createdBy.in(categoryUser.getUserIdList()).or(builder.editedBy.in(categoryUser.getUserIdList())));
        }

        if(isAll == false) {
            String[] splits = idList.split(",");
            List<Long> caseDealIdList = new ArrayList<>();
            for(String idStr: splits) {
                caseDealIdList.add(Long.valueOf(idStr));
            }
            predicate.and(builder.caseDealId.in(caseDealIdList));
        }

        Long limit = Constants.MAX_EXPORT_NUMBER;
        int currentPage = 0;
        int perPage = limit.intValue();
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by("caseDealId").descending());
        }

        List<SerKnowledgeCaseDealImage> dealList;
        dealList = StreamSupport
                .stream(serKnowledgeCaseDealImageRepository.findAll(predicate, pageRequest).spliterator(), false)
                .collect(Collectors.toList());

        //List<SerKnowledgeCaseDeal> exportList = getExportList(dealList, isAll, idList);
        return dealList;
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
    public boolean checkKnowledgeExistByTask(Long taskId) {
        return serKnowledgeCaseRepository.exists(QSerKnowledgeCase.serKnowledgeCase.taskId.eq(taskId));
    }

    @Override
    public void delete(Long caseDealId) {
        SerKnowledgeCase serKnowledgeCase = serKnowledgeCaseRepository.findOne(QSerKnowledgeCase.serKnowledgeCase.caseId.eq(caseDealId)).get();
        String valueBefore = getJsonFromKnowledge(serKnowledgeCase);
        serKnowledgeCaseRepository.delete(serKnowledgeCase);
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
        serKnowledgeCase.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());

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
            existKnowledgeCase.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        }
        else {

            Long knowledgeCaseId = existKnowledgeCase.getCaseId();
            knowledgeCase.setCaseId(knowledgeCaseId);
            existKnowledgeCase = knowledgeCase;
            existKnowledgeCase.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
            Iterable<SerTaskTag> taskTagList = serTaskTagRepository.findAll(QSerTaskTag.serTaskTag.taskId.eq(knowledgeCase.getTaskId()));
            serTaskTagRepository.deleteAll(taskTagList);
        }
        if(tagList != null && tagList.size() > 0) {
            List<SerTaskTag> taskTagList = new ArrayList<>();
            for(int i = 0; i < tagList.size(); i ++) {
                SerTaskTag taskTag = new SerTaskTag();
                taskTag.setTaskId(knowledgeCase.getTaskId());
                taskTag.setTagId(tagList.get(i));
                taskTag.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
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
     * update knowledge case
     * @param knowledgeId
     * @param knowledgeCase
     * @return
     */
    public Long updateKnowledgeCase(Long knowledgeId, SerKnowledgeCase knowledgeCase) {

        knowledgeCase.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        SerKnowledgeCase serKnowledgeCase = serKnowledgeCaseRepository.save(knowledgeCase);
        if (serKnowledgeCase != null) {
            return serKnowledgeCase.getCaseId();
        } else
            return null;
    }
}
