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
import com.nuctech.ecuritycheckitem.models.db.SerTaskTag;
import com.nuctech.ecuritycheckitem.models.db.SysWorkMode;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_HISTORY)
@Table(name = "history")
public class HistorySimplifiedForHistoryTableManagement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID", length = 20)
    private Long historyId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "SCAN_START_TIME", nullable = false)
    private Date scanStartTime;

    @Column(name = "SCAN_END_TIME", nullable = false)
    private Date scanEndTime;


    @Column(name = "HAND_TASK_RESULT", length = 10)
    private String handTaskResult;



    @OneToOne()
    @JoinColumn(name = "MODE", referencedColumnName = "MODE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysWorkMode workMode;

    @OneToOne()
    @JoinColumn(name = "SCAN_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDeviceSimplifiedOnlyHasName scanDevice;

    @OneToOne()
    @JoinColumn(name = "SCAN_POINTSMAN_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUserSimplifiedOnlyHasName scanPointsman;


    @OneToOne()
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerTaskSimplifiedForHistoryTaskManagement task;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerCheckResultSimplifiedForProcessTaskManagement serCheckResult;

//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    private SerCheckResultSimplifiedForProcessTaskManagement serCheckResult;

//    @OneToOne()
//    @JoinColumn(name = "SCAN_ID", referencedColumnName = "SCAN_ID", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    private SerScanSimplifiedForTaskManagement serScan;


}
