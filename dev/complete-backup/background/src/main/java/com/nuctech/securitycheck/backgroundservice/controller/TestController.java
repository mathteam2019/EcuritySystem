package com.nuctech.securitycheck.backgroundservice.controller;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysDevice;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.enums.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;
import com.nuctech.securitycheck.backgroundservice.es.repository.SysDeviceEsRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * SysSecurityController
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-11-29
 */
@Api(tags = "TestController")
@RestController
@Slf4j
@RequestMapping(value = "api/test/")
public class TestController {

    @Autowired
    private RedisUtil redisUtil;

//    @Autowired
//    private SysDeviceEsRepository sysDeviceEsRepository;

    @ApiOperation("work-status-all-free")
    @PostMapping("work-status-all-free")
    public void dispatchManual() throws Exception {

        ObjectMapper objectRedisMapper = new ObjectMapper();
        String dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.security.info")));

        JSONArray dataContent = JSONArray.parseArray(dataStr);
        List<SysSecurityInfoVO> securityDeviceModelList = dataContent.toJavaList(SysSecurityInfoVO.class);
        for (SysSecurityInfoVO securityDeviceModel : securityDeviceModelList) {
//            SysDeviceConfigInfo conf = securityDeviceModel.getConfigInfo();
//            conf.setManualSwitch(DefaultType.TRUE.getValue());
//            securityDeviceModel.setConfigInfo(conf);
//            if(securityDeviceModel.getGuid().equals("guid0001")){
            List<SysManualInfoVO> manualList = securityDeviceModel.getManualDeviceModelList();
            for(SysManualInfoVO item : manualList){
                item.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
            }
            securityDeviceModel.setManualDeviceModelList(manualList);
            List<SysJudgeInfoVO> judgeList = securityDeviceModel.getJudgeDeviceModelList();
            for(SysJudgeInfoVO item : judgeList){
                item.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
            }
            securityDeviceModel.setJudgeDeviceModelList(judgeList);
//            }
        }
        redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"),
                CryptUtil.encrypt(objectRedisMapper.writeValueAsString(securityDeviceModelList)),
                CommonConstant.EXPIRE_TIME.getValue());
        return;
    }

    @ApiOperation("device-config-manual-status-change")
    @PostMapping("device-config-manual-status-change")
    public void dispatchManual(Long deviceId, String status) throws Exception {

        ObjectMapper objectRedisMapper = new ObjectMapper();
        String dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.security.info")));

        JSONArray dataContent = JSONArray.parseArray(dataStr);
        List<SysSecurityInfoVO> securityDeviceModelList = dataContent.toJavaList(SysSecurityInfoVO.class);
        for (SysSecurityInfoVO securityDeviceModel : securityDeviceModelList) {
            if(securityDeviceModel.getDeviceId() == deviceId){
                SysDeviceConfigInfoVO conf = securityDeviceModel.getConfigInfo();
                conf.setManualSwitch(status);
                securityDeviceModel.setConfigInfo(conf);
            }
        }
        redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"),
                CryptUtil.encrypt(objectRedisMapper.writeValueAsString(securityDeviceModelList)),
                CommonConstant.EXPIRE_TIME.getValue());
        return;
    }

    @ApiOperation("File delete")
    @PostMapping("device-file")
    public void testDelete() throws Exception {
        File fileToDelete = new File("src/test1.txt");
        boolean success = fileToDelete.delete();
        return;
    }

    @ApiOperation("Elastic Device")
    @PostMapping("device-list")
    public void testDevice() throws Exception {
//        List<SysDevice> deviceList = StreamSupport
//                .stream(sysDeviceEsRepository.findAll().spliterator(), false)
//                .collect(Collectors.toList());
//        int size = deviceList.size();
        return;
    }
}
