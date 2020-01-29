/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceStatusServiceImpl）
 * 文件名：	DeviceStatusServiceImpl.java
 * 描述：	DeviceStatusService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;

import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceStatusService;
import com.nuctech.ecuritycheckitem.service.devicemanagement.SerLoginInfoService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SerLoginInfoServiceImpl implements SerLoginInfoService {

    @Autowired
    SerLoginInfoRepository serLoginInfoRepository;

    @Override
    public SerLoginInfo findLatestLoginInfo(Long deviceId) {
        Optional<SerLoginInfo> serLoginInfoOptional = serLoginInfoRepository.findOne(QSerLoginInfo.serLoginInfo.deviceId.eq(deviceId));
        return serLoginInfoOptional.get();
    }
}
