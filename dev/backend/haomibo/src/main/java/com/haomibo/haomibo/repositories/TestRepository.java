package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends
        JpaRepository<Test, Integer>,
        PagingAndSortingRepository<Test, Integer>,
        QuerydslPredicateExecutor<Test> {

}