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
@JsonFilter(ModelJsonFilters.FILTER_SYS_DICTONARY_DATA)
@Table(name = "sys_dictionary_data")
public class SysDictionaryData extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_ID", length = 20, nullable = false)
    private Long dataId;

    @Column(name = "DICTIONARY_ID", length = 20)
    private Long dictionaryId;

    @Column(name = "DATA_CODE", length = 10)
    private String dataCode;

    @Column(name = "DATA_VALUE", length = 100)
    private String dataValue;

}
