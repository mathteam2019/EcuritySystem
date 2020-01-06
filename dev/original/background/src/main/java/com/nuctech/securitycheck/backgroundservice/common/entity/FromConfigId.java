package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * FromConfigId
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "from_config_id")
public class FromConfigId extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FROM_CONFIG_ID", length = 20, nullable = false)
    private Long fromConfigId;

    @Column(name = "CONFIG_ID", length = 20)
    private Long configId;

    @Column(name = "FROM_DEVICE_ID", length = 20)
    private Long fromDeviceId;

    @Column(name = "DEVICE_ID", length = 20)
    private Long deviceId;

}
