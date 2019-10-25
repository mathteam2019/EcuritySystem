package com.haomibo.haomibo.repositories;

import com.haomibo.haomibo.models.db.SysDataGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysDataGroupUserRepository extends
        JpaRepository<SysDataGroupUser, Integer>,
        PagingAndSortingRepository<SysDataGroupUser, Integer>,
        QuerydslPredicateExecutor<SysDataGroupUser> {

}