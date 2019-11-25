package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysWorkflowRepository extends
        JpaRepository<SysWorkflow, Integer>,
        PagingAndSortingRepository<SysWorkflow, Integer>,
        QuerydslPredicateExecutor<SysWorkflow> {

}