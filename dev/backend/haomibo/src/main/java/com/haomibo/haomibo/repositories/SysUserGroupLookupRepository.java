package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.SysUserGroupLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserGroupLookupRepository extends
        JpaRepository<SysUserGroupLookup, Integer>,
        PagingAndSortingRepository<SysUserGroupLookup, Integer>,
        QuerydslPredicateExecutor<SysUserGroupLookup> {

}