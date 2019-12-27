/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PlatformOtherServiceImpl）
 * 文件名：	PlatformOtherServiceImpl.java
 * 描述：	PlatformOtherService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.settingmanagement.impl;

import com.nuctech.ecuritycheckitem.models.db.SerPlatformOtherParams;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformOtherParamRepository;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformOtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformOtherServiceImpl implements PlatformOtherService {
    @Autowired
    SerPlatformOtherParamRepository serPlatformOtherParamRepository;


    /**
     * find all platform other params
     * @return
     */
    @Override
    public List<SerPlatformOtherParams> findAll() {
        return serPlatformOtherParamRepository.findAll();
    }

    /**
     * edit platform other param
     * @param serPlatformOtherParams
     */
    @Override
    public void modifyPlatform(SerPlatformOtherParams serPlatformOtherParams) {
        serPlatformOtherParamRepository.save(serPlatformOtherParams);
    }
}
