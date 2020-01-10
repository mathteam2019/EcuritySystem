package com.nuctech.securitycheck.backgroundservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
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
import com.nuctech.securitycheck.backgroundservice.common.utils.DateUtil;
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
    private ISecurityImageInfoService securityImageInfoService;

    @Autowired
    private ISerHandResultService serHandResultService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    ISerMqMessageService serMqMessageService;

    /**
     * 将设备状态上传到Redis
     */
    public void uploadDeviceStatus() {
        List<SysMonitoringDeviceStatusInfoVO> monitoringList = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
        monitoringList = sysDeviceService.findMonitoringInfoList();
        String jsonStr = JSONObject.toJSONString(monitoringList);
        redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"), CryptUtil.encrypt(jsonStr));
        redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"), CommonConstant.EXPIRE_TIME.getValue());
    }

    /**
     * 将设备配置上传到Redis
     */
    public void uploadDeviceConfig() {
        List<SysSecurityInfoVO> sysSecurityInfoVOList = new ArrayList<SysSecurityInfoVO>();
        sysSecurityInfoVOList = sysDeviceService.findSecurityInfoList();
        String jsonStr = JSONObject.toJSONString(sysSecurityInfoVOList);
        redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CryptUtil.encrypt(jsonStr));
        redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CommonConstant.EXPIRE_TIME.getValue());
    }

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
        int checkResult = sysRegisterModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = new SysDevice();
                sysDevice.setGuid(sysRegisterModel.getGuid());
                sysDevice.setStatus(DefaultType.TRUE.getValue());
                sysDevice.setDeviceType(DeviceType.JUDGE.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                // 检查设备是否存在(check device exist or not)
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                } else {
                    //注册设备(register device)
                    boolean isSuccess = sysDeviceService.register(sysDevice, sysRegisterModel);
                    if (isSuccess) {

                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                        //将手检查点信息上传到Redis(upload security device information to redis)
                        uploadDeviceConfig();

                        // 4.3.2.10 后台服务向远程短下发配置信息
                        sysJudgeController.sendDeviceConfig(sysRegisterModel.getGuid());
                    } else {
                        result.setResult(CommonConstant.RESULT_FAIL.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("注册失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendRemRegisterMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysRegisterModel.getGuid(), null,
                result.getResult().toString());
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
        int checkResult = sysLoginModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = new SysDevice();
                sysDevice.setGuid(sysLoginModel.getGuid());
                sysDevice.setStatus(DefaultType.TRUE.getValue());
                sysDevice.setDeviceType(DeviceType.JUDGE.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                } else {
                    // 登录设备(login device)
                    boolean isSuccess = sysDeviceService.login(sysDevice, sysLoginModel);
                    if (isSuccess) {
                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                        //将设备状态和设备配置上传到Redis( upload device status and device config to redis)
                        uploadDeviceStatus();
                        uploadDeviceConfig();

                    } else {
                        result.setResult(CommonConstant.RESULT_FAIL.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("登录失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }



        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendRemLoginMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysLoginModel.getGuid(), null,
                result.getResult().toString());
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
        int checkResult = sysLogoutModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = new SysDevice();
                sysDevice.setGuid(sysLogoutModel.getGuid());
                sysDevice.setStatus(DefaultType.TRUE.getValue());
                sysDevice.setDeviceType(DeviceType.JUDGE.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                } else {
                    // 注销设备
                    boolean isSuccess = sysDeviceService.logout(sysDevice, sysLogoutModel);
                    if (isSuccess) {
                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                        //将设备状态和设备配置上传到Redis( upload device status and device config to redis)
                        uploadDeviceStatus();
                        uploadDeviceConfig();
                    } else {
                        result.setResult(CommonConstant.RESULT_FAIL.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("登出失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendRemLogoutMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysLogoutModel.getGuid(), null,
                result.getResult().toString());
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
        int checkResult = sysUnregisterModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = new SysDevice();
                sysDevice.setGuid(sysUnregisterModel.getGuid());
                sysDevice.setStatus(DefaultType.TRUE.getValue());
                sysDevice.setDeviceType(DeviceType.JUDGE.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                } else {
                    // 取消注册设备
                    boolean isSuccess = sysDeviceService.unRegister(sysDevice, sysUnregisterModel);
                    if (isSuccess) {
                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                        uploadDeviceConfig();
                    } else {
                        result.setResult(CommonConstant.RESULT_FAIL.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("取消注册失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }

        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendRemUnregisterMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysUnregisterModel.getGuid(), null,
                result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 判图站向后台服务发送日志信息
     *
     * @param serDevLogModel 设备向后台服务发送日志信息
     * @Param deviceType     设备类型
     */
    @ApiOperation("4.3.2.5 判图站向后台服务发送日志信息")
    @PostMapping("save-dev-log")
    public void saveSerDevLog(@ApiParam("设备向后台服务发送日志信息") @RequestBody SerDevLogModel serDevLogModel) {
        String exchangeName = null;
        String routingKey = null;
        CommonResultVO result = new CommonResultVO();
        result.setGuid(serDevLogModel.getGuid());
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        //设置Rabbitmq的主题密钥和路由密钥
        exchangeName = BackgroundServiceUtil.getConfig("topic.inter.dev.sys.data");
        routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.log");
        int checkResult = serDevLogModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
        } else {
            try {
                // 从数据库获取设备(get device data from database)
                SysDevice sysDevice = SysDevice.builder().guid(serDevLogModel.getGuid()).build();
                sysDevice = sysDeviceService.find(sysDevice);

                // 检查设备是否开启( check device is on)
                if (!sysDeviceService.checkDeviceLogin(sysDevice)) {
                    log.error("设备已关闭");
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());

                } else {

                    // 将设备日志添加到数据库
                    SerDevLog serDevLog = SerDevLog.builder()
                            .guid(serDevLogModel.getGuid())
                            .loginName(serDevLogModel.getLoginName())
                            .category(Integer.valueOf(serDevLogModel.getCategory()))
                            .level(Integer.valueOf(serDevLogModel.getLevel()))
                            .content(serDevLogModel.getContent())
                            .time(serDevLogModel.getTime())
                            .devType(sysDevice.getDeviceType())
                            .build();
                    serDevLogService.save(serDevLog);

                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                }
            } catch (Exception e) {
                log.error("无法保存设备日志");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(result);
        messageSender.sendSerDevLogReplyMessage(resultMessageVO, exchangeName, routingKey);
        serMqMessageService.save(resultMessageVO, 1, serDevLogModel.getGuid(), null,
                result.getResult().toString());
    }

    /**
     * 判图站向后台服务发送心跳信息
     *
     * @param heartBeatModel 心跳信息
     * @Param deviceType     设备类型
     */
    @ApiOperation("4.3.2.6 判图站向后台服务发送心跳信息")
    @PostMapping("save-heartbeat")
    public void saveHeartBeatTime(@ApiParam(value = "心跳信息") @RequestBody HeartBeatModel heartBeatModel) {
        String exchangeName = null;
        String routingKey = null;
        HeartBeatReplyModel heartBeatReplyModel = new HeartBeatReplyModel();
        ResultMessageVO resultMessageVO = new ResultMessageVO();

        //设置Rabbitmq的主题密钥和路由密钥
        exchangeName = BackgroundServiceUtil.getConfig("topic.inter.dev.sys.status");
        routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.rem.sys.heartbeat");

        int checkResult = heartBeatModel.checkValid();
        if(checkResult == 1) {
            heartBeatReplyModel.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            heartBeatReplyModel.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
        } else {
            try {
                // 从数据库获取设备(get device data from database)
                SysDevice sysDevice = SysDevice.builder().guid(heartBeatModel.getGuid()).build();
                sysDevice = sysDeviceService.find(sysDevice);

                // 检查设备是否开启( check device is on)
                if(sysDeviceService.checkDeviceLogin(sysDevice)) {
                    //从数据库获取心跳
                    SerHeartBeat serHeartBeat = SerHeartBeat.builder().deviceId(sysDevice.getDeviceId()).deviceType(sysDevice.getDeviceType()).build();
                    SerHeartBeat oldSerHeartBeat = serHeartBeatService.find(serHeartBeat);

                    //修改心跳时间

                    if (oldSerHeartBeat == null) {
                        //insert
                        serHeartBeat.setHeartBeatTime(DateUtil.stringDateToDate(heartBeatModel.getHeartbeatTime()));
                        serHeartBeatService.save(serHeartBeat);
                    } else {
                        //update
                        oldSerHeartBeat.setHeartBeatTime(DateUtil.stringDateToDate(heartBeatModel.getHeartbeatTime()));
                        serHeartBeatService.save(oldSerHeartBeat);
                    }
                    heartBeatReplyModel.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                } else {
                    log.error("设备已关闭");
                    heartBeatReplyModel.setResult(CommonConstant.RESULT_FAIL.getValue());
                }
            } catch (Exception e) {
                log.error("无法保存心跳");
                log.error(e.getMessage());
                heartBeatReplyModel.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }


        heartBeatReplyModel.setGuid(heartBeatModel.getGuid());
        heartBeatReplyModel.setHeartbeatTime(heartBeatModel.getHeartbeatTime());
        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(heartBeatModel);
        messageSender.sendHeartBeatReplyMessage(resultMessageVO, exchangeName, routingKey);
        serMqMessageService.save(resultMessageVO, 1, heartBeatModel.getGuid(), null,
                heartBeatReplyModel.getResult().toString());
    }

    /**
     * 判图站向后台服务发送开始工作通知
     *
     * @param sysDeviceStatusModel
     * @return ResultMessage
     */
    @ApiOperation("4.3.2.7 判图站向后台服务发送开始工作通知")
    @PostMapping("device-start")
    public ResultMessageVO deviceStart(@ApiParam("工作状态通知") @RequestBody SysDeviceStatusModel sysDeviceStatusModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.rem.sys.start");
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysDeviceStatusModel.getGuid());
        int checkResult = sysDeviceStatusModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else {
            try {
                // 启动设备
                if (judgeSysService.updateSysDeviceStatus(sysDeviceStatusModel, DeviceStatusType.START.getValue())) {
                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                    // redis
                    uploadDeviceConfig();
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                }

            } catch (Exception e) {
                log.error("操作失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        resultMessageVO.setKey(routingKey);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(result));
        messageSender.sendSysRemReplyMessage(encryptMsg, routingKey);
        serMqMessageService.save(resultMessageVO, 1, sysDeviceStatusModel.getGuid(), null,
                result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 判图站向后台服务发送停止工作通知
     *
     * @param sysDeviceStatusModel
     * @Param deviceStatus
     * @Param resultMessageVO
     * @return ResultMessage
     */
    @ApiOperation("4.3.2.8 判图站向后台服务发送停止工作通知")
    @PostMapping("device-stop")
    public ResultMessageVO deviceStop(@ApiParam("停止工作通知") @RequestBody SysDeviceStatusModel sysDeviceStatusModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.rem.sys.suspend");
        resultMessageVO.setKey(routingKey);
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysDeviceStatusModel.getGuid());
        int checkResult = sysDeviceStatusModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else {
            try {
                // 停止装置
                if (judgeSysService.updateSysDeviceStatus(sysDeviceStatusModel, DeviceStatusType.STOP.getValue())) {
                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                    uploadDeviceConfig();
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                }
                resultMessageVO.setContent(result);
            } catch (Exception e) {
                log.error("操作失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
                resultMessageVO.setContent(result);
            }
        }

        // 将结果发送到rabbitmq
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(result));
        messageSender.sendSysRemReplyMessage(encryptMsg, routingKey);
        serMqMessageService.save(resultMessageVO, 1, sysDeviceStatusModel.getGuid(), null,
                result.getResult().toString());
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
        SendMessageModel sendMessageModel = new SendMessageModel();
        ObjectMapper objectMapper = new ObjectMapper();

        // 4.3.2.9 判图站向后台服务提交判图结论

        sendMessageModel.setGuid(judgeSerResultModel.getGuid());
        sendMessageModel.setImageGuid(judgeSerResultModel.getImageResult().getImageGuid());
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.rem.sys.result");

        int checkResult = judgeSerResultModel.checkValid();
        if(checkResult == 1) {
            sendMessageModel.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            sendMessageModel.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
        } else {
            JudgeInfoSaveResultVO saveResult = serJudgeGraphService.saveJudgeGraphResult(judgeSerResultModel);
            if (saveResult.getIsSucceed()) {
                sendMessageModel.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                String dispatchManualDeviceInfoStr = "";
                while (StringUtils.isBlank(dispatchManualDeviceInfoStr)) {
                    dispatchManualDeviceInfoStr = redisUtil.get(BackgroundServiceUtil
                            .getConfig("redisKey.sys.manual.assign.task.info") + saveResult.getTaskNumber());
                    if (StringUtils.isBlank(dispatchManualDeviceInfoStr)) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                try {
                    DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO = objectMapper.readValue(dispatchManualDeviceInfoStr,
                            DispatchManualDeviceInfoVO.class);
                    if (dispatchManualDeviceInfoVO.getLocalRecheck()) {     // 分派手检端 安检仪
                        // // 4.3.1.15 后台服务向安检仪推送判图结论
                        SerDevJudgeGraphResultModel serDevJudgeGraphResultModel = new SerDevJudgeGraphResultModel();
                        serDevJudgeGraphResultModel.setImageResult(judgeSerResultModel.getImageResult());
                        serDevJudgeGraphResultModel.setGuid(saveResult.getGuid());
                        sysSecurityController.sendJudgeResultToSecurityDevice(serDevJudgeGraphResultModel);
                    } else {                                                // 分派手检端 手检端
                        // // 4.3.3.12 后台服务向手检站推送业务数据
                        SerManImageInfoModel serManImageInfoModel = serHandResultService.sendScanInfoToHand(judgeSerResultModel.getImageResult().getImageGuid());
                        sysManualController.sendJudgeResultToHandDevice(serManImageInfoModel);
                    }


                } catch (IOException e) {
                    log.error(e.getMessage());
                }

            } else {
                sendMessageModel.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }

        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(sendMessageModel);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendJudgeGraphResultReplyMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, judgeSerResultModel.getGuid(), judgeSerResultModel.getImageResult().getImageGuid(),
                String.valueOf(sendMessageModel.getResult()));
        //databaseRecord.save(key, message);
        return resultMessageVO;
    }

}
