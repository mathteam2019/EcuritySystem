package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * SerLoginInfoRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
@Repository
public interface SerLoginInfoRepository extends JpaRepository<SerLoginInfo, Long> {

    @Query(value = "select * from ser_login_info e WHERE e.DEVICE_ID = ?1 ORDER BY e.CREATEDTIME desc LIMIT 1", nativeQuery = true)
    SerLoginInfo findLatestLoginInfo(Long deviceId);

}
