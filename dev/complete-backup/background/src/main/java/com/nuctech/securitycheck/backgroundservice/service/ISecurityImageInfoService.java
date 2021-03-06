package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.models.DevSerDataSyncModel;
import com.nuctech.securitycheck.backgroundservice.common.models.DevSerImageInfoModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SendSimpleMessageModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SerDevJudgeGraphResultModel;
import com.nuctech.securitycheck.backgroundservice.common.vo.DispatchManualDeviceInfoVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.ScanInfoSaveResultVO;

/**
 * ISecurityImageInfoService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
public interface ISecurityImageInfoService {

    void sendInvalidResult(ScanInfoSaveResultVO scanInfoSaveResultVO, String atrResult, boolean isFinished);

    ScanInfoSaveResultVO saveScanResult(DevSerImageInfoModel devSerImageInfo);

    Boolean synchronizeScanResult(DevSerDataSyncModel devSerImageInfoModel);

    SerDevJudgeGraphResultModel sendJudgeResultToSecurity(String taskNumber);

    boolean checkAssignHand(SendSimpleMessageModel sendSimpleMessageModel);

}
