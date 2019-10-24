package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleRepository extends
        JpaRepository<SysRole, Integer>,
        PagingAndSortingRepository<SysRole, Integer>,
        QuerydslPredicateExecutor<SysRole> {

}