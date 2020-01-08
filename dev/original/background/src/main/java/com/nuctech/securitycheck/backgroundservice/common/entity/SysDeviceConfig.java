package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysDeviceConfig
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
@Table(name = "sys_device_config")
public class SysDeviceConfig extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIG_ID", length = 20, nullable = false)
    private Long configId;

    @ManyToOne
    @JoinColumn(name = "MODE_ID")
    private SysWorkMode sysWorkMode;

    @ManyToOne
    @JoinColumn(name = "FIELD_ID")
    private SysField sysField;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    private SysDevice sysDevice;

    @Column(name = "MANUAL_SWITCH", length = 10)
    private String manualSwitch;

    @Column(name = "ATR_SWITCH", length = 10)
    private String atrSwitch;

    @Column(name = "MAN_REMOTE_GENDER", length = 10)
    private String manRemoteGender;

    @Column(name = "WOMAN_REMOTE_GENDER", length = 10)
    private String womanRemoteGender;

    @Column(name = "MAN_MANUAL_GENDER", length = 10)
    private String manManualGender;

    @Column(name = "WOMAN_MANUAL_GENDER", length = 10)
    private String womanManualGender;

    @Column(name = "MAN_DEVICE_GENDER", length = 10)
    private String manDeviceGender;

    @Column(name = "WOMAN_DEVICE_GENDER", length = 10)
    private String womanDeviceGender;

}