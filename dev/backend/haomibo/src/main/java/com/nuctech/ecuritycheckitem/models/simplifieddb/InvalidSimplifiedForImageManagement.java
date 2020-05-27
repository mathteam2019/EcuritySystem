/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（HistorySimplifiedForHistoryTaskManagement）
 * 文件名：	HistorySimplifiedForHistoryTaskManagement.java
 * 描述：	Simplified History Model
 * 作者名：	Tiny
 * 日期：	2019/12/27
 */

package com.nuctech.ecuritycheckitem.models.simplifieddb;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
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
@JsonFilter(ModelJsonFilters.FILTER_HISTORY)
@Table(name = "history_invalid")
public class InvalidSimplifiedForImageManagement extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID", length = 20)
    private Long historyId;


    @Column(name = "SCAN_START_TIME", nullable = false)
    private Date scanStartTime;

    @Column(name = "TASK_NUMBER", length = 50)
    private String taskNumber;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "MODE", length = 20)
    private Long modeId;

    @Column(name = "SCENE", length = 20)
    private Long fieldId;

    @Column(name = "SCAN_POINTSMAN_NAME", length = 50)
    private String scanPointsManName;


    @OneToOne()
    @JoinColumn(name = "SCAN_ID", referencedColumnName = "SCAN_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerScanSimplifiedForTaskManagement serScan;


}
