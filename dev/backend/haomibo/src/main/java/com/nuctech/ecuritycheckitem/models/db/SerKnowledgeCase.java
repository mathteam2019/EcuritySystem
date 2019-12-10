/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName SerKnowledgeCase.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_KNOWLEDGE_CASE)
@Table(name = "ser_knowledge_case")
public class SerKnowledgeCase extends BaseEntity implements Serializable {

    public static class Status {
        public static final String SUBMIT_APPROVAL = "submit_approval";
        public static final String DISMISS = "dismiss";
        public static final String SUCCESS_APPROVAL = "success_approval";
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
