package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysRole;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SysRoleRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
}