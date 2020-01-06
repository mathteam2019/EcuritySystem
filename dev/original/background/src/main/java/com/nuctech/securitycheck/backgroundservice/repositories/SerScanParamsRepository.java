package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerScanParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SerScanParamsRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
@Repository
public interface SerScanParamsRepository extends JpaRepository<SerScanParam, Long> {
}
