package com.haomibo.haomibo.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.haomibo.haomibo.jsonfilter.ModelJsonFilters;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESOURCE_ID", length = 20, nullable = false)
    Long resourceId;

    @Column(name = "PARENT_RESOURCE_ID", length = 20)
    Long parentResourceId;

    @Column(name = "RESOURCE_NAME", length = 50)
    String resourceName;

    @Column(name = "RESOURCE_CAPTION", length = 50)
    String resourceCaption;

    @Column(name = "RESOURCE_URL", length = 200)
    String resourceUrl;

    @Column(name = "RESOURCE_CATEGORY", length = 10)
    String resourceCategory;

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

