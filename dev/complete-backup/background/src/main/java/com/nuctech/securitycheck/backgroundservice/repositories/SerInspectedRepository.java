package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerInspected;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SerInspected Repository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-30
 */
@Repository
public interface SerInspectedRepository extends JpaRepository<SerInspected, Long> {
}
