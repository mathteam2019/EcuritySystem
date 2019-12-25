/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/24
 * @CreatedBy Sandy.
 * @FileName SysResource.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

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


}

