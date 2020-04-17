package com.nuctech.ecuritycheckitem.models.redis;

import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysWorkMode
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
@Table(name = "sys_work_mode")
public class SysWorkModeSimple extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MODE_ID", length = 20, nullable = false)
    private Long modeId;

    @Column(name = "MODE_NAME", length = 20)
    private String modeName;

}
