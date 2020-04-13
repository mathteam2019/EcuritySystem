package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerKnowledgeCase;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerKnowledgeCaseDeal;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysManualGroup;

import java.util.List;

/**
 * ISysManualGroupService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISysManualGroupService {

    List<SysManualGroup> findAll(SysManualGroup sysManualGroup);

    SysManualGroup findLastManualConfig(Long deviceId);

    Long insertNewKnowledgeCase(SerKnowledgeCase knowledgeCase, Long userId);

    Long insertNewKnowledgeCaseDeal(SerKnowledgeCaseDeal knowledgeCaseDeal, Long userId);

    Long updateKnowledgeCase(Long knowledgeId, SerKnowledgeCase knowledgeCase);
}
