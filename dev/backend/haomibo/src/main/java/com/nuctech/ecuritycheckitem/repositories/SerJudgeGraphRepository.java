package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.TaskManagementController;
import com.nuctech.ecuritycheckitem.models.db.SerJudgeGraph;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SerJudgeGraphRepository extends
        JpaRepository<SerJudgeGraph, Integer>,
        PagingAndSortingRepository<SerJudgeGraph, Integer>,
        QuerydslPredicateExecutor<SerJudgeGraph> {



}
