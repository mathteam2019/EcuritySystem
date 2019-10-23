package com.haomibo.haomibo.models.db;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
@Table(name = "sys_role")
public class SysRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID", length = 20, nullable = false)
    Long roleId;

    @Column(name = "ORG_ID", length = 20)
    Long orgId;

    @Column(name = "ROLE_NAME", length = 50)
    String roleName;

    @Column(name = "ROLE_FLAG", length = 10)
    String roleFlag;

    @Column(name = "STATUS", length = 10)
    String status;

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

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_role_resource",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID")}
    )
    Set<SysResource> resources;

}
