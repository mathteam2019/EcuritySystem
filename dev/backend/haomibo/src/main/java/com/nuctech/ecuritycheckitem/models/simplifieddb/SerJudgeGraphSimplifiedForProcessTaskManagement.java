/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerJudgeGraphSimplifiedForProcessTaskManagement）
 * 文件名：	SerJudgeGraphSimplifiedForProcessTaskManagement.java
 * 描述：	Simplified SerJudgeGraph Model for process task management
 * 作者名：	Tiny
 * 日期：	2019/12/27
 */

package com.nuctech.ecuritycheckitem.models.simplifieddb;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.GenerationType;
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
public class SerJudgeGraphSimplifiedForProcessTaskManagement extends BaseEntitySimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JUDGE_ID", length = 20)
    private Long judgeId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    private Long judgeDeviceId;

    @Column(name = "JUDGE_RESULT", length = 10)
    private String judgeResult;

    @Column(name = "JUDGE_TIMEOUT", length = 10)
    private String judgeTimeout;

    @Column(name = "JUDGE_SUBMITRECTS", length = 255)
    private String judgeSubmitrects;

    @Column(name = "JUDGE_TIME", length = 255)
    private String judgeTime;

    @Column(name = "JUDGE_START_TIME", nullable = false)
    private Date judgeStartTime;

    @Column(name = "JUDGE_END_TIME", nullable = false)
    private Date judgeEndTime;

    @Column(name = "JUDGE_USER_ID", length = 20)
    private Long judgeUserId;

    @OneToOne()
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDeviceSimplifiedOnlyHasName judgeDevice;

    @OneToOne()
    @JoinColumn(name = "JUDGE_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUserSimplifiedOnlyHasName judgeUser;

}
