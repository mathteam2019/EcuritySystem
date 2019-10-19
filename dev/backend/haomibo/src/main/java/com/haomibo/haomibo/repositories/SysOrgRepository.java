package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.SysOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysOrgRepository extends
        JpaRepository<SysOrg, Integer>,
        PagingAndSortingRepository<SysOrg, Integer>,
        QuerydslPredicateExecutor<SysOrg> {

}