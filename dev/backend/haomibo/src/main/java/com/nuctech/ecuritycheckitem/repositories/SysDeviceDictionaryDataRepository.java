/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/18
 * @CreatedBy Choe.
 * @FileName SysDeviceCategoryRepository.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceDictionaryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysDeviceDictionaryDataRepository extends
        JpaRepository<SysDeviceDictionaryData, Integer>,
        PagingAndSortingRepository<SysDeviceDictionaryData, Integer>,
        QuerydslPredicateExecutor<SysDeviceDictionaryData> {

}