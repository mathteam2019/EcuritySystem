package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * SerScan Repository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SerScanRepository extends JpaRepository<SerScan, Long> {
    @Query(value = "SELECT * from ser_scan e where e.EDITED_TIME < :date", nativeQuery = true)
    List<SerScan> getScanListBeforeLimit(@Param("date") Date date);
}
