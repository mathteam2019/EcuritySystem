package com.nuctech.securitycheck.backgroundservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.constants.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.SpringContextHolder;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SysJudgeController
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@EnableAsync
@Api(tags = "SysJudgeController")
@RestController
@Slf4j
@RequestMapping(value = "api/sys-judge/")
public class SysJudgeController {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ISysDeviceService sysDeviceService;

    @Autowired
    private ISysDeviceConfigService sysDeviceConfigService;

    @Autowired
    private ISerScanParamsService serScanParamsService;

    @Autowired
    private ISerPlatformCheckParamsService serPlatformCheckParamsService;

    @Autowired
    private ISerJudgeGraphService serJudgeGraphService;

    @Autowired
    private ISysJudgeGroupService sysJudgeGroupService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 后台服务向远程短下发配置信息
     *
     * @param guid 设备guid
     */
    @Async
    @ApiOperation("4.3.2.10 后台服务向远程短下发配置信息")
    @PostMapping("send-dev-config")
    public void sendDeviceConfig(@ApiParam("设备guid") @RequestParam("guid") String guid) {
        String exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.rem");
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.rem.config");

        try {
            SysDevice sysDevice = SysDevice.builder().guid(guid).build();
            sysDevice = sysDeviceService.find(sysDevice);

            if (sysDevice == null) {
                SerDeviceConfigModel serDeviceConfigModel = new SerDeviceConfigModel();
                serDeviceConfigModel.setGuid(guid);
                ResultMessageVO resultMsg = new ResultMessageVO();
                resultMsg.setKey(routingKey);
                SerDeviceConfigModel model = new SerDeviceConfigModel();
                model.setGuid(guid);
                resultMsg.setContent(model);
                messageSender.sendDeviceConfigMessage(resultMsg, exchangeName, routingKey);
            } else {
                Long deviceId = sysDevice.getDeviceId();
                SysJudgeGroup sysJudgeGroup = sysJudgeGroupService.findLastJudgeConfig(deviceId);

                SysDeviceConfig sysDeviceConfig = new SysDeviceConfig();
                sysDeviceConfig = sysDeviceConfigService.findById(sysJudgeGroup.getConfigId());

                SerScanParam serScanParam = SerScanParam.builder().deviceId(deviceId).build();
                List<SerScanParam> serScanParamList = serScanParamsService.findAll(serScanParam);
                List<SerScanParamModel> serScanParamModelList = new ArrayList<SerScanParamModel>();
                for (SerScanParam temp : serScanParamList) {
                    SerScanParamModel serScanParamModel = SerScanParamModel.builder()
                            .AirCaliWarnTime(temp.getAirCaliWarnTime())
                            .StandbyTime(temp.getStandByTime())
                            .AlarmSound(temp.getAlarmSound())
                            .PassSound(temp.getPassSound())
                            .PoserrorSound(temp.getPosErrorSound())
                            .StandSound(temp.getStandSound())
                            .ScanSound(temp.getScanSound())
                            .ScanOverUseSound(temp.getScanOverUseSound())
                            .AutoRecognise(temp.getAutoRecognise())
                            .RecognitionRate(temp.getRecognitionRate())
                            .SaveScanData(temp.getSaveScanData())
                            .SaveSuspectData(temp.getSaveSuspectData())
                            .FacialBlurring(temp.getFacialBlurring())
                            .ChestBlurring(temp.getChestBlurring())
                            .HipBlurring(temp.getHipBlurring())
                            .GroinBlurring(temp.getGroinBlurring())
                            .build();
                    serScanParamModelList.add(serScanParamModel);
                }
                SerPlatformCheckParams serPlatformCheckParams = new SerPlatformCheckParams();
                serPlatformCheckParams = serPlatformCheckParamsService.getLastCheckParams();

                SerDeviceConfigModel serDeviceConfigModel = new SerDeviceConfigModel();
                serDeviceConfigModel.setGuid(guid);
                serDeviceConfigModel.setDeviceNumber(sysDevice.getDeviceSerial());
                serDeviceConfigModel.setMode(sysDeviceConfig.getSysWorkMode().getModeId());
                serDeviceConfigModel.setATRColor(serPlatformCheckParams.getScanRecogniseColour());
                serDeviceConfigModel.setManualColor(serPlatformCheckParams.getHandRecogniseColour());
                serDeviceConfigModel.setDeleteColor(serPlatformCheckParams.getDisplayDeleteSuspicionColour());
                serDeviceConfigModel.setParams(serScanParamModelList);
                ResultMessageVO resultMessageVO = new ResultMessageVO();
                resultMessageVO.setKey(routingKey);
                resultMessageVO.setContent(serDeviceConfigModel);
                messageSender.sendDeviceConfigMessage(resultMessageVO, exchangeName, routingKey);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            ResultMessageVO resultMsg = new ResultMessageVO();
            resultMsg.setKey(routingKey);
            SerDeviceConfigModel model = new SerDeviceConfigModel();
            model.setGuid(guid);
            resultMsg.setContent(model);
            messageSender.sendDeviceConfigMessage(resultMsg, exchangeName, routingKey);
        }
    }

    /**
     * 后台服务向判图站推送待判图图像信息
     *
     * @param taskNumber
     * @return
     */
    @Async
    @ApiOperation("4.3.2.11 后台服务向判图站推送待判图图像信息")
    @PostMapping("send-judge-image-info")
    public void sendJudgeImageInfo(@RequestBody @ApiParam("请求报文定义") String taskNumber) {
        log.debug("4.3.2.11 后台服务向判图站推送待判图图像信息  service started at" + System.currentTimeMillis()
                + "params:taskNumber" + taskNumber);
        SerJudgeImageInfoModel serJudgeImageInfoModel = serJudgeGraphService.sendJudgeImageInfo(taskNumber);
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.rem.image"));
        resultMessageVO.setContent(serJudgeImageInfoModel);
        messageSender.sendImageInfoToJudge(resultMessageVO);

        // 判断 是否超时
        if (serJudgeImageInfoModel.getGuid() != null) {         // 判图站分派超时-false
            boolean isProcessTimeout = false;
            SerPlatformCheckParams checkParamsTest = serPlatformCheckParamsService.getLastCheckParams();
            ObjectMapper objectMapper = new ObjectMapper();
            long start = System.currentTimeMillis();        // 判图计时
            SerManImageInfoModel serManImageInfo = new SerManImageInfoModel();
            while (serManImageInfo.getImageResult() == null) {                      // 是否收到判图结论
                String manImageInfoKey = "dev.service.image.info" + taskNumber;
                String serManImageInfoStr = redisUtil.get(manImageInfoKey);
                serManImageInfoStr = CryptUtil.decrypt(serManImageInfoStr);
                try {
                    serManImageInfo = objectMapper.readValue(serManImageInfoStr, SerManImageInfoModel.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (serManImageInfo.getImageResult() == null) {
                    try {
                        Thread.sleep(1000);                     // 等待？秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                long end = System.currentTimeMillis();
                if (checkParamsTest.getJudgeProcessingTime() != null && (end - start) >= checkParamsTest.getJudgeProcessingTime() * 1000) {             // 是否超时
                    isProcessTimeout = true;    // 处理超时
                    break;
                }
            }

            if (isProcessTimeout) {         // 提交超时结论
                JudgeSerResultModel judgeSerResultModel = new JudgeSerResultModel();
                judgeSerResultModel.setImageGuid(taskNumber);
                judgeSerResultModel.setResult(serManImageInfo.getAtrResult());
                judgeSerResultModel.setLoginName(BackgroundServiceUtil.getConfig("default.user"));
                judgeSerResultModel.setGuid(serJudgeImageInfoModel.getGuid());
                // 4.3.2.9 判图站向后台服务提交判图结论(提交超时结论)
                JudgeSysController judgeSysController = SpringContextHolder.getBean(JudgeSysController.class);
                judgeSysController.saveJudgeGraphResult(judgeSerResultModel);
            }
        }

        log.debug("4.3.2.11 后台服务向判图站推送待判图图像信息  service finished at" + System.currentTimeMillis()
                + "params:taskNumber" + taskNumber);
    }
    
}
