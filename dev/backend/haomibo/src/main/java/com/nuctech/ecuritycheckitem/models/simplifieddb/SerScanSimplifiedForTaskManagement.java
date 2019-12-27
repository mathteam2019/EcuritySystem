/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerScan）
 * 文件名：	SerScan.java
 * 描述：	SerScan Model
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
@JsonFilter(ModelJsonFilters.FILTER_SER_SCAN)
@Table(name = "ser_scan")
public class SerScanSimplifiedForTaskManagement extends BaseEntitySimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCAN_ID", length = 20)
    private Long scanId;

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "WORKFLOW_ID", length = 20)
    private Long workflowId;

    @Column(name = "SCAN_DEVICE_ID", length = 20)
    private Long scanDeviceId;

    @Column(name = "SCAN_IMAGE_ID", length = 20)
    private Long scanImageId;

    @Column(name = "SCAN_DEVICEIMAGES", length = 1000)
    private String scanDeviceImages;

    @Column(name = "SCAN_ATR_RESULT", length = 10)
    private String scanAtrResult;

    @Column(name = "SCAN_INVALID", length = 10)
    private String scanInvalid;

    @Column(name = "SCAN_FOOT_ALARM", length = 10)
    private String scanFootAlarm;

    @Column(name = "SCAN_START_TIME", nullable = false)
    private Date scanStartTime;

    @Column(name = "SCAN_END_TIME", nullable = false)
    private Date scanEndTime;

    @Column(name = "SCAN_POINTSMAN_ID", length = 20)
    private Long scanPointsmanId;

    @Column(name = "SCAN_ASSIGN_TIMEOUT", length = 10)
    private String scanAssignTimeout;

    @Column(name = "SCAN_IMAGEGENDER", length = 10)
    private String scanImageGender;

    @OneToOne()
    @JoinColumn(name = "SCAN_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysDeviceSimplifiedOnlyHasName scanDevice;

    @OneToOne()
    @JoinColumn(name = "SCAN_POINTSMAN_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SysUserSimplifiedOnlyHasName scanPointsman;

    @OneToOne()
    @JoinColumn(name = "SCAN_IMAGE_ID", referencedColumnName = "IMAGE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    SerImageSimplifiedForTaskManagement scanImage;

}