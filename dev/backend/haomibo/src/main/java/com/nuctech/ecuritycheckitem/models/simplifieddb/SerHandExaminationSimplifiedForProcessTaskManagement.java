/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerHandExaminationSimplifiedForProcessTaskManagement）
 * 文件名：	SerHandExaminationSimplifiedForProcessTaskManagement.java
 * 描述：	Simplified SerHandExamination Model for process task management
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
import lombok.AllArgsConstructor;
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
@JsonFilter(ModelJsonFilters.FILTER_SER_HAND_EXAMINATION)
@Table(name = "ser_hand_examination")
public class SerHandExaminationSimplifiedForProcessTaskManagement extends BaseEntitySimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HAND_EXAMINATION_ID", length = 20)
    private Long handExaminationId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "WORKFLOW_ID", length = 20)
    private Long workflowId;

    @Column(name = "HAND_DEVICE_ID", length = 20)
    private Long handDeviceId;

    @Column(name = "HAND_RESULT", length = 10)
    private String handResult;

    @Column(name = "HAND_START_TIME", nullable = false)
    private Date handStartTime;

    @Column(name = "HAND_END_TIME", nullable = false)
    private Date handEndTime;

    @Column(name = "HAND_USER_ID", length = 20)
    private Long handUserId;

    @OneToOne()
    @JoinColumn(name = "HAND_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDeviceSimplifiedOnlyHasName handDevice;

    @OneToOne()
    @JoinColumn(name = "HAND_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysUserSimplifiedOnlyHasName handUser;

}
