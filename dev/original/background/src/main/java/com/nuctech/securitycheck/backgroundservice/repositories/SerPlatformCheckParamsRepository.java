package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerPlatformCheckParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * SerPlatformCheckParamsRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
@Repository
public interface SerPlatformCheckParamsRepository extends JpaRepository<SerPlatformCheckParams, Long> {

    @Query(value = "select * from ser_platform_check_params order by SCAN_ID DESC LIMIT 1", nativeQuery = true)
    SerPlatformCheckParams getLastCheckParams();
}
