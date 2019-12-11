package com.nuctech.ecuritycheckitem.service.knowledgemanagement;

import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface KnowledgeService {
    PageResult<SerKnowledgeCaseDeal> getDealListByFilter(String caseStatus, String taskNumber, String modeName, String taskResult,
                                                         String fieldDesignation, String handGoods, int currentPage, int perPage);

    List<SerKnowledgeCaseDeal> getDealExportList(String caseStatus, String taskNumber, String modeName, String taskResult,
                                                 String fieldDesignation, String handGoods, boolean isAll, String idList);

    boolean checkKnowledgeExist(Long caseId);

    void updateStatus(Long caseId, String status);
}
