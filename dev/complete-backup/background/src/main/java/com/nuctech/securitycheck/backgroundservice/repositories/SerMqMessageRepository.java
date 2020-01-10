package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerCheckResult;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerMqMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SerMqMessage Repository
 *
 * @author Choe
 * @version v1.0
 * @since 2020-01-09
 */
@Repository
public interface SerMqMessageRepository extends JpaRepository<SerMqMessage, Long> {
}
