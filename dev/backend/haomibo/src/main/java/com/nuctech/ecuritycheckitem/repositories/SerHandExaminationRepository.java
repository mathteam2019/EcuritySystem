package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerHandExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerHandExaminationRepository extends
        JpaRepository<SerHandExamination, Integer>,
        PagingAndSortingRepository<SerHandExamination, Integer>,
        QuerydslPredicateExecutor<SerHandExamination> {

}