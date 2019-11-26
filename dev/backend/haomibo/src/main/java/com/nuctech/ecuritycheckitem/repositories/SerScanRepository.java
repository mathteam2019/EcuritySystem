package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerScanRepository extends
        JpaRepository<SerScan, Integer>,
        PagingAndSortingRepository<SerScan, Integer>,
        QuerydslPredicateExecutor<SerScan> {


}