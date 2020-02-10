package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerPlatformCheckParams;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerPlatformOtherParams;
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
public interface SerPlatformOtherParamsRepository extends JpaRepository<SerPlatformOtherParams, Long> {

    @Query(value = "select * from ser_platform_other_params order by ID DESC LIMIT 1", nativeQuery = true)
    SerPlatformOtherParams getLastOtherParams();
}
