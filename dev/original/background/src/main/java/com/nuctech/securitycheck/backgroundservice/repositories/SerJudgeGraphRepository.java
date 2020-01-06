package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerJudgeGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SerJudgeGraph Repository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
@Repository
public interface SerJudgeGraphRepository extends JpaRepository<SerJudgeGraph, Long> {
}
