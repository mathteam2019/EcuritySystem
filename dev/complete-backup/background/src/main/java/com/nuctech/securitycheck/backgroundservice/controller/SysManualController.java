package com.nuctech.securitycheck.backgroundservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
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
 * SysManualController
 *
 * @author benja
 * @version v1.0
 * @since 2019-11-29
 */
@EnableAsync
@Api(tags = "SysManualController")
@RestController
@Slf4j
@RequestMapping("api/sys-manual/")
public class SysManualController {

    @Autowired
    private ISerHandResultService serHandResultService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ISysDictionaryDataService sysDictionaryDataService;

    @Autowired
    private ISysDeviceService sysDeviceService;

    @Autowired
    private ISysManualGroupService sysManualGroupService;

    @Autowired
    private ISysDeviceConfigService sysDeviceConfigService;

    @Autowired
    private ISerScanParamsService serScanParamsService;

    @Autowired
    private ISerPlatformCheckParamsService serPlatformCheckParamsService;

    @Autowired
    private SysSecurityController sysSecurityController;

    /**
     * 后台服务向远程短下发配置信息
     *
     * @param guid 设备guid
     */
    @Async
    @ApiOperation("4.3.3.10 后台服务向手检站下发配置信息")
    @PostMapping("send-dev-config")
    public void sendDeviceConfig(@ApiParam("设备guid") @RequestParam("guid") String guid) {
        String exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.man");
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.man.config");

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
                SysManualGroup sysManualGroup = sysManualGroupService.findLastManualConfig(deviceId);

                SysDeviceConfig sysDeviceConfig = sysDeviceConfigService.findById(sysManualGroup.getConfigId());

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

    /**
     * 后台服务向手检站下发字典
     *
     * @param guid       设备guid
     *
     */
    @ApiOperation("4.3.3.11 后台服务向手检站下发字典")
    @PostMapping("send-dict-data")
    public void sendDictData(@ApiParam("设备guid") @RequestParam String guid) {
        sysSecurityController.sendDictionaryData(guid, DeviceType.MANUAL.getValue());
    }

    /**
     * 后台服务向手检站推送业务数据
     *
     * @param serManImageInfoModel
     */
    @Async
    @ApiOperation("4.3.3.12 后台服务向手检站推送业务数据")
    @PostMapping("send-judge-result-to-hand-device")
    public void sendJudgeResultToHandDevice(@RequestBody @ApiParam("请求报文定义") SerManImageInfoModel serManImageInfoModel) {
        log.debug("4.3.3.12 后台服务向手检站推送业务数据-------------start-----------timeLine------"
                + System.currentTimeMillis() + "param:taskNumber=" + serManImageInfoModel.getImageData().getImageGuid());
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.man.image"));
        SendMessageModel sendMessageModel = SendMessageModel.builder()
                .guid(serManImageInfoModel.getGuid())
                .imageGuid(serManImageInfoModel.getImageResult().getImageGuid())
                .result(CommonConstant.RESULT_SUCCESS.getValue())
                .build();
        resultMessageVO.setContent(sendMessageModel);
        messageSender.sendJudgeInfoToHandDevice(resultMessageVO);
        log.debug("4.3.3.12 后台服务向手检站推送业务数据-------------end-----------timeLine--------"
                + System.currentTimeMillis() + "param:taskNumber=" + serManImageInfoModel.getImageData().getImageGuid());
    }

    @ApiOperation("4.3.3.13 后台服务向手检端推送工作超时提醒")
    @PostMapping("monitor-manual-overtime")
    public void monitorManualOvertime(DeviceOvertimeModel checkOvertimeModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.man.overtime"));

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
            messageSender.cronJobHandOvertime(CryptUtil.encrypt(objectMapper.writeValueAsString(result)));
        } catch (Exception e) {
            log.error("随着时间的推移未能监控安全性");
            log.error(e.getMessage());
        }
    }

}
