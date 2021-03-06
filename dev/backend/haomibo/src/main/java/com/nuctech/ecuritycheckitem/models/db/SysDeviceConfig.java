/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysDeviceConfig）
 * 文件名：	SysDeviceConfig.java
 * 描述：	SysDeviceConfig Model
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

    public static class Status {
        public static final String ACTIVE = "1000000701";
        public static final String INACTIVE = "1000000702";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIG_ID", length = 20, nullable = false)
    private Long configId;

    @Column(name = "MODE_ID", length = 20)
    private Long modeId;

//    @Column(name = "FIELD_ID", length = 20)
//    Long fieldId;

    @Column(name = "DEVICE_ID", length = 20)
    private Long deviceId;

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

    @Column(name = "STATUS", length = 10)
    private String status;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODE_ID", referencedColumnName = "MODE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("manual")
    private SysWorkMode workMode; // Relation to SysWorkMode table.


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    private SysDevice device; // Relation to SysDevice table.

    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "CONFIG_ID", referencedColumnName = "CONFIG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("manual")
    @OrderBy("manualGroupId ASC")
    private List<SysManualGroup> manualGroupList; // Relation to SysManualGroup table.

    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "CONFIG_ID", referencedColumnName = "CONFIG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("judge")
    @OrderBy("judgeGroupId ASC")
    private List<SysJudgeGroup> judgeGroupList; // Relation to SysJudgeGroup table.

    @ToString.Exclude
    @OneToMany()
    @JoinColumn(name = "CONFIG_ID", referencedColumnName = "CONFIG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("from_config")
    @OrderBy("fromConfigId ASC")
    private List<FromConfigId> fromConfigIdList; // Relation to FromConfigId table.

    @javax.persistence.Transient
    private String fromConfigDeviceName;

}
