package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysJudgeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * SysJudgeGroupRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SysJudgeGroupRepository extends JpaRepository<SysJudgeGroup, Long> {

    @Query(value = "select * from sys_judge_group e WHERE e.JUDGE_DEVICE_ID = ?1 ORDER BY e.JUDGE_GROUP_ID desc LIMIT 1", nativeQuery = true)
    SysJudgeGroup findLastJudgeConfig(Long deviceId);
    
}
