package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerCheckResult
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
@Table(name = "ser_check_result")
public class SerCheckResult extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHECK_RESULT_ID", length = 20)
    private Long checkResultId;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    private SysDevice sysDevice;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private SerTask serTask;

    @Column(name = "HAND_TASK_RESULT", length = 10)
    private String handTaskResult;

    @Column(name = "HAND_GOODS")
    private String handGoods;

    @Column(name = "HAND_GOODS_GRADE", length = 10)
    private String handGoodsGrade;

    @Column(name = "HAND_COLLECT_SIGN", length = 10)
    private String handCollectSign;

    @Column(name = "HAND_ATTACHED", length = 50)
    private String handAttached;

    @Column(name = "HAND_COLLECT_LABEL", length = 10)
    private String handCollectLabel;

    @Column(name = "HAND_APPRAISE", length = 10)
    private String handAppraise;

    @Column(name = "HAND_SUBMITRECTS", length = 10)
    private String handSubmitRects;

}
