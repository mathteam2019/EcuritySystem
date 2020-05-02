package com.nuctech.ecuritycheckitem.models.redis;

import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysField
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_field")
public class SysFieldSimple extends BaseEntity implements Serializable {

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
