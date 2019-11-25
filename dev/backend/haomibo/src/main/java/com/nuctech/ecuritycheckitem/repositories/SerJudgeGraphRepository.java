package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerJudgeGraphRepository extends
        JpaRepository<SerJudgeGraph, Integer>,
        PagingAndSortingRepository<SerJudgeGraph, Integer>,
        QuerydslPredicateExecutor<SerJudgeGraph> {

}