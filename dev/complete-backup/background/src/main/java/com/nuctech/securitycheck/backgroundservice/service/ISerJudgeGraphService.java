package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.models.JudgeSerResultModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SerJudgeImageInfoModel;
import com.nuctech.securitycheck.backgroundservice.common.vo.JudgeInfoSaveResultVO;

/**
 * ISerJudgeGraphService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISerJudgeGraphService {

    JudgeInfoSaveResultVO saveJudgeGraphResult(JudgeSerResultModel judgeSerResultModel);

    SerJudgeImageInfoModel sendJudgeImageInfo(String taskNumber);

}
