package com.haomibo.haomibo.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.haomibo.haomibo.jsonfilter.ModelJsonFilters;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_USER)
@Table(name = "sys_user")
public class SysUser implements Serializable {


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
    Long userId;

    @Column(name = "ORG_ID", length = 20)
    Long orgId;

    @Column(name = "USER_NAME", length = 50)
    String userName;

    @Column(name = "USER_ACCOUNT", length = 20)
    String userAccount;

    @JsonIgnore
    @Column(name = "PASSWORD", length = 20)
    String password;

    @Column(name = "DATA_RANGE_CATEGORY", length = 10)
    String dataRangeCategory;

    @Column(name = "USER_NUMBER", length = 50)
    String userNumber;

    @Column(name = "GENDER", length = 10)
    String gender;

    @Column(name = "IDENTITY_CARD", length = 50)
    String identityCard;

    @Column(name = "POST", length = 50)
    String post;

    @Column(name = "EDUCATION", length = 10)
    String education;

    @Column(name = "DEGREE", length = 10)
    String degree;

    @Column(name = "EMAIL", length = 50)
    String email;

    @Column(name = "MOBILE", length = 20)
    String mobile;

    @Column(name = "ADDRESS", length = 50)
    String address;

    @Column(name = "CATEGORY", length = 10)
    String category;

    @Column(name = "STATUS", length = 10)
    String status;

    @Column(name = "PORTRAIT", length = 200)
    String portrait;

    @Column(name = "TASK_ID", length = 20)
    Long taskId;

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
    Set<SysRole> roles; // Relation to SysRole table.
}
