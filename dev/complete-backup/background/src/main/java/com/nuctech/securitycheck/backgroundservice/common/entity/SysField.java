package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysField
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "sys_field")
public class SysField extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FIELD_ID", length = 20, nullable = false)
    private Long fieldId;

    @Column(name = "ORG_ID", length = 20)
    private Long orgId;

    @Column(name = "PARENT_FIELD_ID", length = 20)
    private Long parentFieldId;

    @Column(name = "FIELD_SERIAL", length = 50)
    private String fieldSerial;

    @Column(name = "FIELD_DESIGNATION", length = 50)
    private String fieldDesignation;

    @Column(name = "LEADER", length = 50)
    private String leader;

    @Column(name = "MOBILE", length = 50)
    private String mobile;

    @Column(name = "FIELD_STATUS", length = 10)
    private String status;

}
