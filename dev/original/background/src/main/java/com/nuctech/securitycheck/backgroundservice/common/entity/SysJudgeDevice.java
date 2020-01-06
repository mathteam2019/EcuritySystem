package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysJudgeDevice
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "sys_judge_device")
public class SysJudgeDevice extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JUDGE_DEVICE_ID", length = 20, nullable = false)
    private Long judgeDeviceId;

    @Column(name = "DEVICE_STATUS", length = 10)
    private String deviceStatus;

    @Column(name = "DEVICE_STRATEGY", length = 10)
    private String deviceStrategy;

    @Column(name = "DEVICE_CHECKER_GENDER", length = 10)
    private String deviceCheckGender;

}
