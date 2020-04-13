package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerCheckResult;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerMqMessage;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * SerMqMessage Repository
 *
 * @author Choe
 * @version v1.0
 * @since 2020-01-09
 */
@Repository
public interface SerMqMessageRepository extends JpaRepository<SerMqMessage, Long> {
    @Modifying
    @Query(value="delete from ser_mq_message where CREATEDTIME < :limit", nativeQuery = true)
    int  removeMqMessageBeforeLimit(@Param("limit")Date limit);
}
