/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerCheckResult）
 * 文件名：	SerCheckResult.java
 * 描述：	SerCheckResult Model
 * 作者名：	Tiny
 * 日期：	2019/12/27
 */

package com.nuctech.ecuritycheckitem.models.db;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_CHECK_RESULT)
@Table(name = "ser_check_result")
public class SerCheckResult extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHECK_RESULT_ID", length = 20)
    private Long checkResultId;

    @Column(name = "DEVICE_ID", length = 20)
    private Long deviceId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "CONCLUSION_TYPE", length = 10)
    private String conclusiontype;

    @Column(name = "JUDGE_RESULT", length = 10)
    private String judgeResult;

    @Column(name = "HAND_TASK_RESULT", length = 10)
    private String handTaskResult;

    @Column(name = "HAND_GOODS", length = 10)
    private String handGoods;

    @Column(name = "HAND_GOODS_GRADE", length = 10)
    private String handGoodsGrade;

    @Column(name = "HAND_COLLECT_SIGN", length = 10)
    private String handCollectSign;

    @Column(name = "HAND_ATTACHED", length = 10)
    private String handAttached;

    @Column(name = "HAND_COLLECT_LABEL", length = 10)
    private String handCollectLabel;

    @Column(name = "HAND_APPRAISE", length = 10)
    private String handAppraise;

    @Column(name = "HAND_SUBMITRECTS", length = 10)
    private String handSubmitrects;


}
