package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysDataGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysDataGroupRepository extends
        JpaRepository<SysDataGroup, Integer>,
        PagingAndSortingRepository<SysDataGroup, Integer>,
        QuerydslPredicateExecutor<SysDataGroup> {

}