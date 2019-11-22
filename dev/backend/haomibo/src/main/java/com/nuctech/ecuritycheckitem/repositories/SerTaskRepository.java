package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerTaskRepository extends
        JpaRepository<SerTask, Integer>,
        PagingAndSortingRepository<SerTask, Integer>,
        QuerydslPredicateExecutor<SerTask> {

}