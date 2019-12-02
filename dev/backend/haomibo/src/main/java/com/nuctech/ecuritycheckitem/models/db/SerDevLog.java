/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName SerDevLog.java
 * @ModifyHistory
 *
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_DEV_LOG)
@Table(name = "ser_dev_log")
public class SerDevLog extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    Long id;

    @Column(name = "GUID", length = 255)
    String guid;

    @Column(name = "DEV_TYPE", length = 10)
    String devType;

    @Column(name = "LOGIN_NAME", length = 20)
    String loginName;

    @Column(name = "CATEGORY", length = 2)
    Long category;

    @Column(name = "LEVEL", length = 11)
    Long level;

    @Column(name = "CONTENT", length = 2000)
    String content;

    @Column(name = "TIME", nullable = false)
    Date time;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "GUID", referencedColumnName = "GUID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    SysDevice device;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "LOGIN_NAME", referencedColumnName = "USER_ACCOUNT", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("user")
    SysUser user;



    
}
