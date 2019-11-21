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

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_LOGIN_INFO)
@Table(name = "ser_login_info")
public class SerLoginInfo extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOGIN_INFO_ID", length = 20, nullable = false)
    Long loginInfoId;

    @Column(name = "USER_ID", length = 20)
    String userId;

    @Column(name = "DEVICE_ID", length = 20)
    String deviceId;

    @Column(name = "LOGIN_CATEGORY", length = 10)
    String loginCategory;



    
}
