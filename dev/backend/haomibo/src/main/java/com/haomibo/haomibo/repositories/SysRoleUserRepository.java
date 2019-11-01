package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.SysRoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleUserRepository extends
        JpaRepository<SysRoleUser, Integer>,
        PagingAndSortingRepository<SysRoleUser, Integer>,
        QuerydslPredicateExecutor<SysRoleUser> {

}