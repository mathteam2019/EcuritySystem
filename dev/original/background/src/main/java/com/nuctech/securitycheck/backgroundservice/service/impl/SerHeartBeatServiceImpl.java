package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerHeartBeat;
import com.nuctech.securitycheck.backgroundservice.repositories.SerHeartBeatRepository;
import com.nuctech.securitycheck.backgroundservice.service.ISerHeartBeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SerHeartBeatServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
public class SerHeartBeatServiceImpl implements ISerHeartBeatService {

    @Autowired
    private SerHeartBeatRepository serHeartBeatRepository;

    /**
     * 心跳信息查询
     *
     * @param serHeartBeat 心跳信息
     * @return SerHeartBeat
     */
    @Override
    public SerHeartBeat find(SerHeartBeat serHeartBeat) {
        Example<SerHeartBeat> heartBeatExample = Example.of(serHeartBeat);
        return serHeartBeatRepository.findOne(heartBeatExample);
    }

    /**
     * 心跳信息保存
     *
     * @param serHeartBeat 心跳信息
     */
    @Override
    public void save(SerHeartBeat serHeartBeat) {
        serHeartBeatRepository.save(serHeartBeat);
    }
    
}
