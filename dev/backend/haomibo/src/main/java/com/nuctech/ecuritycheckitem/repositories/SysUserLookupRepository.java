/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/01
 * @CreatedBy Sandy.
 * @FileName SysUserLookupRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysUserLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserLookupRepository extends
        JpaRepository<SysUserLookup, Long>,
        PagingAndSortingRepository<SysUserLookup, Long>,
        QuerydslPredicateExecutor<SysUserLookup> {

}