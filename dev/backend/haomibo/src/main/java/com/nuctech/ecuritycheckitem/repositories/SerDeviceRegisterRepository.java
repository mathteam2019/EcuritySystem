/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/12/06
 * @CreatedBy Choe.
 * @FileName SerDeviceRegisterRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SerDeviceRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerDeviceRegisterRepository extends
        JpaRepository<SerDeviceRegister, Long>,
        PagingAndSortingRepository<SerDeviceRegister, Long>,
        QuerydslPredicateExecutor<SerDeviceRegister> {

}