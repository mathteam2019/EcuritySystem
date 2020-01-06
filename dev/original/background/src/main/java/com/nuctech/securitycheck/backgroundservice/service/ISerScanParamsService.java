package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerScanParam;

import java.util.List;

/**
 * ISerScanParamsService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISerScanParamsService {

    List<SerScanParam> findAll(SerScanParam serScanParam);
    
}
