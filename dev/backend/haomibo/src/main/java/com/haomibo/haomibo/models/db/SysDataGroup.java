package com.haomibo.haomibo.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.haomibo.haomibo.jsonfilter.ModelJsonFilters;
import lombok.*;

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
@JsonFilter(ModelJsonFilters.FILTER_DATA_GROUP)
@Table(name = "sys_data_group")
public class SysDataGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_GROUP_ID", length = 20, nullable = false)
    Long dataGroupId;

    @Column(name = "ORG_ID", length = 20)
    Long orgId;

    @Column(name = "DATA_GROUP_NUMBER", length = 50)
    String dataGroupNumber;

    @Column(name = "DATA_GROUP_NAME", length = 50)
    String dataGroupName;

    @Column(name = "DATA_GROUP_FLAG", length = 10)
    String dataGroupFlag;

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
            name = "sys_data_group_user",
            joinColumns = {@JoinColumn(name = "DATA_GROUP_ID", referencedColumnName = "DATA_GROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")}
    )
    Set<SysUser> users;


}

