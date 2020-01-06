package com.nuctech.securitycheck.backgroundservice.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysUser
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "sys_user")
public class SysUser extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "ORG_ID")
    private SysOrg sysOrg;

    @Column(name = "USER_NAME", length = 50)
    private String userName;

    @Column(name = "USER_ACCOUNT", length = 20)
    private String userAccount;

    @JsonIgnore
    @Column(name = "PASSWORD", length = 20)
    private String password;

    @Column(name = "DATA_RANGE_CATEGORY", length = 10)
    private String dataRangeCategory;

    @Column(name = "USER_NUMBER", length = 50)
    private String userNumber;

    @Column(name = "GENDER", length = 10)
    private String gender;

    @Column(name = "IDENTITY_CARD", length = 50)
    private String identityCard;

    @Column(name = "POST", length = 50)
    private String post;

    @Column(name = "EDUCATION", length = 10)
    private String education;

    @Column(name = "DEGREE", length = 10)
    private String degree;

    @Column(name = "EMAIL", length = 50)
    private String email;

    @Column(name = "MOBILE", length = 20)
    private String mobile;

    @Column(name = "ADDRESS", length = 50)
    private String address;

    @Column(name = "CATEGORY", length = 10)
    private String category;

    @Column(name = "STATUS", length = 10)
    private String status;

    @Column(name = "PORTRAIT", length = 200)
    private String portrait;

}
