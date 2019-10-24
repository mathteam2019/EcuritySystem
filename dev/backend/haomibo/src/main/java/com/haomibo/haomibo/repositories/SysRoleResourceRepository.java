package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.SysRoleResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleResourceRepository extends
        JpaRepository<SysRoleResource, Integer>,
        PagingAndSortingRepository<SysRoleResource, Integer>,
        QuerydslPredicateExecutor<SysRoleResource> {

}