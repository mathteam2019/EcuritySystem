package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerScanRepository extends
        JpaRepository<SerScan, Long>,
        PagingAndSortingRepository<SerScan, Long>,
        QuerydslPredicateExecutor<SerScan> {

    @Query("SELECT YEAR(MAX(SCAN_START_TIME)) FROM SerScan ")
    Integer findMaxYear();

    @Query("SELECT YEAR(MIN(SCAN_END_TIME)) FROM SerScan ")
    Integer findMinYear();
}