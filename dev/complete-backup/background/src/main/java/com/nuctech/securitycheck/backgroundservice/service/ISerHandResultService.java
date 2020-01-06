package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.models.HandSerResultModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SerManImageInfoModel;

/**
 * ISerHandResultService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISerHandResultService {

    Boolean saveHandResult(HandSerResultModel handSerResultModel);

    SerManImageInfoModel sendScanInfoToHand(String taskNumber);
    
}
