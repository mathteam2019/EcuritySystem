/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysJudgeGroup）
 * 文件名：	SysJudgeGroup.java
 * 描述：	SysJudgeGroup Model
 * 作者名：	Choe
 * 日期：	2019/11/20
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_JUDGE_GROUP)
@Table(name = "sys_judge_group")
public class SysJudgeGroup extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JUDGE_GROUP_ID", length = 20, nullable = false)
    private Long judgeGroupId;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    private Long judgeDeviceId;


    @Column(name = "CONFIG_ID", length = 20)
    private Long configId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    private SysDevice judgeDevice; // Relation to SysJudgeDevice table.

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "JUDGE_DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("judge_device")
    private SysJudgeDevice sysJudgeDevice; // Relation to SysJudgeDevice table.
    
}
