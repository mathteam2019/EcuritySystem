package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerScanParam;
import com.nuctech.securitycheck.backgroundservice.repositories.SerScanParamsRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISerScanParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SerScanParamsServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SerScanParamsServiceImpl implements ISerScanParamsService {

    @Autowired
    private SerScanParamsRepository serScanParamsRepository;

    /**
     * 系统参数-安检仪 查询
     *
     * @param serScanParam
     * @return List<SerScanParam>
     */
    @Override
    public List<SerScanParam> findAll(SerScanParam serScanParam) {
        Example<SerScanParam> exSerScanParam = Example.of(serScanParam);
        return serScanParamsRepository.findAll(exSerScanParam);
    }
    
}
