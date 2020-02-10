package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * History Repository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query(value = "DELETE FROM History e WHERE e.EDITED_TIME < :date", nativeQuery = true)
    void removeAllHistory(@Param("date")Date date);
}
