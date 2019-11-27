/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName SerKnowledgeCaseDealRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerKnowledgeCaseDealRepository extends
        JpaRepository<SerKnowledgeCaseDeal, Integer>,
        PagingAndSortingRepository<SerKnowledgeCaseDeal, Integer>,
        QuerydslPredicateExecutor<SerKnowledgeCaseDeal> {

}