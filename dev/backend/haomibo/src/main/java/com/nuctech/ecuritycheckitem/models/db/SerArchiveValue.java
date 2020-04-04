/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerArchiveValue）
 * 文件名：	SerArchiveValue.java
 * 描述：	SerArchiveValue Model
 * 作者名：	Choe
 * 日期：	2019/11/19
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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
@JsonFilter(ModelJsonFilters.FILTER_SER_ARCHIVES_VALUE)
@Table(name = "ser_archives_value")
public class SerArchiveValue extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VALUE_ID", length = 20, nullable = false)
    private Long id;

    @Column(name = "INDICATORS_ID", length = 20)
    private Long indicatorsId;

    @Column(name = "ARCHIVE_ID", length = 20)
    private Long archiveId;

    @Column(name = "VALUE", length = 255)
    private String value;

//    @ToString.Exclude
//    @ManyToOne()
//    @JoinColumn(name = "INDICATORS_ID", referencedColumnName = "INDICATORS_ID", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    private SerArchiveIndicators indicators; // Relation to SerArchivesValue table.


}
