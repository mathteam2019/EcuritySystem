/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/20
 * @CreatedBy Choe.
 * @FileName SysManualGroup.java
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

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_JUDGE_GROUP)
@Table(name = "sys_judge_group")
public class SysJudgeGroup extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JUDGE_GROUP_ID", length = 20, nullable = false)
    private Long judgeGroupId;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    private Long judgeDeviceId;


    @Column(name = "CONFIG_ID", length = 20)
    private Long configId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JUDGE_DEVICE_ID", referencedColumnName = "DEVICE_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    private SysDevice judgeDevice; // Relation to SysJudgeDevice table.
    
}
