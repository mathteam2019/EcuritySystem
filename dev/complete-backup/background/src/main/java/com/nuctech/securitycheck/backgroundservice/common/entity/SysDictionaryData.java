package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysDictionaryData
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
@Table(name = "sys_dictionary_data")
public class SysDictionaryData extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_ID", length = 20, nullable = false)
    private Long dataId;

    @Column(name = "DICTIONARY_ID", length = 20)
    private Long dictionaryId;

    @Column(name = "DATA_CODE", length = 10)
    private Long dataCode;

    @Column(name = "DATA_VALUE", length = 200)
    private String dataValue;

}