package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysManualGroup;
import com.nuctech.securitycheck.backgroundservice.repositories.SysManualGroupRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISysManualGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SysManualGroupServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SysManualGroupServiceImpl implements ISysManualGroupService {

    @Autowired
    private SysManualGroupRepository sysManualGroupRepository;

    /**
     * 安检设备和手检站关系 查询
     *
     * @return List<SysJudgeGroup>
     */
    @Override
    public List<SysManualGroup> findAll(SysManualGroup sysManualGroup) {
        Example<SysManualGroup> manualEx = Example.of(sysManualGroup);
        return sysManualGroupRepository.findAll(manualEx);
    }

    @Override
    public SysManualGroup findLastManualConfig(Long deviceId) {
        return sysManualGroupRepository.findLastJudgeConfig(deviceId);
    }
}
