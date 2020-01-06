package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDeviceRegister;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SerDeviceRegisterRepository
 *
 * @author LovelyXing
 * @version v1.0
 * @since 2019-11-28
 */
public interface SerDeviceRegisterRepository extends JpaRepository<SerDeviceRegister, Long> {
}
