/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName SerDevLogRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerDevLogRepository extends
        JpaRepository<SerDevLog, Long>,
        PagingAndSortingRepository<SerDevLog, Long>,
        QuerydslPredicateExecutor<SerDevLog> {

}