package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysUserGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserGroupUserRepository extends
        JpaRepository<SysUserGroupUser, Integer>,
        PagingAndSortingRepository<SysUserGroupUser, Integer>,
        QuerydslPredicateExecutor<SysUserGroupUser> {

}