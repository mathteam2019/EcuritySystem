/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerJudgeGraph）
 * 文件名：	SerJudgeGraph.java
 * 描述：	SerJudgeGraph Model
 * 作者名：	Tiny
 * 日期：	2019/12/27
 */

package com.nuctech.ecuritycheckitem.models.simplifieddb;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
@JsonFilter(ModelJsonFilters.FILTER_SER_JUDGE_GRAPH)
@Table(name = "ser_judge_graph")
public class SerJudgeGraphSimplifiedForHistoryTaskManagement extends BaseEntitySimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JUDGE_ID", length = 20)
    private Long judgeId;

    @Column(name = "JUDGE_SUBMITRECTS", length = 255)
    private String judgeSubmitrects;

}
