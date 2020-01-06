package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysJudgeGroup;

import java.util.List;

/**
 * ISysJudgeGroupService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISysJudgeGroupService {

    List<SysJudgeGroup> findAll(SysJudgeGroup sysJudgeGroup);

    SysJudgeGroup findLastJudgeConfig(Long deviceId);
}
