/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（HistorySimplifiedForProcessTaskManagement）
 * 文件名：	HistorySimplifiedForProcessTaskManagement.java
 * 描述：	Simplified History Model only for Process task management response
 * 作者名：	Tiny
 * 日期：	2019/12/27
 */

package com.nuctech.ecuritycheckitem.models.simplifieddb;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
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
@JsonFilter(ModelJsonFilters.FILTER_HISTORY)
@Table(name = "history_invalid")
public class HistorySimplifiedForInvalidTaskManagement extends BaseEntitySimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID", length = 20)
    private Long historyId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "HAND_APPRAISE", length = 10)
    private String handAppraise;

    @Column(name = "HAND_APPRAISE2", length = 10)
    private String handAppraiseSecond;

    @Column(name = "SCAN_POINTSMAN_NAME", length = 50)
    private String scanPointsmanName;

    @Column(name = "JUDGE_USER_NAME", length = 50)
    private String judgeUserName;

    @Column(name = "HAND_USER_NAME", length = 50)
    private String handUserName;

    @Column(name = "FIELD_DESIGNATION", length = 50)
    private String fieldDesignation;

    @Column(name = "SCAN_DEVICE_NAME", length = 50)
    private String scanDeviceName;

    @Column(name = "JUDGE_DEVICE_NAME", length = 50)
    private String judgeDeviceName;

    @Column(name = "HAND_DEVICE_NAME", length = 50)
    private String handDeviceName;

}
