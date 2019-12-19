/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/25
 * @CreatedBy Sandy.
 * @FileName SysRoleResourceRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysRoleResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleResourceRepository extends
        JpaRepository<SysRoleResource, Long>,
        PagingAndSortingRepository<SysRoleResource, Long>,
        QuerydslPredicateExecutor<SysRoleResource> {

}