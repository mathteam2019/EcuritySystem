package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysRoleUser;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysUserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SysRoleUserRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SysUserGroupRepository extends JpaRepository<SysUserGroup, Long> {

    @Query(value = "select * from sys_role_user where USER_ID = ?1 and ROLE_ID in ?2", nativeQuery = true)
    List<SysRoleUser> getGroupUserListByUserId(@Param("userId") Long userId, @Param("groupIdList") List<Long> groupIdList);
}
