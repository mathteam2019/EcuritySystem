package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerSeizedGood;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysDictionaryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SerSeizedGoodRepository
 *
 * @author Choe
 * @version v1.0
 * @since 2020-01-12
 */
@Repository
public interface SerSeizedGoodRepository extends JpaRepository<SerSeizedGood, Long> {
}
