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
import java.util.Date;

/**
 * SysDevice
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
@Table(name = "sys_device")
public class SysDeviceDetail extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_ID", length = 20, nullable = false)
    private Long deviceId;

    @Column(name = "GUID", length = 255)
    private String guid;

    @Column(name = "DEVICE_NAME", length = 50)
    private String deviceName;

    @Column(name = "DEVICE_TYPE", length = 50)
    private String deviceType;

    @Column(name = "STATUS", length = 10)
    private String status;


    @Column(name = "CURRENT_STATUS", length = 30)
    private String currentStatus;

    @Column(name = "WORK_STATUS", length = 30)
    private String workStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SerDeviceStatus deviceStatus;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(SELECT login.LOGIN_INFO_ID FROM ser_login_info login WHERE login.DEVICE_ID = DEVICE_ID ORDER BY login.CREATEDTIME DESC LIMIT 1)")
    @NotFound(action = NotFoundAction.IGNORE)
    private SerLoginInfoDetail loginInfo;

}
