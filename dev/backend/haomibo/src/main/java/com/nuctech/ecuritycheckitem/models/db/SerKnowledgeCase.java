package com.nuctech.ecuritycheckitem.models.db;

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
@Table(name = "ser_knowledge_case_deal")
public class SerKnowledgeCase extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CASE_DETAL_ID", length = 20)
    Long caseDealId;

    @Column(name = "CASE_ID", length = 20)
    Long caseId;

    @Column(name = "TASK_ID", length = 20)
    Long taskId;

    @Column(name = "CASE_STATUS", length = 10)
    String caseStatus;

    @Column(name = "CASE_COLLECT_USERID", length = 20)
    Long caseCollectUserId;

    @Column(name = "CASE_APPROVAL_USERID", length = 20)
    Long caseApprovalUserId;




}
