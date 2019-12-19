/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName SerArchiveIndicatorsRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerArchiveIndicators;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerArchiveIndicatorsRepository extends
        JpaRepository<SerArchiveIndicators, Long>,
        PagingAndSortingRepository<SerArchiveIndicators, Long>,
        QuerydslPredicateExecutor<SerArchiveIndicators> {

}