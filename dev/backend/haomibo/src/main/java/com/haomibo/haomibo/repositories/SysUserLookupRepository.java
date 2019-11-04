package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.SysUserLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserLookupRepository extends
        JpaRepository<SysUserLookup, Integer>,
        PagingAndSortingRepository<SysUserLookup, Integer>,
        QuerydslPredicateExecutor<SysUserLookup> {

}