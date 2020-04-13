package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerKnowledgeCase;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerKnowledgeCaseDeal;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysManualGroup;
import com.nuctech.securitycheck.backgroundservice.repositories.SerKnowledgeCaseDealRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SerKnowledgeCaseRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SysManualGroupRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISysManualGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SysManualGroupServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SysManualGroupServiceImpl implements ISysManualGroupService {

    @Autowired
    private SysManualGroupRepository sysManualGroupRepository;

    @Autowired
    private SerKnowledgeCaseDealRepository serKnowledgeCaseDealRepository;

    @Autowired
    private SerKnowledgeCaseRepository serKnowledgeCaseRepository;

    /**
     * 安检设备和手检站关系 查询
     *
     * @return List<SysJudgeGroup>
     */
    @Override
    public List<SysManualGroup> findAll(SysManualGroup sysManualGroup) {
        Example<SysManualGroup> manualEx = Example.of(sysManualGroup);
        return sysManualGroupRepository.findAll(manualEx);
    }

    @Override
    public SysManualGroup findLastManualConfig(Long deviceId) {
        return sysManualGroupRepository.findLastJudgeConfig(deviceId);
    }

    /**
     * insert new knowledge case
     * @param knowledgeCase
     * @return
     */
    @Override
    public Long insertNewKnowledgeCase(SerKnowledgeCase knowledgeCase, Long userId) {
        knowledgeCase.setCreatedBy(userId);
        knowledgeCase.setCreatedTime(new Date());
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
    @Override
    public Long insertNewKnowledgeCaseDeal(SerKnowledgeCaseDeal knowledgeCaseDeal, Long userId) {
        knowledgeCaseDeal.setCreatedBy(userId);
        knowledgeCaseDeal.setCreatedTime(new Date());
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
    @Override
    public Long updateKnowledgeCase(Long knowledgeId, SerKnowledgeCase knowledgeCase) {
        SerKnowledgeCase serKnowledgeCase = serKnowledgeCaseRepository.save(knowledgeCase);
        if (serKnowledgeCase != null) {
            return serKnowledgeCase.getCaseId();
        } else
            return null;
    }
}
