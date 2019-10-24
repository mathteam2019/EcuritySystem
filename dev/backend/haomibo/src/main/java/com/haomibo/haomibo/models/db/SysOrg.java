package com.haomibo.haomibo.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.haomibo.haomibo.jsonfilter.ModelJsonFilters;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_ORG)
@Table(name = "sys_org")
public class SysOrg implements Serializable {


    public static class Status {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
    }

    public List<SysOrg> generateChildrenList() {

        List<SysOrg> children = new ArrayList<>();

        Queue<SysOrg> queue = new LinkedList<>();

        queue.add(this);

        while (!queue.isEmpty()) {
            SysOrg head = queue.remove();
            if (head.children != null) {
                queue.addAll(head.children);
            }
            children.add(head.toBuilder().parent(null).children(null).build());
        }

        return children;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORG_ID", length = 20, nullable = false)
    Long orgId;

    @Column(name = "PARENT_ORG_ID", length = 20)
    Long parentOrgId;

    @Column(name = "ORG_NAME", length = 50)
    String orgName;

    @Column(name = "ORG_NUMBER", length = 50)
    String orgNumber;

    @Column(name = "LEADER", length = 50)
    String leader;

    @Column(name = "MOBILE", length = 20)
    String mobile;

    @Column(name = "STATUS", length = 10)
    String status;

    @Column(name = "CREATEDBY", length = 20)
    Long createdBy;

    @Column(name = "CREATEDTIME", nullable = false)
    Date createdTime;

    @Column(name = "EDITEDBY", length = 20)
    Long editedBy;

    @Column(name = "EDITEDTIME", nullable = false)
    Date editedTime;

    @Column(name = "NOTE", length = 500, nullable = false)
    String note;

    @ToString.Exclude
    @OneToOne()
    @JoinColumn(name = "PARENT_ORG_ID", referencedColumnName = "ORG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("parent")
    SysOrg parent;


    @ToString.Exclude
    @OneToMany(mappedBy = "parent")
    @NotFound(action = NotFoundAction.IGNORE)
    Set<SysOrg> children;


    @ToString.Exclude
    @OneToMany(mappedBy = "org")
    @NotFound(action = NotFoundAction.IGNORE)
    Set<SysUser> users;

}

