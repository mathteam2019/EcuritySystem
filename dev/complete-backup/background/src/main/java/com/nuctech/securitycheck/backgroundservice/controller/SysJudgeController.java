package com.nuctech.securitycheck.backgroundservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.*;
import com.nuctech.securitycheck.backgroundservice.common.vo.CommonResultVO;
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
        //设置Rabbitmq的主题密钥和路由密钥
        String exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.rem");
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.rem.config");

        try {
            // 从数据库获取设备(get device data from database)
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

                SysDeviceConfig sysDeviceConfig = sysDeviceConfigService.findById(sysJudgeGroup.getConfigId());


                //获取扫描参数列表
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
            log.error("无法发送设备配置");
            log.error(e.getMessage());
            ResultMessageVO resultMsg = new ResultMessageVO();
            resultMsg.setKey(routingKey);
            SerDeviceConfigModel model = new SerDeviceConfigModel();
            model.setGuid(guid);
            resultMsg.setContent(model);
            messageSender.sendDeviceConfigMessage(resultMsg, exchangeName, routingKey);
        }
    }

    @ApiOperation("4.3.2.12 后台服务向安检仪推送工作超时提醒")
    @PostMapping("monitor-judge-overtime")
    public void monitorJudgeOvertime(DeviceOvertimeModel checkOvertimeModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.rem.overtime"));

        CommonResultVO result = new CommonResultVO();
        ObjectMapper objectMapper = new ObjectMapper();
        result.setGuid(checkOvertimeModel.getGuid());
        if(checkOvertimeModel.getRemind() == true) {
            result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
        } else {
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }
        try {
            resultMessageVO.setContent(result);
            messageSender.cronJobJudgeOvertime(CryptUtil.encrypt(objectMapper.writeValueAsString(result)));
        } catch (Exception e) {
            log.error("随着时间的推移未能监控安全性");
            log.error(e.getMessage());
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
        SerJudgeImageInfoModel serJudgeImageInfoModel = serJudgeGraphService.sendJudgeImageInfo(taskNumber);
        log.debug("4.3.2.11 后台服务向判图站推送待判图图像信息  service started at" + System.currentTimeMillis()
                + "params:taskNumber" + serJudgeImageInfoModel.getImageData().getImageGuid());
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.rem.image"));
        SendMessageModel sendMessageModel = SendMessageModel.builder()
                .guid(serJudgeImageInfoModel.getGuid())
                .imageGuid(serJudgeImageInfoModel.getImageData().getImageGuid())
                .result(CommonConstant.RESULT_SUCCESS.getValue())
                .build();
        resultMessageVO.setContent(sendMessageModel);
        messageSender.sendImageInfoToJudge(resultMessageVO);

        // 判断 是否超时
        if (serJudgeImageInfoModel.getGuid() != null) {         // 判图站分派超时-false
            boolean isProcessTimeout = false;

            ObjectMapper objectMapper = new ObjectMapper();
            long start = System.currentTimeMillis();        // 判图计时
            SerManImageInfoModel serManImageInfo = null;
            while (serManImageInfo == null) {                      // 是否收到判图结论
                String manImageInfoKey = "dev.service.image.result.info" + serJudgeImageInfoModel.getImageData().getImageGuid();
                String serManImageInfoStr = redisUtil.get(manImageInfoKey);
                serManImageInfoStr = CryptUtil.decrypt(serManImageInfoStr);

                String checkParamsStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.setting.platform.check"));

                SerPlatformCheckParams checkParamsTest = new SerPlatformCheckParams();
                checkParamsTest.setJudgeProcessingTime(Long.valueOf(CommonConstant.DEFAULT_JUDGE_PROCESSING_TIME.getValue()));
                checkParamsTest.setJudgeAssignTime(Long.valueOf(CommonConstant.DEFAULT_JUDGE_ASSIGN_TIME.getValue()));
                try {
                    checkParamsTest = objectMapper.readValue(checkParamsStr, SerPlatformCheckParams.class);
                    serManImageInfo = objectMapper.readValue(serManImageInfoStr, SerManImageInfoModel.class);
                } catch (Exception e) {
                    log.error("转换模型时发生错误");
                    log.error(e.getMessage());
                }
                if (serManImageInfo == null) {
                    try {
                        Thread.sleep(1000);                     // 等待？秒
                    } catch (InterruptedException e) {
                        log.error("无法使睡眠");
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
                log.error("判断进程超时");
                JudgeSerResultModel judgeSerResultModel = new JudgeSerResultModel();
                ImageResultModel imageResult = new ImageResultModel();
                imageResult.setImageGuid(serJudgeImageInfoModel.getImageData().getImageGuid());
                imageResult.setResult(serJudgeImageInfoModel.getImageData().getAtrResult());
                imageResult.setUserName(BackgroundServiceUtil.getConfig("default.user"));
                imageResult.setTime(DateUtil.getCurrentDate());
                judgeSerResultModel.setImageResult(imageResult);
                judgeSerResultModel.setGuid(serJudgeImageInfoModel.getGuid());
                // 4.3.2.9 判图站向后台服务提交判图结论(提交超时结论)
                JudgeSysController judgeSysController = SpringContextHolder.getBean(JudgeSysController.class);
                judgeSysController.saveJudgeGraphResult(judgeSerResultModel);
            }
        }

        log.debug("4.3.2.11 后台服务向判图站推送待判图图像信息  service finished at" + System.currentTimeMillis()
                + "params:taskNumber" + serJudgeImageInfoModel.getImageData().getImageGuid());
    }
    
}
