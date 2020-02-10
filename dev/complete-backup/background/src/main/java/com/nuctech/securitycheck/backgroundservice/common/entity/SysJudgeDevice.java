package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
    @Column(name = "ID", length = 20, nullable = false)
    private Long id;

    @Column(name = "JUDGE_DEVICE_ID", length = 20, nullable = false)
    private Long judgeDeviceId;

    @Column(name = "DEVICE_STATUS", length = 10)
    private String deviceStatus;

    @Column(name = "DEVICE_STRATEGY", length = 10)
    private String deviceStrategy;

    @Column(name = "DEVICE_CHECKER_GENDER", length = 10)
    private String deviceCheckGender;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysDevice device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(SELECT login.LOGIN_INFO_ID FROM ser_login_info login WHERE login.DEVICE_ID = JUDGE_DEVICE_ID ORDER BY login.CREATEDTIME DESC LIMIT 1)")
    @NotFound(action = NotFoundAction.IGNORE)
    private SerLoginInfo loginInfo;

}
