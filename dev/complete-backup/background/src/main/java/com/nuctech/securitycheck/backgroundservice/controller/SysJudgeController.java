package com.nuctech.securitycheck.backgroundservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceDefaultType;
import com.nuctech.securitycheck.backgroundservice.common.enums.WorkModeType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.*;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    private ISerMqMessageService serMqMessageService;

    @Autowired
    private ISerHandResultService serHandResultService;

    /**
     * 将设备状态上传到Redis
     */
    @Async
    public void uploadDeviceStatus() {
        List<SysMonitoringDeviceStatusInfoVO> monitoringList = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
        monitoringList = sysDeviceService.findMonitoringInfoList();
        String jsonStr = CryptUtil.getJSONString(monitoringList);
        redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"), CryptUtil.encrypt(jsonStr));
        redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"), CommonConstant.EXPIRE_TIME.getValue());
    }

    /**
     * 将设备配置上传到Redis
     */
    @Async
    public void uploadDeviceConfig() {
        List<SysJudgeInfoVO> sysJudgeInfoVOList = sysDeviceService.findJudgeInfoList();
        List<SysManualInfoVO> sysManualInfoVOList = sysDeviceService.findManualInfoList();
        redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.judge.info"), CryptUtil.encrypt(CryptUtil.getJSONString(sysJudgeInfoVOList)), CommonConstant.EXPIRE_TIME.getValue());
        redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.manual.info"), CryptUtil.encrypt(CryptUtil.getJSONString(sysManualInfoVOList)), CommonConstant.EXPIRE_TIME.getValue());
    }




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
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.rem.config");

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
                serDeviceConfigModel.setATRColor(serPlatformCheckParams.getScanRecogniseColour());
                serDeviceConfigModel.setManualColor(serPlatformCheckParams.getJudgeRecogniseColour());
                serDeviceConfigModel.setDeleteColor(serPlatformCheckParams.getDisplayDeleteSuspicionColour());
                serDeviceConfigModel.setParams(serScanParamModelList);
                ResultMessageVO resultMessageVO = new ResultMessageVO();
                resultMessageVO.setKey(routingKey);
                resultMessageVO.setContent(serDeviceConfigModel);
                messageSender.sendDeviceConfigMessage(resultMessageVO, exchangeName, routingKey);
                serMqMessageService.save(resultMessageVO, 1, serDeviceConfigModel.getGuid(), null,
                        CommonConstant.RESULT_SUCCESS.getValue().toString());
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
            serMqMessageService.save(resultMsg, 1, model.getGuid(), null,
                    CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue().toString());
        }
    }

    @Async
    public void sendJudgeResultToDevice(JudgeSerResultModel judgeSerResultModel, JudgeInfoSaveResultVO saveResult) {
        ObjectMapper objectMapper = new ObjectMapper();
        String dispatchManualDeviceInfoStr = "";
        // 分派手检端 安检仪
        // // 4.3.1.15 后台服务向安检仪推送判图结论
        SerDevJudgeGraphResultModel serDevJudgeGraphResultModel = new SerDevJudgeGraphResultModel();
        serDevJudgeGraphResultModel.setImageResult(judgeSerResultModel.getImageResult());
        serDevJudgeGraphResultModel.setGuid(saveResult.getGuid());
        SysSecurityController sysSecurityController = SpringContextHolder.getBean(SysSecurityController.class);
        sysSecurityController.sendJudgeResultToSecurityDevice(serDevJudgeGraphResultModel);

        if(WorkModeType.SECURITY_JUDGE_MANUAL.getValue().equals(saveResult.getWorkModeName()) || WorkModeType.SECURITY_MANUAL.getValue().equals(saveResult.getWorkModeName())) {

            // 安检仪+手检端
//            if (DeviceInfoDefaultType.TRUE.getValue().equals(saveResult.getManualSwitch())) {
            //check assigned hand device or not
            sysSecurityController.checkHandDevice(saveResult.getTaskNumber());
//            }
            while (StringUtils.isBlank(dispatchManualDeviceInfoStr)) {
                dispatchManualDeviceInfoStr = redisUtil.get(BackgroundServiceUtil
                        .getConfig("redisKey.sys.manual.assign.task.info") + saveResult.getTaskNumber());
//                if (StringUtils.isBlank(dispatchManualDeviceInfoStr)) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                    }
//                }
            }

            // 分派手检端 手检端
            // // 4.3.3.12 后台服务向手检站推送业务数据
            SerManImageInfoModel serManImageInfoModel = serHandResultService.sendScanInfoToHand(judgeSerResultModel.getImageResult().getImageGuid());
            serManImageInfoModel.setImageResult(judgeSerResultModel.getImageResult());
            SysManualController sysManualController = SpringContextHolder.getBean(SysManualController.class);
            sysManualController.sendJudgeResultToHandDevice(serManImageInfoModel);

        }
    }


    /**
     * 后台服务向判图站推送待判图图像信息
     *
     * @param devSerImageInfoModel
     * @return
     */
    @Async
    @ApiOperation("4.3.2.11 后台服务向判图站推送待判图图像信息")
    @PostMapping("send-judge-image-info")
    public void sendJudgeImageInfo(@RequestBody @ApiParam("请求报文定义") DevSerImageInfoModel devSerImageInfoModel, String workModeName) {
        String taskNumber = devSerImageInfoModel.getImageData().getImageGuid();
        log.debug("4.3.2.11 后台服务向判图站推送待判图图像信息  service started at" + System.currentTimeMillis()
                + "params:taskNumber" + devSerImageInfoModel.getImageData().getImageGuid());
        SerJudgeImageInfoModel serJudgeImageInfoModel = serJudgeGraphService.sendJudgeImageInfo(taskNumber);

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.rem.image"));
        devSerImageInfoModel.setGuid(serJudgeImageInfoModel.getGuid());
        resultMessageVO.setContent(serJudgeImageInfoModel);
        if(serJudgeImageInfoModel.getGuid() != null) {
            messageSender.sendImageInfoToJudge(resultMessageVO);
            serMqMessageService.save(resultMessageVO, 1, devSerImageInfoModel.getGuid(), devSerImageInfoModel.getImageData().getImageGuid(),
                    CommonConstant.RESULT_SUCCESS.getValue().toString());
        }

        // 判断 是否超时
        if (serJudgeImageInfoModel.getGuid() != null) {         // 判图站分派超时-false
            boolean isProcessTimeout = false;

            ObjectMapper objectMapper = new ObjectMapper();
            long start = System.currentTimeMillis();        // 判图计时
            SerManImageInfoModel serManImageInfo = null;
            SerPlatformCheckParams checkParamsTest = serPlatformCheckParamsService.getLastCheckParams();
            while (serManImageInfo == null) {                      // 是否收到判图结论F
                String manImageInfoKey = "dev.service.image.result.info" + serJudgeImageInfoModel.getImageData().getImageGuid();
                String serManImageInfoStr = redisUtil.get(manImageInfoKey);
                serManImageInfoStr = CryptUtil.decrypt(serManImageInfoStr);

                String checkParamsStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.setting.platform.check"));

//                SerPlatformCheckParams checkParamsTest = new SerPlatformCheckParams();
//                checkParamsTest.setJudgeProcessingTime(Long.valueOf(CommonConstant.DEFAULT_JUDGE_PROCESSING_TIME.getValue()));
//                checkParamsTest.setJudgeAssignTime(Long.valueOf(CommonConstant.DEFAULT_JUDGE_ASSIGN_TIME.getValue()));
                try {
                    checkParamsTest = objectMapper.readValue(checkParamsStr, SerPlatformCheckParams.class);
                    serManImageInfo = objectMapper.readValue(serManImageInfoStr, SerManImageInfoModel.class);
                } catch (Exception e) {
                    log.error("转换模型时发生错误");
                    log.error(e.getMessage());
                }
//                if (serManImageInfo == null) {
//                    try {
//                        Thread.sleep(1000);                     // 等待？秒
//                    } catch (InterruptedException e) {
//                        log.error("无法使睡眠");
//                        e.printStackTrace();
//                    }
//                }
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
                imageResult.setTime(DateUtil.getDateTmeAsString(DateUtil.getCurrentDate()));
                imageResult.setTimeout(true);
                judgeSerResultModel.setImageResult(imageResult);
                judgeSerResultModel.setGuid(serJudgeImageInfoModel.getGuid());
                // 4.3.2.9 判图站向后台服务提交判图结论(提交超时结论)
                JudgeInfoSaveResultVO saveResult = serJudgeGraphService.saveJudgeGraphResult(judgeSerResultModel);

                sendJudgeResultToDevice(judgeSerResultModel, saveResult);

                //JudgeSysController judgeSysController = SpringContextHolder.getBean(JudgeSysController.class);
                //judgeSysController.saveJudgeGraphResult(judgeSerResultModel);
            }
        } else {

        }

        log.debug("4.3.2.11 后台服务向判图站推送待判图图像信息  service finished at" + System.currentTimeMillis()
                + "params:taskNumber" + serJudgeImageInfoModel.getImageData().getImageGuid());
    }
    
}
