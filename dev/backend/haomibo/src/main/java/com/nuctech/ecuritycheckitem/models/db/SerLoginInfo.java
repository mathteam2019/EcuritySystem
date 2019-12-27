/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerLoginInfo）
 * 文件名：	SerLoginInfo.java
 * 描述：	SerLoginInfo Model
 * 作者名：	Choe
 * 日期：	2019/11/26
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
@JsonFilter(ModelJsonFilters.FILTER_SER_LOGIN_INFO)
@Table(name = "ser_login_info")
public class SerLoginInfo extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOGIN_INFO_ID", length = 20, nullable = false)
    private Long loginInfoId;

    @Column(name = "USER_ID", length = 20)
    private String userId;

    @Column(name = "DEVICE_ID", length = 20)
    private String deviceId;

    @Column(name = "LOGIN_CATEGORY", length = 10)
    private String loginCategory;



    
}
