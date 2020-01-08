package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * History Repository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
}