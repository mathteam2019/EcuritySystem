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

package com.nuctech.ecuritycheckitem.models.redis;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_org")
public class SysOrgInfoVO extends BaseEntity implements Serializable {


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


}
