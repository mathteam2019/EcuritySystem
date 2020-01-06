package com.nuctech.securitycheck.backgroundservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.constants.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerDevLog;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerHeartBeat;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysDevice;
import com.nuctech.securitycheck.backgroundservice.common.enums.DefaultType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceStatusType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.enums.WorkModeType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.repositories.SysUserRepository;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * JudegeSysController
 *
 * @author benja
 * @version v1.0
 * @since 2019-11-29
 */
@Api(tags = "JudgeSysController")
@RestController
@Slf4j
@RequestMapping(value = "api/judge-sys/")
@EnableAsync
public class JudgeSysController {

    @Autowired
    private IJudgeSysService judgeSysService;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ISerDevLogService serDevLogService;

    @Autowired
    private ISysDeviceService sysDeviceService;

    @Autowired
    private ISerHeartBeatService serHeartBeatService;

    @Autowired
    private ISerJudgeGraphService serJudgeGraphService;

    @Autowired
    private SysSecurityController sysSecurityController;

    @Autowired
    private SysManualController sysManualController;

    @Autowired
    private SysJudgeController sysJudgeController;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 判图站向后台服务发送注册信息
     *
     * @param sysRegisterModel 注册信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.2.1 判图站向后台服务发送注册信息")
    @PostMapping("send-register")
    public ResultMessageVO sendRegister(@ApiParam("注册信息") @RequestBody SysRegisterModel sysRegisterModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.register"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysRegisterModel.getGuid());
        List<SysSecurityInfoVO> sysSecurityInfoVOList = new ArrayList<SysSecurityInfoVO>();
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysRegisterModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.JUDGE.getValue());
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                result.setResult(CommonConstant.RESULT_FAIL);
            } else {
                boolean isSuccess = sysDeviceService.register(sysDevice, sysRegisterModel);
                if (isSuccess) {
                    sysSecurityInfoVOList = sysDeviceService.findSecurityInfoList();
                    result.setResult(CommonConstant.RESULT_SUCCESS);

                    String jsonStr = JSONObject.toJSONString(sysSecurityInfoVOList);
                    redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CryptUtil.encrypt(jsonStr));
                    redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CommonConstant.EXPIRE_TIME);

                    sysJudgeController.sendDeviceConfig(sysRegisterModel.getGuid());
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setResult(CommonConstant.RESULT_FAIL);
        }

        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendRemRegisterMessage(encryptMsg);

        return resultMessageVO;
    }

    /**
     * 判图站向后台服务发送登录信息
     *
     * @param sysLoginModel
     */
    @ApiOperation("4.3.2.2 判图站向后台服务发送登录信息")
    @PostMapping("send-login")
    public ResultMessageVO sendLogin(@ApiParam("登录信息") @RequestBody SysLoginModel sysLoginModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.login"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysLoginModel.getGuid());
        List<SysMonitoringDeviceStatusInfoVO> monitoringList = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
        List<SysSecurityInfoVO> sysSecurityInfoVOList = new ArrayList<SysSecurityInfoVO>();
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysLoginModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.JUDGE.getValue());
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                result.setResult(CommonConstant.RESULT_FAIL);
            } else {
                boolean isSuccess = sysDeviceService.login(sysDevice, sysLoginModel);
                if (isSuccess) {
                    sysSecurityInfoVOList = sysDeviceService.findSecurityInfoList();
                    monitoringList = sysDeviceService.findMonitoringInfoList();
                    result.setResult(CommonConstant.RESULT_SUCCESS);

                    String jsonStr = JSONObject.toJSONString(monitoringList);
                    redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"), CryptUtil.encrypt(jsonStr));
                    redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"), CommonConstant.EXPIRE_TIME);

                    jsonStr = JSONObject.toJSONString(sysSecurityInfoVOList);
                    redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CryptUtil.encrypt(jsonStr));
                    redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CommonConstant.EXPIRE_TIME);
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setResult(CommonConstant.RESULT_FAIL);
        }

        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendRemLoginMessage(encryptMsg);

        return resultMessageVO;
    }

    /**
     * 判图站向后台服务发送登出信息
     *
     * @param sysLogoutModel
     */
    @ApiOperation("4.3.2.3 判图站向后台服务发送登出信息")
    @PostMapping("send-logout")
    public ResultMessageVO sendLogout(@ApiParam("登出信息") @RequestBody SysLogoutModel sysLogoutModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.logout"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysLogoutModel.getGuid());
        List<SysMonitoringDeviceStatusInfoVO> monitoringList = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
        List<SysSecurityInfoVO> sysSecurityInfoVOList = new ArrayList<SysSecurityInfoVO>();
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysLogoutModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.JUDGE.getValue());
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                result.setResult(CommonConstant.RESULT_FAIL);
            } else {
                boolean isSuccess = sysDeviceService.logout(sysDevice, sysLogoutModel);
                if (isSuccess) {
                    sysSecurityInfoVOList = sysDeviceService.findSecurityInfoList();
                    monitoringList = sysDeviceService.findMonitoringInfoList();
                    result.setResult(CommonConstant.RESULT_SUCCESS);

                    String jsonStr = JSONObject.toJSONString(monitoringList);
                    redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"), CryptUtil.encrypt(jsonStr));
                    redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"), CommonConstant.EXPIRE_TIME);

                    jsonStr = JSONObject.toJSONString(sysSecurityInfoVOList);
                    redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CryptUtil.encrypt(jsonStr));
                    redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CommonConstant.EXPIRE_TIME);
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setResult(CommonConstant.RESULT_FAIL);
        }

        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendRemLogoutMessage(encryptMsg);

        return resultMessageVO;
    }

    /**
     * 判图站向后台服务发送注销信息
     *
     * @param sysUnregisterModel
     */
    @ApiOperation("4.3.2.4 判图站向后台服务发送注销信息")
    @PostMapping("send-unregister")
    public ResultMessageVO sendUnregister(@ApiParam("登录信息") @RequestBody SysUnregisterModel sysUnregisterModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.unregister"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysUnregisterModel.getGuid());
        List<SysSecurityInfoVO> sysSecurityInfoVOList = new ArrayList<SysSecurityInfoVO>();
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysUnregisterModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.JUDGE.getValue());
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                result.setResult(CommonConstant.RESULT_FAIL);
            } else {
                boolean isSuccess = sysDeviceService.unRegister(sysDevice, sysUnregisterModel);
                if (isSuccess) {
                    sysSecurityInfoVOList = sysDeviceService.findSecurityInfoList();
                    result.setResult(CommonConstant.RESULT_SUCCESS);

                    String jsonStr = JSONObject.toJSONString(sysSecurityInfoVOList);
                    redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CryptUtil.encrypt(jsonStr));
                    redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CommonConstant.EXPIRE_TIME);
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setResult(CommonConstant.RESULT_FAIL);
        }

        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendRemUnregisterMessage(encryptMsg);

        return resultMessageVO;
    }

    /**
     * 判图站向后台服务发送日志信息
     *
     * @param serDevLogModel 设备向后台服务发送日志信息
     * @param deviceType     设备类型
     */
    @ApiOperation("4.3.2.5 判图站向后台服务发送日志信息")
    @PostMapping("save-dev-log")
    public void saveSerDevLog(@ApiParam("设备向后台服务发送日志信息") @RequestBody SerDevLogModel serDevLogModel, @ApiParam("设备类型") @RequestParam DeviceType deviceType) {
        String exchangeName = null;
        String routingKey = null;
        CommonResultVO result = new CommonResultVO();
        result.setGuid(serDevLogModel.getGuid());
        ResultMessageVO resultMessageVO = new ResultMessageVO();

        if (DeviceType.SECURITY.equals(deviceType)) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.dev.sys");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.log");
        } else if (DeviceType.JUDGE.equals(deviceType)) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.rem.sys");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.log");
        } else if (DeviceType.MANUAL.equals(deviceType)) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.man.sys");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.log");
        }

        try {
            SysDevice sysDevice = SysDevice.builder().guid(serDevLogModel.getGuid()).build();
            sysDevice = sysDeviceService.find(sysDevice);

            if (sysDevice == null) {
                result.setResult(CommonConstant.RESULT_FAIL);
                resultMessageVO.setKey(routingKey);
                resultMessageVO.setContent(result);
                messageSender.sendSerDevLogReplyMessage(resultMessageVO, exchangeName, routingKey);
            } else {
                SerDevLog serDevLog = SerDevLog.builder()
                        .guid(serDevLogModel.getGuid())
                        .loginName(serDevLogModel.getLoginName())
                        .category(serDevLogModel.getCategory())
                        .level(serDevLogModel.getLevel())
                        .content(serDevLogModel.getContent())
                        .time(serDevLogModel.getTime())
                        .devType(sysDevice.getDeviceType())
                        .build();
                serDevLogService.save(serDevLog);

                result.setResult(CommonConstant.RESULT_SUCCESS);
                resultMessageVO.setKey(routingKey);
                resultMessageVO.setContent(result);
                messageSender.sendSerDevLogReplyMessage(resultMessageVO, exchangeName, routingKey);
            }
        } catch (Exception e) {
            log.error(e.toString());
            result.setResult(CommonConstant.RESULT_FAIL);
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(result);
            messageSender.sendSerDevLogReplyMessage(resultMessageVO, exchangeName, routingKey);
        }
    }

    /**
     * 判图站向后台服务发送心跳信息
     *
     * @param heartBeatModel 心跳信息
     * @param deviceType     设备类型
     */
    @ApiOperation("4.3.2.6 判图站向后台服务发送心跳信息")
    @PostMapping("save-heartbeat")
    public void saveHeartBeatTime(@ApiParam(value = "心跳信息") @RequestBody HeartBeatModel heartBeatModel, @RequestParam DeviceType deviceType) {
        String exchangeName = null;
        String routingKey = null;
        HeartBeatReplyModel heartBeatReplyModel = new HeartBeatReplyModel();
        ResultMessageVO resultMessageVO = new ResultMessageVO();

        if (DeviceType.SECURITY.equals(deviceType)) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.dev.sys");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.heartbeat");
        } else if (DeviceType.JUDGE.equals(deviceType)) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.rem.sys");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.heartbeat");
        } else if (DeviceType.MANUAL.equals(deviceType)) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.man.sys");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.heartbeat");
        }

        try {
            SysDevice sysDevice = SysDevice.builder().guid(heartBeatModel.getGuid()).build();
            sysDevice = sysDeviceService.find(sysDevice);

            SerHeartBeat serHeartBeat = SerHeartBeat.builder().deviceId(sysDevice.getDeviceId()).deviceType(sysDevice.getDeviceType()).build();
            SerHeartBeat oldSerHeartBeat = serHeartBeatService.find(serHeartBeat);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (oldSerHeartBeat == null) {
                //insert
                serHeartBeat.setHeartBeatTime(sdf.parse(heartBeatModel.getHeartbeatTime()));
                serHeartBeatService.save(serHeartBeat);
            } else {
                //update
                oldSerHeartBeat.setHeartBeatTime(sdf.parse(heartBeatModel.getHeartbeatTime()));
                serHeartBeatService.save(oldSerHeartBeat);
            }

            heartBeatReplyModel.setGuid(heartBeatModel.getGuid());
            heartBeatReplyModel.setHeartbeatTime(heartBeatModel.getHeartbeatTime());
            heartBeatReplyModel.setResult(CommonConstant.RESULT_SUCCESS);
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(heartBeatModel);

            messageSender.sendHeartBeatReplyMessage(resultMessageVO, exchangeName, routingKey);
        } catch (Exception e) {
            log.error(e.getMessage());
            heartBeatReplyModel.setGuid(heartBeatModel.getGuid());
            heartBeatReplyModel.setHeartbeatTime(heartBeatModel.getHeartbeatTime());
            heartBeatReplyModel.setResult(CommonConstant.RESULT_FAIL);
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(heartBeatModel);
            messageSender.sendHeartBeatReplyMessage(resultMessageVO, exchangeName, routingKey);
        }
    }

    /**
     * 判图站向后台服务发送开始工作通知
     *
     * @param sysDeviceStatusModel
     * @param deviceStatus
     * @param resultMessageVO
     * @return ResultMessage
     */
    @ApiOperation("4.3.2.7 判图站向后台服务发送开始工作通知")
    @PostMapping("device-start")
    public ResultMessageVO deviceStartAndStop(@ApiParam("工作状态通知") @RequestBody SysDeviceStatusModel sysDeviceStatusModel,
                                              String deviceStatus,
                                              @ApiParam("工作状态") @RequestBody ResultMessageVO resultMessageVO) {
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysDeviceStatusModel.getGuid());
        try {
            if (judgeSysService.updateSysDeviceStatus(sysDeviceStatusModel, deviceStatus)) {
                result.setResult(CommonConstant.RESULT_SUCCESS);

                // redis
                List<SysSecurityInfoVO> sysSecurityInfoVOList = sysDeviceService.findSecurityInfoList();
                String jsonStr = JSONObject.toJSONString(sysSecurityInfoVOList);

                redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CryptUtil.encrypt(jsonStr));
                redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CommonConstant.EXPIRE_TIME);
            } else {
                result.setResult(CommonConstant.RESULT_FAIL);
            }
            resultMessageVO.setContent(result);
        } catch (Exception e) {
            log.error("操作失败");
            result.setResult(CommonConstant.RESULT_FAIL);
            resultMessageVO.setContent(result);
        }

        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(result));
        if (deviceStatus.equals(DeviceStatusType.START.getValue())) {
            messageSender.sendSysRemReplyMessage(encryptMsg, BackgroundServiceUtil.getConfig("routingKey.reply.sys.start"));
        } else {
            messageSender.sendSysRemReplyMessage(encryptMsg, BackgroundServiceUtil.getConfig("routingKey.reply.sys.stop"));
        }

        return resultMessageVO;
    }

    /**
     * 判图站向后台服务发送停止工作通知
     *
     * @param sysDeviceStatusModel
     * @param deviceStatus
     * @param resultMessageVO
     * @return ResultMessage
     */
    @ApiOperation("4.3.2.8 判图站向后台服务发送停止工作通知")
    @PostMapping("device-stop")
    public ResultMessageVO deviceStop(@ApiParam("停止工作通知") @RequestBody SysDeviceStatusModel sysDeviceStatusModel,
                                      String deviceStatus, ResultMessageVO resultMessageVO) {
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysDeviceStatusModel.getGuid());
        try {
            if (judgeSysService.updateSysDeviceStatus(sysDeviceStatusModel, deviceStatus)) {
                result.setResult(CommonConstant.RESULT_SUCCESS);

                // redis
                List<SysSecurityInfoVO> sysSecurityInfoVOList = sysDeviceService.findSecurityInfoList();
                String jsonStr = JSONObject.toJSONString(sysSecurityInfoVOList);

                redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CryptUtil.encrypt(jsonStr));
                redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CommonConstant.EXPIRE_TIME);
            } else {
                result.setResult(CommonConstant.RESULT_FAIL);
            }
            resultMessageVO.setContent(result);
        } catch (Exception e) {
            log.error("操作失败");
            result.setResult(CommonConstant.RESULT_FAIL);
            resultMessageVO.setContent(result);
        }

        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(result));
        if (deviceStatus.equals(DeviceStatusType.START.getValue())) {
            messageSender.sendSysRemReplyMessage(encryptMsg, BackgroundServiceUtil.getConfig("routingKey.reply.sys.start"));
        } else {
            messageSender.sendSysRemReplyMessage(encryptMsg, BackgroundServiceUtil.getConfig("routingKey.reply.sys.stop"));
        }

        return resultMessageVO;
    }

    /**
     * 判图站向后台服务提交判图结论
     *
     * @param judgeSerResultModel
     * @return resultMessage
     */
    @ApiOperation("4.3.2.9 判图站向后台服务提交判图结论")
    @PostMapping("save-judge-graph-result")
    @Async
    public ResultMessageVO saveJudgeGraphResult(@RequestBody @ApiParam("请求报文定义") JudgeSerResultModel judgeSerResultModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        CommonResultVO result = new CommonResultVO();
        ObjectMapper objectMapper = new ObjectMapper();

        // 4.3.2.9 判图站向后台服务提交判图结论
        JudgeInfoSaveResultVO saveResult = serJudgeGraphService.saveJudgeGraphResult(judgeSerResultModel);
        result.setGuid(saveResult.getGuid());
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.result");
        if (saveResult.getIsSucceed()) {
            result.setResult(CommonConstant.RESULT_SUCCESS);

            if (saveResult.getWorkModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {       // 安检仪+判图站+手检端
                String dispatchManualDeviceInfoStr = "";
                int i = 0;
                // 等 分派手检端
                while (StringUtils.isBlank(dispatchManualDeviceInfoStr)) {
                    i++;
                    dispatchManualDeviceInfoStr = redisUtil.get(BackgroundServiceUtil
                            .getConfig("redisKey.sys.manual.assign.task.info") + saveResult.getTaskNumber());
                    if (StringUtils.isBlank(dispatchManualDeviceInfoStr)) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (i == 10) {
                        break;
                    }
                }
                try {
                    DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO = objectMapper.readValue(dispatchManualDeviceInfoStr,
                            DispatchManualDeviceInfoVO.class);
                    if (dispatchManualDeviceInfoVO.getLocalRecheck()) {     // 分派手检端 安检仪
                        // // 4.3.1.15 后台服务向安检仪推送判图结论
                        sysSecurityController.sendJudgeResultToSecurityDevice(saveResult.getTaskNumber(), dispatchManualDeviceInfoVO.getGuid());
                    } else {                                                // 分派手检端 手检端
                        // // 4.3.3.12 后台服务向手检站推送业务数据
                        sysManualController.sendJudgeResultToHandDevice(saveResult.getTaskNumber());
                    }


                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        } else {
            result.setResult(CommonConstant.RESULT_FAIL);
        }
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendJudgeGraphResultReplyMessage(encryptMsg);
        return resultMessageVO;
    }

}
