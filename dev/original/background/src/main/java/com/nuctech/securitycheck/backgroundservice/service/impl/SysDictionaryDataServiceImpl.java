package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDictionaryData;
import com.nuctech.securitycheck.backgroundservice.repositories.SysDictionaryDataRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISysDictionaryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SysDictionaryDataServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SysDictionaryDataServiceImpl implements ISysDictionaryDataService {

    @Autowired
    private SysDictionaryDataRepository sysDictionaryDataRepository;

    /**
     * 数据值 查询
     *
     * @return List<SysDictionaryData>
     */
    @Override
    public List<SysDictionaryData> findAll(SysDictionaryData sysDictionaryData) {
        Example<SysDictionaryData> ex = Example.of(sysDictionaryData);
        return sysDictionaryDataRepository.findAll(ex);
    }

}
