package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.ForbiddenToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForbiddenTokenRepository extends
        JpaRepository<ForbiddenToken, Integer>,
        PagingAndSortingRepository<ForbiddenToken, Integer>,
        QuerydslPredicateExecutor<ForbiddenToken> {

}