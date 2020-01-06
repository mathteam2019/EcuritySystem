package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SerDeviceStatus Repository
 *
 * @author benja
 * @version v1.0
 * @since 2019-11-28
 */
@Repository
public interface SerDeviceStatusRepository extends JpaRepository<SerDeviceStatus, Long> {
}