package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDeviceRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * SerDeviceRegisterRepository
 *
 * @author LovelyXing
 * @version v1.0
 * @since 2019-11-28
 */
public interface SerDeviceRegisterRepository extends JpaRepository<SerDeviceRegister, Long> {
    @Query(value = "select * from ser_device_register e WHERE e.DEVICE_ID = ?1 ORDER BY e.CREATEDTIME desc LIMIT 1", nativeQuery = true)
    SerDeviceRegister findLatestRegisterInfo(Long deviceId);
}
