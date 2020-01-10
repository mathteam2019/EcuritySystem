package com.nuctech.securitycheck.backgroundservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerDevLog;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerHeartBeat;
import com.nuctech.securitycheck.backgroundservice.common.entity.SysDevice;
import com.nuctech.securitycheck.backgroundservice.common.enums.DefaultType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceStatusType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.CommonResultVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.SysSecurityInfoVO;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * ManualSysController
 *
 * @author LovelyXing
 * @version v1.0
 * @since 2019-11-29
 */
@Api(tags = "ManualSysController")
@RestController
@Slf4j
@RequestMapping(value = "api/manual-sys/")
public class ManualSysController {

    @Autowired
    private ISerHandResultService serHandResultService;

    @Autowired
    private ISysDeviceService sysDeviceService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ISerHeartBeatService serHeartBeatService;

    @Autowired
    private ISerDevLogService serDevLogService;

    @Autowired
    private IJudgeSysService judgeSysService;

    @Autowired
    private SysSecurityController sysSecurityController;

    @Autowired
    private SysManualController sysManualController;

    @Autowired
    private JudgeSysController judgeSysController;

    @Autowired
    private SecuritySysController securitySysController;

    @Autowired
    private ISerMqMessageService serMqMessageService;




    /**
     * 手检站向后台服务发送注册信息
     *
     * @param sysRegisterModel 注册信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.3.1 手检站向后台服务发送注册信息")
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
                sysDevice.setDeviceType(DeviceType.MANUAL.getValue());

                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
                } else {
                    //注册设备(register device)
                    boolean isSuccess = sysDeviceService.register(sysDevice, sysRegisterModel);
                    if (isSuccess) {
                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                        //将设备配置上传到Redis
                        judgeSysController.uploadDeviceConfig();
                        judgeSysController.uploadDeviceStatus();

                        // 发送设备配置到rabbitmq
                        sysManualController.sendDeviceConfig(sysRegisterModel.getGuid());
                        // 发送字典数据到rabbitmq
                        sysManualController.sendDictData(sysRegisterModel.getGuid());

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
        messageSender.sendManRegisterMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysRegisterModel.getGuid(), null,
                result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 手检站向后台服务发送登录信息
     *
     * @param sysLoginModel 登录信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.3.2 手检站向后台服务发送登录信息")
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
                sysDevice.setDeviceType(DeviceType.MANUAL.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
                } else {
                    boolean isSuccess = sysDeviceService.login(sysDevice, sysLoginModel);
                    if (isSuccess) {

                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                        //将设备状态和设备配置上传到Redis( upload device status and device config to redis)
                        judgeSysController.uploadDeviceConfig();
                        judgeSysController.uploadDeviceStatus();
                    } else {
                        result.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
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
        messageSender.sendManLoginMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysLoginModel.getGuid(), null,
                result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 手检站向后台服务发送登出信息
     *
     * @param sysLogoutModel 登出信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.3.3 手检站向后台服务发送登出信息")
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
                sysDevice.setDeviceType(DeviceType.MANUAL.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                } else {
                    boolean isSuccess = sysDeviceService.logout(sysDevice, sysLogoutModel);
                    if (isSuccess) {

                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                        //将设备状态和设备配置上传到Redis( upload device status and device config to redis)
                        judgeSysController.uploadDeviceConfig();
                        judgeSysController.uploadDeviceStatus();
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
        messageSender.sendManLogoutMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysLogoutModel.getGuid(), null,
                result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 手检站向后台服务发送开始工作通知
     *
     * @param sysDeviceStatusModel
     * @Param deviceStatus
     * @Param resultMessageVO
     * @return
     */
    @ApiOperation("4.3.3.4 手检站向后台服务发送开始工作通知")
    @PostMapping("device-start")
    public ResultMessageVO deviceStart(@ApiParam("开始工作通知") @RequestBody SysDeviceStatusModel sysDeviceStatusModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.man.sys.start");
        resultMessageVO.setKey(routingKey);
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysDeviceStatusModel.getGuid());
        int checkResult = sysDeviceStatusModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else {
            try {
                //// 启动设备
                if (judgeSysService.updateSysDeviceStatus(sysDeviceStatusModel, DeviceStatusType.START.getValue())) {
                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                    // redis
                    judgeSysController.uploadDeviceConfig();
                    judgeSysController.uploadDeviceStatus();
                } else {
                    result.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
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
        messageSender.sendSysManReplyMessage(encryptMsg, routingKey);
        serMqMessageService.save(resultMessageVO, 1, sysDeviceStatusModel.getGuid(), null,
                result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 手检站向后台服务发送暂停工作通知
     *
     * @param sysDeviceStatusModel
     * @Param deviceStatus
     * @Param resultMessageVO
     * @return
     */
    @ApiOperation("4.3.3.5 手检站向后台服务发送暂停工作通知")
    @PostMapping("device-stop")
    public ResultMessageVO deviceStop(@ApiParam("暂停工作通知") @RequestBody SysDeviceStatusModel sysDeviceStatusModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.man.sys.stop");
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

                    // redis
                    judgeSysController.uploadDeviceConfig();
                    judgeSysController.uploadDeviceStatus();
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
        messageSender.sendSysManReplyMessage(encryptMsg, routingKey);
        serMqMessageService.save(resultMessageVO, 1, sysDeviceStatusModel.getGuid(), null,
                result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 手检战向后台服务发送注销信息
     *
     * @param sysUnregisterModel 登录信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.3.6 手检战向后台服务发送注销信息")
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
                sysDevice.setDeviceType(DeviceType.MANUAL.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                } else {
                    // 取消注册设备
                    boolean isSuccess = sysDeviceService.unRegister(sysDevice, sysUnregisterModel);
                    if (isSuccess) {
                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                        judgeSysController.uploadDeviceConfig();
                        judgeSysController.uploadDeviceStatus();
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
        messageSender.sendManUnregisterMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysUnregisterModel.getGuid(), null,
                result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 手检站向后台服务发送日志信息
     *
     * @param serDevLogModel 设备向后台服务发送日志信息
     *
     */
    @ApiOperation("4.3.3.7 手检站向后台服务发送日志信息")
    @PostMapping("save-dev-log")
    public ResultMessageVO saveSerDevLog(@ApiParam("设备向后台服务发送日志信息") @RequestBody SerDevLogModel serDevLogModel) {
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
                log.error(e.toString());
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }


        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(result);
        messageSender.sendSerDevLogReplyMessage(resultMessageVO, exchangeName, routingKey);
        serMqMessageService.save(resultMessageVO, 1, serDevLogModel.getGuid(), null,
                result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 手检端向后台服务发送心跳信息
     *
     * @param heartBeatModel 心跳信息
     *
     */
    @ApiOperation("4.3.3.8 手检端向后台服务发送心跳信息")
    @PostMapping("save-heartbeat")
    public ResultMessageVO saveHeartBeatTime(@ApiParam(value = "心跳信息") @RequestBody HeartBeatModel heartBeatModel) {
        String exchangeName = null;
        String routingKey = null;
        HeartBeatReplyModel heartBeatReplyModel = new HeartBeatReplyModel();
        ResultMessageVO resultMessageVO = new ResultMessageVO();

        //设置Rabbitmq的主题密钥和路由密钥
        exchangeName = BackgroundServiceUtil.getConfig("topic.inter.dev.sys.status");
        routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.man.heartbeat");
        int checkResult = heartBeatModel.checkValid();
        if(checkResult == 1) {
            heartBeatReplyModel.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            heartBeatReplyModel.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = SysDevice.builder().guid(heartBeatModel.getGuid()).build();
                sysDevice = sysDeviceService.find(sysDevice);

                // 检查设备是否开启( check device is on)
                if(sysDeviceService.checkDeviceLogin(sysDevice)) {
                    //从数据库获取心跳
                    SerHeartBeat serHeartBeat = SerHeartBeat.builder().deviceId(sysDevice.getDeviceId()).deviceType(sysDevice.getDeviceType()).build();
                    SerHeartBeat oldSerHeartBeat = serHeartBeatService.find(serHeartBeat);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //修改心跳时间
                    if (oldSerHeartBeat == null) {
                        //insert
                        serHeartBeat.setHeartBeatTime(sdf.parse(heartBeatModel.getHeartbeatTime()));
                        serHeartBeatService.save(serHeartBeat);
                    } else {
                        //update
                        oldSerHeartBeat.setHeartBeatTime(sdf.parse(heartBeatModel.getHeartbeatTime()));
                        serHeartBeatService.save(oldSerHeartBeat);
                    }
                    heartBeatReplyModel.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                } else {
                    log.error("设备已关闭");
                    heartBeatReplyModel.setResult(CommonConstant.RESULT_FAIL.getValue());
                }
            } catch (Exception e) {
                heartBeatReplyModel.setResult(CommonConstant.RESULT_FAIL.getValue());
                log.error("无法保存心跳");
                log.error(e.getMessage());

            }
        }


        // 将结果发送到rabbitmq
        heartBeatReplyModel.setGuid(heartBeatModel.getGuid());
        heartBeatReplyModel.setHeartbeatTime(heartBeatModel.getHeartbeatTime());
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(heartBeatModel);
        messageSender.sendHeartBeatReplyMessage(resultMessageVO, exchangeName, routingKey);
        serMqMessageService.save(resultMessageVO, 1, heartBeatModel.getGuid(), null,
                heartBeatReplyModel.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 手检站后台服务请求提交手检结论
     *
     * @param handSerResultModel
     * @return ResultMessage
     */
    @ApiOperation("4.3.3.9 手检站后台服务请求提交手检结论")
    @PostMapping("save-hand-result")
    public ResultMessageVO saveHandResult(@RequestBody @ApiParam("请求报文定义") HandSerResultModel handSerResultModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        SendMessageModel sendMessageModel = new SendMessageModel();
        sendMessageModel.setGuid(handSerResultModel.getGuid());
        sendMessageModel.setImageGuid(handSerResultModel.getCheckResult().getImageGuid());
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.man.sys.manual.conclusion");
        int checkResult = handSerResultModel.checkValid();
        if(checkResult == 1) {
            sendMessageModel.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            sendMessageModel.setResult(CommonConstant.RESULT_INVALID_DATA.getValue());
        } else {
            boolean isSucceed = serHandResultService.saveHandResult(handSerResultModel);
            if (isSucceed) {
                // 4.3.1.19 后台服务向设备推送安检仪结论
                securitySysController.saveHandResult(handSerResultModel);
                sendMessageModel.setResult(CommonConstant.RESULT_SUCCESS.getValue());

            } else {
                sendMessageModel.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        }



        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(sendMessageModel);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendSaveHandResultReplyMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, handSerResultModel.getGuid(), handSerResultModel.getCheckResult().getImageGuid(),
                String.valueOf(sendMessageModel.getResult()));
        return resultMessageVO;
    }

}
