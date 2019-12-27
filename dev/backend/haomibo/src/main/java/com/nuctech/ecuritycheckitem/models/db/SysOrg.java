/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysOrg）
 * 文件名：	SysOrg.java
 * 描述：	SysOrg Model
 * 作者名：	Sandy
 * 日期：	2019/10/19
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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;


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
        public static final String ACTIVE = "1000000701";
        public static final String INACTIVE = "1000000702";
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


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORG_ID", referencedColumnName = "ORG_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("parent")
    private SysOrg parent;


    @ToString.Exclude
    @OneToMany(mappedBy = "parent")
    private Set<SysOrg> children;


    @ToString.Exclude
    @OneToMany(mappedBy = "org")
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<SysUser> users;


}
