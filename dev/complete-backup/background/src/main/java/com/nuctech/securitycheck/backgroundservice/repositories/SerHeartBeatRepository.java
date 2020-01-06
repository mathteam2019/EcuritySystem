package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerHeartBeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SerHeartBeat Repository
 *
 * @author CheGuangxing
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SerHeartBeatRepository extends JpaRepository<SerHeartBeat, Long> {
}
