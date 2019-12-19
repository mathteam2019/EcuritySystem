package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerJudgeGraphRepository extends
        JpaRepository<SerJudgeGraph, Long>,
        PagingAndSortingRepository<SerJudgeGraph, Long>,
        QuerydslPredicateExecutor<SerJudgeGraph> {

    @Query("SELECT YEAR(MAX(JUDGE_START_TIME)) FROM SerJudgeGraph ")
    Integer findMaxYear();

    @Query("SELECT YEAR(MIN(JUDGE_START_TIME)) FROM SerJudgeGraph ")
    Integer findMinYear();


}
