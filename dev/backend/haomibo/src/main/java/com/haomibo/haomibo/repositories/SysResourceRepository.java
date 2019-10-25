package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.SysResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysResourceRepository extends
        JpaRepository<SysResource, Integer>,
        PagingAndSortingRepository<SysResource, Integer>,
        QuerydslPredicateExecutor<SysResource> {

}