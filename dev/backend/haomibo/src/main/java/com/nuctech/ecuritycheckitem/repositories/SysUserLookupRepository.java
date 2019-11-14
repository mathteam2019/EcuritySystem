package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysUserLookup;
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