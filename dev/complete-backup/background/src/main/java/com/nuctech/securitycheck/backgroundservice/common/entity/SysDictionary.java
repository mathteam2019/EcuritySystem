package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysDictionary
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
@Table(name = "sys_dictionary")
public class SysDictionary extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DICTIONARY_ID", length = 20, nullable = false)
    private Long dictionaryId;

    @Column(name = "DICTIONARY_NAME", length = 50)
    private String dictionaryName;

}
