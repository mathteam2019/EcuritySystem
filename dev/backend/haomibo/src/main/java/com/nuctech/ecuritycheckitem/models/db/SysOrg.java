/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/19
 * @CreatedBy Sandy.
 * @FileName SysOrg.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;
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
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_ORG)
@Table(name = "sys_org")
public class SysOrg extends BaseEntity implements Serializable {


    public static class Status {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
    }

    /**
     * Generates children org list.
     *
     * @return List of Children org.
     */
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


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORG_ID", referencedColumnName = "ORG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("parent")
    SysOrg parent;


    @ToString.Exclude
    @OneToMany(mappedBy = "parent")
    Set<SysOrg> children;


    @ToString.Exclude
    @OneToMany(mappedBy = "org")
    @NotFound(action = NotFoundAction.IGNORE)
    Set<SysUser> users;


}
