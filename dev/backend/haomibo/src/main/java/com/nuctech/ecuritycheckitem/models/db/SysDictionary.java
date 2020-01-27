/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysDictionary）
 * 文件名：	SysDictionary.java
 * 描述：	SysDictionary Model
 * 作者名：	Choe
 * 日期：	2020/01/10
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
@JsonFilter(ModelJsonFilters.FILTER_SYS_DICTONARY)
@Table(name = "sys_dictionary")
public class SysDictionary extends BaseEntity implements Serializable {

    public static class Type {
        public static final Integer ADMIN = 1;
        public static final Integer USER = 2;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DICTIONARY_ID", length = 20, nullable = false)
    private Long dictionaryId;

    @Column(name = "DICTIONARY_NAME", length = 50)
    private String dictionaryName;

    @Column(name = "DICTIONARY_TYPE", length = 1)
    private Integer dictionaryType;



}
