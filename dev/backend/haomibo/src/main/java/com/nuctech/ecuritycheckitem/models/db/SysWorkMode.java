/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysWorkMode）
 * 文件名：	SysWorkMode.java
 * 描述：	SysWorkMode Model
 * 作者名：	Tiny
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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GenerationType;
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
public class SysWorkMode implements Serializable {

    public static class WorkModeValue {

        public static final String MODE_1000001301 = "1000001301";
        public static final String MODE_1000001302 = "1000001302";
        public static final String MODE_1000001303 = "1000001303";
        public static final String MODE_1000001304 = "1000001304";

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MODE_ID", length = 20, nullable = false)
    private Long modeId;


    @Column(name = "MODE_NAME", length = 20)
    private String modeName;


}
