package com.haomibo.haomibo.models.db;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "sys_org")
@DynamicUpdate
public class SysOrg implements Serializable {


    public static class Status {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORG_ID", length = 20, nullable = false)
    private Long orgId;

    @Column(name = "PARENT_ORG_ID", length = 20)
    private Long parentOrgId;

    @Column(name = "ORG_NAME", length = 50)
    private String orgName;

    @Column(name = "ORG_NUMBER", length = 50)
    private String orgNumber;

    @Column(name = "LEADER", length = 50)
    private String leader;

    @Column(name = "MOBILE", length = 20)
    private String mobile;

    @Column(name = "STATUS", length = 10)
    private String status;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORG_ID", referencedColumnName = "ORG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysOrg parent;

}

