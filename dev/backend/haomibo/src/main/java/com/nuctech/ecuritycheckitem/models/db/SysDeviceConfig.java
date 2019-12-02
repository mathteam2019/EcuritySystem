/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/20
 * @CreatedBy Choe.
 * @FileName SysDeviceConfig.java
 * @ModifyHistory
 *
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CONFIG)
@Table(name = "sys_device_config")
public class SysDeviceConfig extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIG_ID", length = 20, nullable = false)
    Long configId;

    @Column(name = "MODE_ID", length = 20)
    Long modeId;

//    @Column(name = "FIELD_ID", length = 20)
//    Long fieldId;

    @Column(name = "DEVICE_ID", length = 20)
    Long deviceId;

    @Column(name = "MANUAL_SWITCH", length = 10)
    String manualSwitch;

    @Column(name = "ATR_SWITCH", length = 10)
    String atrSwitch;

    @Column(name = "MAN_REMOTE_GENDER", length = 10)
    String manRemoteGender;

    @Column(name = "WOMAN_REMOTE_GENDER", length = 10)
    String womanRemoteGender;

    @Column(name = "MAN_MANUAL_GENDER", length = 10)
    String manManualGender;

    @Column(name = "WOMAN_MANUAL_GENDER", length = 10)
    String womanManualGender;

    @Column(name = "MAN_DEVICE_GENDER", length = 10)
    String manDeviceGender;

    @Column(name = "WOMAN_DEVICE_GENDER", length = 10)
    String womanDeviceGender;


    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "MODE_ID", referencedColumnName = "MODE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("manual")
    private SysWorkMode workMode; // Relation to SysWorkMode table.


    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    private SysDevice device; // Relation to SysDevice table.

    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "CONFIG_ID", referencedColumnName = "CONFIG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("manual")
    private List<SysManualGroup> manualGroupList; // Relation to SysManualGroup table.

    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "CONFIG_ID", referencedColumnName = "CONFIG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("judge")
    private List<SysJudgeGroup> judgeGroupList; // Relation to SysJudgeGroup table.

    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "CONFIG_ID", referencedColumnName = "CONFIG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("from_config")
    private List<FromConfigId> fromConfigIdList; // Relation to FromConfigId table.

    @javax.persistence.Transient
    String fromConfigDeviceName;

}
