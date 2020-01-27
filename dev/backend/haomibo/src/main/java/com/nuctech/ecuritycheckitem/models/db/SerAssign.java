/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（FromConfigId）
 * 文件名：	FromConfigId.java
 * 描述：	FromConfigId Model
 * 作者名：	Choe
 * 日期：	2020/01/02
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysDeviceSimplifiedOnlyHasName;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysUserSimplifiedOnlyHasName;
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
@JsonFilter(ModelJsonFilters.FILTER_SER_ASSIGN)
@Table(name = "ser_assign")
public class SerAssign extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ASSIGN_ID", length = 20)
    private Long assignId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;


    @Column(name = "ASSIGN_START_TIME", nullable = false)
    private Date assignStartTime;

    @Column(name = "ASSIGN_END_TIME", nullable = false)
    private Date assignEndTime;

    @Column(name = "ASSIGN_JUDGE_DEVICE_ID", length = 20)
    private Long assignJudgeDeviceId;

    @Column(name = "ASSIGN_HAND_DEVICE_ID", length = 20)
    private Long assignHandDeviceId;


    @OneToOne()
    @JoinColumn(name = "ASSIGN_JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDeviceSimplifiedOnlyHasName judgeDevice;

    @OneToOne()
    @JoinColumn(name = "ASSIGN_HAND_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDeviceSimplifiedOnlyHasName handDevice;

    @OneToOne()
    @JoinColumn(name = "ASSIGN_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUserSimplifiedOnlyHasName assignUser;

}
