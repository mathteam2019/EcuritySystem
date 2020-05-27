/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerKnowledgeCaseDeal）
 * 文件名：	SerKnowledgeCaseDeal.java
 * 描述：	SerKnowledgeCaseDeal Model
 * 作者名：	Choe
 * 日期：	2019/11/26
 */

package com.nuctech.ecuritycheckitem.models.db;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerScanSimplifiedForTaskManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimple;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_KNOWLEDGE_CASE_DEAL)
@Table(name = "ser_knowledge_case")
public class SerKnowledgeCaseDealImage extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CASE_ID", length = 20)
    private Long caseDealId;

    @Column(name = "CASE_STATUS", length = 20)
    private String caseStatus;

    @Column(name = "MODE_NAME", length = 50)
    private String modeName;

    @Column(name = "HAND_TASK_RESULT", length = 10)
    private String handTaskResult;

    @Column(name = "HAND_GOODS", length = 255)
    private String handGoods;

    @Column(name = "SCENE", length = 20)
    private Long fieldId;

    @Column(name = "TASK_NUMBER", length = 50)
    private String taskNumber;

    @Column(name = "JUDGE_USER_ID", length = 20)
    private Long judgeUserId;

    @Column(name = "SCAN_ATR_RESULT", length = 10)
    private String scanAtrResult;

    @Column(name = "JUDGE_RESULT", length = 10)
    private String judgeResult;

    @OneToOne()
    @JoinColumn(name = "SCAN_ID", referencedColumnName = "SCAN_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerScanSimplifiedForTaskManagement serScan;




}
