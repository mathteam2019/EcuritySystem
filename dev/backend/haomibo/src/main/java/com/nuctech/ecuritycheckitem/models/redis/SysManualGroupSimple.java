package com.nuctech.ecuritycheckitem.models.redis;

import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SysManualGroup
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
@Table(name = "sys_manual_group")
public class SysManualGroupSimple extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MANUAL_GROUP_ID", length = 20, nullable = false)
    private Long manualGroupId;

    @Column(name = "MANUAL_DEVICE_ID", length = 20)
    private Long manualDeviceId;

    @Column(name = "CONFIG_ID", length = 20)
    private Long configId;

//    @ManyToOne()
//    @JoinColumn(name = "MANUAL_DEVICE_ID", referencedColumnName = "MANUAL_DEVICE_ID", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    @MapsId("device")
//    private SysManualDevice manualDevice;

}