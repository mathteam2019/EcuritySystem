/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/20
 * @CreatedBy Choe.
 * @FileName SysWorkMode.java
 * @ModifyHistory
 *
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE)
@Table(name = "sys_work_mode")
public class SysWorkMode extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MODE_ID", length = 20, nullable = false)
    Long modeId;


    @Column(name = "MODE_NAME", length = 20)
    String modeName;


}
