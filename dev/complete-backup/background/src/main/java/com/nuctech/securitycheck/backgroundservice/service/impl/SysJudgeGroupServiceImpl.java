package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysJudgeGroup;
import com.nuctech.securitycheck.backgroundservice.repositories.SysJudgeGroupRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISysJudgeGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SysJudgeGroupServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SysJudgeGroupServiceImpl implements ISysJudgeGroupService {

    @Autowired
    private SysJudgeGroupRepository sysJudgeGroupRepository;

    /**
     * 安检设备和判图站关系 查询
     *
     * @return List<SysJudgeGroup>
     */
    @Override
    public List<SysJudgeGroup> findAll(SysJudgeGroup sysJudgeGroup) {
        Example<SysJudgeGroup> judgeEx = Example.of(sysJudgeGroup);
        return sysJudgeGroupRepository.findAll(judgeEx);
    }

    /**
     * 安检设备和判图站关系 查询
     *
     * @return SysJudgeGroup
     */
    @Override
    public SysJudgeGroup findLastJudgeConfig(Long deviceId) {
        return sysJudgeGroupRepository.findLastJudgeConfig(deviceId);
    }
}
