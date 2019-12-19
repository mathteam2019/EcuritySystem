/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/10
 * @CreatedBy Sandy.
 * @FileName SysUserGroupLookupRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysUserGroupLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserGroupLookupRepository extends
        JpaRepository<SysUserGroupLookup, Long>,
        PagingAndSortingRepository<SysUserGroupLookup, Long>,
        QuerydslPredicateExecutor<SysUserGroupLookup> {

}