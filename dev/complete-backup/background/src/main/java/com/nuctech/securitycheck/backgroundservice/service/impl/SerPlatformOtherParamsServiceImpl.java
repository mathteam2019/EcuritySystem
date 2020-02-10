package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerPlatformCheckParams;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerPlatformOtherParams;
import com.nuctech.securitycheck.backgroundservice.repositories.SerPlatformCheckParamsRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SerPlatformOtherParamsRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISerPlatformCheckParamsService;
import com.nuctech.securitycheck.backgroundservice.service.ISerPlatformOtherParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SerPlatformCheckParamsServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SerPlatformOtherParamsServiceImpl implements ISerPlatformOtherParamsService {
    
    @Autowired
    private SerPlatformOtherParamsRepository serPlatformOtherParamsRepository;

    /**
     * 系统参数-平台参数人员查验 取最后数据
     *
     * @return SerPlatformOtherParams
     */
    @Override
    public SerPlatformOtherParams getLastOtherParams() {
        return serPlatformOtherParamsRepository.getLastOtherParams();
    }

}
