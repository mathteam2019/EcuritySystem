package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRepository extends
        JpaRepository<SysUser, Integer>,
        PagingAndSortingRepository<SysUser, Integer>,
        QuerydslPredicateExecutor<SysUser> {

}