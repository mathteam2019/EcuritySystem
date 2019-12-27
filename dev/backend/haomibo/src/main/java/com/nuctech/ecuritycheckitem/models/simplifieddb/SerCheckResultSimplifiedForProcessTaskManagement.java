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

package com.nuctech.ecuritycheckitem.models.simplifieddb;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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
public class SerCheckResultSimplifiedForProcessTaskManagement extends BaseEntitySimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHECK_RESULT_ID", length = 20)
    private Long checkResultId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "HAND_GOODS", length = 255)
    private String handGoods;

    @Column(name = "HAND_ATTACHED", length = 10)
    private String handAttached;


}
