package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerSeizedGood;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysDictionaryData;

import java.util.List;

/**
 * ISysDictionaryDataService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISysDictionaryDataService {

    List<SysDictionaryData> findAll(SysDictionaryData sysDictionaryData);
    List<SerSeizedGood> findAllSeized();
    
}
