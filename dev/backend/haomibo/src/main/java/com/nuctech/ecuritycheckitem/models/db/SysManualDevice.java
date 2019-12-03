/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/20
 * @CreatedBy Choe.
 * @FileName SysManualDevice.java
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
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_MANUAL_DEVICE)
@Table(name = "sys_manual_device")
public class SysManualDevice extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MANUAL_DEVICE_ID", length = 20, nullable = false)
    Long manualDeviceId;


    @Column(name = "DEVICE_STATUS", length = 10)
    String deviceStatus;


    @Column(name = "DEVICE_STRATEGY", length = 10)
    String deviceStrategy;

    @Column(name = "DEVICE_CHECKER_GENDER", length = 10)
    String deviceCheckGender;

    @ToString.Exclude
    @OneToOne()
    @JoinColumn(name = "MANUAL_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    SysDevice device;

}
