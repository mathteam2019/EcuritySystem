/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysResource）
 * 文件名：	SysResource.java
 * 描述：	SysResource Model
 * 作者名：	Sandy
 * 日期：	2019/10/24
 */

package com.nuctech.ecuritycheckitem.models.simplifieddb;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_RESOURCE)
@Table(name = "sys_resource")
public class SysResourceSimple implements Serializable {

    public static class Category {
        public static final String ADMIN = "admin";
        public static final String USER = "user";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESOURCE_ID", length = 20, nullable = false)
    private Long resourceId;

    @Column(name = "RESOURCE_NAME", length = 50)
    private String resourceName;

    @Column(name = "RESOURCE_CAPTION", length = 50)
    private String resourceCaption;

}

