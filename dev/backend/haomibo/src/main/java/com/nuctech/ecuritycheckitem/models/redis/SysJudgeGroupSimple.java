package com.nuctech.ecuritycheckitem.models.redis;

import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysJudgeGroup
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
@Table(name = "sys_judge_group")
public class SysJudgeGroupSimple extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JUDGE_GROUP_ID", length = 20, nullable = false)
    private Long judgeGroupId;

    @Column(name = "JUDGE_DEVICE_ID", length = 20)
    private Long judgeDeviceId;

    @Column(name = "CONFIG_ID", length = 20)
    private Long configId;

}
