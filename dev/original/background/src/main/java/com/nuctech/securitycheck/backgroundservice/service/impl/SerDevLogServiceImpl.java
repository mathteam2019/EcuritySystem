package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDevLog;
import com.nuctech.securitycheck.backgroundservice.repositories.SerDevLogRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISerDevLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SerDevLogServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SerDevLogServiceImpl implements ISerDevLogService {

    @Autowired
    private SerDevLogRepository serDevLogRepository;

    /**
     * 更新 或者 保存
     *
     * @param serDevLog
     * @return SerDevLog
     */
    @Override
    public SerDevLog save(SerDevLog serDevLog) {
        return serDevLogRepository.save(serDevLog);
    }

}
