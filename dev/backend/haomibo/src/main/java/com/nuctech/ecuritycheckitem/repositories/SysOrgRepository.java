package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysOrg;
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