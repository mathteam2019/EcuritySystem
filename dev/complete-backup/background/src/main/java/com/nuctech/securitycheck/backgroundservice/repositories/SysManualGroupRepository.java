package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysManualGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * SysManualGroupRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SysManualGroupRepository extends JpaRepository<SysManualGroup, Long> {

    @Query(value = "select * from sys_manual_group e WHERE e.MANUAL_DEVICE_ID = ?1 ORDER BY e.MANUAL_GROUP_ID desc LIMIT 1", nativeQuery = true)
    SysManualGroup findLastJudgeConfig(Long deviceId);
    
}
