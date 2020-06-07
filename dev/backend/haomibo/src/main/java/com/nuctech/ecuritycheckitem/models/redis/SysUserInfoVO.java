package com.nuctech.ecuritycheckitem.models.redis;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysUser
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_user")
public class SysUserInfoVO extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "ORG_ID")
    private SysOrgInfoVO sysOrg;

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

    @Column(name = "FAIL_COUNT", length = 10)
    private Integer failCount;

    @Column(name = "DEFAULT_USER", length = 10)
    private Integer isDefaultUser;

}
