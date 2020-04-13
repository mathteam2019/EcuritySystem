package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysRoleResource;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysRoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SysRoleResourceRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SysRoleResourceRepository extends JpaRepository<SysRoleResource, Long> {
}
