package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDeviceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * SysDeviceConfigRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SysDeviceConfigRepository extends JpaRepository<SysDeviceConfig, Long> {

    @Query(value = "select * from sys_device_config e WHERE e.DEVICE_ID = ?1 ORDER BY e.CONFIG_ID desc LIMIT 1", nativeQuery = true)
    SysDeviceConfig findLatestConfig(Long deviceId);

}
