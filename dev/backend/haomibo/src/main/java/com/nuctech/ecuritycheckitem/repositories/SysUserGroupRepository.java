package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserGroupRepository extends
        JpaRepository<SysUserGroup, Integer>,
        PagingAndSortingRepository<SysUserGroup, Integer>,
        QuerydslPredicateExecutor<SysUserGroup> {

}