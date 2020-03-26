/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（KnowledgeService）
 * 文件名：	KnowledgeService.java
 * 描述：	KnowledgeService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.knowledgemanagement;

import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCase;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDealImage;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface KnowledgeService {
    PageResult<SerKnowledgeCaseDeal> getDealListByFilter(String sortBy, String order, String caseStatus, String taskNumber, String modeName, String taskResult,
                                                         Long fieldId, String handGoods, int currentPage, int perPage);

    List<SerKnowledgeCaseDeal> getDealExportList(String sortBy, String order, String caseStatus, String taskNumber, String modeName, String taskResult,
                                                 Long fieldId, String handGoods, boolean isAll, String idList);

    List<SerKnowledgeCaseDealImage> getDealImageList(String sortBy, String order, String caseStatus, String taskNumber, String modeName, String taskResult,
                                                      Long fieldId, String handGoods, boolean isAll, String idList);

    boolean checkKnowledgeExist(Long caseId);

    boolean checkKnowledgeExistByTask(Long taskId);

    void updateStatus(Long caseId, String status);

    void delete(Long caseDealId);

    Long insertNewKnowledgeCase(SerKnowledgeCase knowledgeCase, List<String> tagList);

    Long insertNewKnowledgeCaseDeal(SerKnowledgeCaseDeal knowledgeCaseDeal);

    Long updateKnowledgeCase(Long knowledgeId, SerKnowledgeCase knowledgeCase);

}
