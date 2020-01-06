package com.nuctech.securitycheck.backgroundservice.service;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysManualGroup;

import java.util.List;

/**
 * ISysManualGroupService
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-12-01
 */
public interface ISysManualGroupService {

    List<SysManualGroup> findAll(SysManualGroup sysManualGroup);

    SysManualGroup findLastManualConfig(Long deviceId);
}
