/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName SerArchiveTemplateRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerArchiveTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerArchiveTemplateRepository extends
        JpaRepository<SerArchiveTemplate, Long>,
        PagingAndSortingRepository<SerArchiveTemplate, Long>,
        QuerydslPredicateExecutor<SerArchiveTemplate> {

}