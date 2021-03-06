/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysUser）
 * 文件名：	SysUser.java
 * 描述：	SysUser Model
 * 作者名：	Sandy
 * 日期：	2019/10/14
 */

package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysRoleSimple;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_USER)
@Table(name = "sys_user")
public class SysUser extends BaseEntity implements Serializable {


    public static class Gender {
        public static final String MALE = "1000000001";
        public static final String FEMALE = "1000000002";
        public static final String OTHER = "1000000003";
    }

    public static class Status {
        public static final String ACTIVE = "1000000301";
        public static final String INACTIVE = "1000000302";
        public static final String PENDING = "1000000304";
        public static final String BLOCKED = "1000000303";
    }

    public static class Category {
        public static final String ADMIN = "admin";
        public static final String NORMAL = "normal";
    }

    @AllArgsConstructor
    @Getter
    public enum DataRangeCategory {
        PERSON("1000000501"),
        ORG("1000000502"),
        ORG_DESC("1000000503"),
        ALL("1000000504"),
        SPECIFIED("1000000505"),
        EVERYONE("1000000506");

        String value;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "ORG_ID", length = 20)
    private Long orgId;

    @Column(name = "USER_NAME", length = 50)
    private String userName;

    @Column(name = "USER_ACCOUNT", length = 20)
    private String userAccount;

    @Column(name = "PASSWORD", length = 100)
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

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @Column(name = "DEFAULT_USER", length = 10)
    private Integer isDefaultUser;



    @Column(name = "FAIL_COUNT", length = 10)
    private Integer failCount;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "ORG_ID", referencedColumnName = "ORG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("org")
    private SysOrg org; // Relation to SysOrg table.

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_role_user",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")}
    )
    @OrderBy("roleId ASC")
    private Set<SysRoleSimple> roles; // Relation to SysRole table.

//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "sys_user_lookup",
//            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")},
//            inverseJoinColumns = {@JoinColumn(name = "DATA_GROUP_ID", referencedColumnName = "DATA_GROUP_ID")}
//    )
//    private Set<SysDataGroup> dataGroups;


}
