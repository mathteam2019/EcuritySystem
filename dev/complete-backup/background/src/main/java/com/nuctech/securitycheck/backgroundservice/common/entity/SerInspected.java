package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerInspected
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
@Table(name = "ser_inspected")
public class SerInspected extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INSPECTED_ID", length = 20)
    private Long inspectedId;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private SerTask serTask;

    @Column(name = "SUSPECT_NAME", length = 50)
    private String suspectName;

    @Column(name = "IDENTITY_CARD", length = 50)
    private String identityCard;

    @Column(name = "IDENTITY_CATEGORY", length = 10)
    private String identityCategory;

    @Column(name = "GENDER", length = 10)
    private String gender;

    @Column(name = "ADDRESS", length = 200)
    private String address;

    @Column(name = "FACE", length = 200)
    private String face;

    @Column(name = "CHECK_LEVE", length = 10)
    private String checkLevel;

    @Column(name = "ACTIVATE_STATUS", length = 10)
    private String activateStatus;

    @Column(name = "CHECK_RESULT", length = 10)
    private Boolean checkResult;

    @Column(name = "CHECK_DESC", length = 500)
    private String checkDesc;

}
