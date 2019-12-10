/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/14
 * @CreatedBy Sandy.
 * @FileName SysUser.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
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
        public static final String MALE = "male";
        public static final String FEMALE = "female";
        public static final String OTHER = "other";
    }

    public static class Status {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
        public static final String PENDING = "pending";
        public static final String BLOCKED = "blocked";
    }

    public static class Category {
        public static final String ADMIN = "admin";
        public static final String NORMAL = "normal";
    }

    @AllArgsConstructor
    @Getter
    public enum DataRangeCategory {
        PERSON("person"),
        ORG("org"),
        ORG_DESC("org_desc"),
        ALL("all"),
        SPECIFIED("specified");

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

    @Column(name = "TASK_ID", length = 20)
    private Long taskId;

    @ToString.Exclude
    @OneToOne()
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
    private Set<SysRole> roles; // Relation to SysRole table.

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_user_lookup",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "DATA_GROUP_ID", referencedColumnName = "DATA_GROUP_ID")}
    )
    private Set<SysDataGroup> dataGroups;

}
