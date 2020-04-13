package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysUser;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysUserGroup;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysUserGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SysUserGroupUserRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SysUserGroupUserRepository extends JpaRepository<SysUserGroupUser, Long> {
    @Query(value = "select * from sys_user_group_user where USERGROUP_ID in ?1", nativeQuery = true)
    List<SysUserGroupUser> getUserIdList(@Param("userGroupIdList") List<Long> userGroupIdList);

    @Query(value = "select * from sys_user_group_user where USER_ID = ?1 and USERGROUP_ID in ?2", nativeQuery = true)
    List<SysUserGroupUser> getGroupUserListByUserId(@Param("userId")Long userId, @Param("userGroupIdList") List<Long> userGroupIdList);
}