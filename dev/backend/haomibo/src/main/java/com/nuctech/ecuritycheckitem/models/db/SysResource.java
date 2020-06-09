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

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysResourceSimple;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Builder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
public class SysResource implements Serializable {

    public static class Category {
        public static final String ADMIN = "admin";
        public static final String USER = "user";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESOURCE_ID", length = 20, nullable = false)
    private Long resourceId;

    @Column(name = "PARENT_RESOURCE_ID", length = 20)
    private Long parentResourceId;

    @Column(name = "RESOURCE_NAME", length = 50)
    private String resourceName;

    @Column(name = "RESOURCE_CAPTION", length = 50)
    private String resourceCaption;

    @Column(name = "RESOURCE_CAPTION_EN", length = 50)
    private String resourceCaptionEnglish;

    @Column(name = "RESOURCE_URL", length = 200)
    private String resourceUrl;

    @Column(name = "RESOURCE_CATEGORY", length = 10)
    private String resourceCategory;

    @Column(name = "CREATEDBY", length = 20)
    private Long createdBy;

    @Column(name = "CREATEDTIME", nullable = false)
    private Date createdTime;

    @Column(name = "EDITEDBY", length = 20)
    private Long editedBy;

    @Column(name = "EDITEDTIME", nullable = false)
    private Date editedTime;

    @Column(name = "NOTE", length = 500, nullable = false)
    private String note;

//    @ToString.Exclude
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "PARENT_RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    private SysResourceSimple parent;


}

