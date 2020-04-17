package com.nuctech.ecuritycheckitem.models.redis;

import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import com.nuctech.ecuritycheckitem.models.db.SysWorkMode;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysFieldSimplifiedForProcessTaskManagement;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysDeviceConfig
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_device_config")
public class SysDeviceConfigSimple extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIG_ID", length = 20, nullable = false)
    private Long configId;

    @ManyToOne
    @JoinColumn(name = "MODE_ID")
    private SysWorkModeSimple sysWorkMode;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    private SysDeviceRedis sysDevice;

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
