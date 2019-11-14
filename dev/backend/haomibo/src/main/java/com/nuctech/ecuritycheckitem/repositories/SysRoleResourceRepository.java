package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysRoleResource;
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