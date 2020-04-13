/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerKnowledgeCase）
 * 文件名：	SerKnowledgeCase.java
 * 描述：	SerKnowledgeCase Model
 * 作者名：	Choe
 * 日期：	2019/11/26
 */

package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "ser_knowledge_case")
public class SerKnowledgeCase extends BaseEntity implements Serializable {

    public static class Status {
        public static final String SUBMIT_APPROVAL = "1000002501";
        public static final String DISMISS = "1000002502";
        public static final String SUCCESS_APPROVAL = "1000002503";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CASE_ID", length = 20)
    private Long caseId;

    @Column(name = "CASE_DETAL_ID", length = 20)
    private Long caseDealId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "CASE_STATUS", length = 20)
    private String caseStatus;

    @Column(name = "CASE_COLLECT_USERID", length = 20)
    private Long caseCollectUserId;

    @Column(name = "CASE_APPROVAL_USERID", length = 20)
    private Long caseApprovalUserId;




}
