package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysUserGroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserGroupRoleRepository extends
        JpaRepository<SysUserGroupRole, Integer>,
        PagingAndSortingRepository<SysUserGroupRole, Integer>,
        QuerydslPredicateExecutor<SysUserGroupRole> {

}