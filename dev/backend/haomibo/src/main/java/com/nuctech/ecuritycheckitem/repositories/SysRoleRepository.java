/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/24
 * @CreatedBy Sandy.
 * @FileName SysRoleRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleRepository extends
        JpaRepository<SysRole, Long>,
        PagingAndSortingRepository<SysRole, Long>,
        QuerydslPredicateExecutor<SysRole> {

}