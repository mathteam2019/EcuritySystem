package com.nuctech.securitycheck.backgroundservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.*;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.DateUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.repositories.SerDeviceStatusRepository;
import com.nuctech.securitycheck.backgroundservice.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * SecuritySysController
 *
 * @author benja
 * @version v1.0
 * @since 2019-11-29
 */
@Api(tags = "SecuritySysController")
@RestController
@Slf4j
@RequestMapping(value = "api/security-sys/")
public class SecuritySysController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private SerDeviceStatusRepository devStatusRepo;

    @Autowired
    private ISysDeviceService sysDeviceService;

    @Autowired
    private ISerHeartBeatService serHeartBeatService;

    @Autowired
    private ISerDevLogService serDevLogService;

    @Autowired
    private ISerDeviceStatusService serDeviceStatusService;

    @Autowired
    private ISecurityImageInfoService securityImageInfoService;

    @Autowired
    private SysSecurityController sysSecurityController;

    @Autowired
    private SysJudgeController sysJudgeController;

    @Autowired
    private SysManualController sysManualController;

    @Autowired
    private JudgeSysController judgeSysController;

    @Autowired
    private ISysDeviceConfigService sysDeviceConfigService;

    @Autowired
    private ISerJudgeGraphService serJudgeGraphService;

    @Autowired
    private ISerHandResultService serHandResultService;

    @Autowired
    private ISerMqMessageService serMqMessageService;

    /**
     *写输出日志
     * @param output
     * @param object
     */
    private void writeResult(String output, Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info(output);
        try {
            String resultMessageVOStr = objectMapper.writeValueAsString(object);
            log.info(resultMessageVOStr);
        } catch(Exception ex) {
            log.error("转换模型时发生错误");
            log.error(ex.getMessage());
        }
    }

    /**
     * 返回失败结果
     * @param resultValue
     * @param result
     * @param resultMessageVO
     * @return
     */
    private ResultMessageVO sendResult(Integer resultValue, String output, SendMessageModel result, ResultMessageVO resultMessageVO) {
        result.setResult(resultValue);
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendSysDeviceSecurityInfoSaveReplyMessage(encryptMsg);
        writeResult(output, resultMessageVO);
        serMqMessageService.save(resultMessageVO, 1, result.getGuid(), result.getImageGuid(), String.valueOf(result.getResult()));
        return resultMessageVO;
    }



    /**
     * 安检仪向后台服务发送注册信息
     *
     * @param sysRegisterModel 注册信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.1.1 安检仪向后台服务发送注册信息")
    @PostMapping("send-register")
    public ResultMessageVO sendRegister(@ApiParam("注册信息") @RequestBody SysRegisterModel sysRegisterModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.register"));
        receivceMessageVO.setContent(sysRegisterModel);
        serMqMessageService.save(receivceMessageVO, 0, sysRegisterModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.register"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysRegisterModel.getGuid());
        int checkResult = sysRegisterModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = new SysDevice();
                sysDevice.setGuid(sysRegisterModel.getGuid());
                sysDevice.setStatus(DefaultType.TRUE.getValue());
                sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                } else {
                    //注册设备(register device)
                    boolean isSuccess = sysDeviceService.register(sysDevice, sysRegisterModel);
                    if (isSuccess) {

                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                        // 将设备配置上传到Redis
                        judgeSysController.uploadDeviceConfig();
                        judgeSysController.uploadDeviceStatus();

                        //4.3.1.6 后台服务向安检仪下发用户列表
                        sysSecurityController.sendUserList(sysRegisterModel.getGuid());

                        //4.3.1.5 发送设备配置信息到rabbitmq
                        sysSecurityController.sendDeviceConfig(sysRegisterModel.getGuid());

                        //4.3.1.7 发送字典给rabbitmq
                        sysSecurityController.sendDictData(sysRegisterModel.getGuid());
                    } else {
                        result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("注册失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendDevRegisterMessage(encryptMsg);

        serMqMessageService.save(resultMessageVO, 1, sysRegisterModel.getGuid(), null, result.getResult().toString());

        return resultMessageVO;
    }

    /**
     * 安检仪向后台服务发送登录信息
     *
     * @param sysLoginModel 登录信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.1.2 安检仪向后台服务发送登录信息")
    @PostMapping("send-login")
    public ResultMessageVO sendLogin(@ApiParam("登录信息") @RequestBody SysLoginModel sysLoginModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.login"));
        receivceMessageVO.setContent(sysLoginModel);
        serMqMessageService.save(receivceMessageVO, 0, sysLoginModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.login"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysLoginModel.getGuid());
        int checkResult = sysLoginModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = new SysDevice();
                sysDevice.setGuid(sysLoginModel.getGuid());
                sysDevice.setStatus(DefaultType.TRUE.getValue());
                sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                } else {
                    boolean isSuccess = sysDeviceService.login(sysDevice, sysLoginModel);
                    if (isSuccess) {

                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                        //将设备状态和设备配置上传到Redis( upload device status and device config to redis)
                        judgeSysController.uploadDeviceStatus();
                        judgeSysController.uploadDeviceConfig();
                    } else {
                        result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("登录失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendDevLoginMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysLoginModel.getGuid(), null, result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 安检仪向后台服务发送登出信息
     *
     * @param sysLogoutModel 登出信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.1.3 安检仪向后台服务发送登出信息")
    @PostMapping("send-logout")
    public ResultMessageVO sendLogout(@ApiParam("登出信息") @RequestBody SysLogoutModel sysLogoutModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.logout"));
        receivceMessageVO.setContent(sysLogoutModel);
        serMqMessageService.save(receivceMessageVO, 0, sysLogoutModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.logout"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysLogoutModel.getGuid());
        int checkResult = sysLogoutModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = new SysDevice();
                sysDevice.setGuid(sysLogoutModel.getGuid());
                sysDevice.setStatus(DefaultType.TRUE.getValue());
                sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                } else {
                    // 注销设备
                    boolean isSuccess = sysDeviceService.logout(sysDevice, sysLogoutModel);
                    if (isSuccess) {

                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                        //将设备状态和设备配置上传到Redis( upload device status and device config to redis)
                        judgeSysController.uploadDeviceStatus();
                        judgeSysController.uploadDeviceConfig();
                    } else {
                        result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("登出失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendDevLogoutMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysLogoutModel.getGuid(), null, result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 安检仪向后台服务发送注销信息
     *
     * @param sysUnregisterModel 登录信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.1.4 安检仪向后台服务发送注销信息")
    @PostMapping("send-unregister")
    public ResultMessageVO sendUnregister(@ApiParam("登录信息") @RequestBody SysUnregisterModel sysUnregisterModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.unregister"));
        receivceMessageVO.setContent(sysUnregisterModel);
        serMqMessageService.save(receivceMessageVO, 0, sysUnregisterModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.unregister"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysUnregisterModel.getGuid());
        int checkResult = sysUnregisterModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = new SysDevice();
                sysDevice.setGuid(sysUnregisterModel.getGuid());
                sysDevice.setStatus(DefaultType.TRUE.getValue());
                sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                if (sysDevice == null) {
                    result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                } else {
                    // 取消注册设备
                    boolean isSuccess = sysDeviceService.unRegister(sysDevice, sysUnregisterModel);
                    if (isSuccess) {
                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                        //将设备配置上传到Redis
                        judgeSysController.uploadDeviceConfig();
                        judgeSysController.uploadDeviceStatus();
                    } else {
                        result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("取消注册失败");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendDevUnregisterMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysUnregisterModel.getGuid(), null, result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 安检仪向后台服务发送心跳信息
     *
     * @param heartBeatModel 心跳信息
     * @Param deviceType 设备类型
     */
    @ApiOperation("4.3.1.8 安检仪向后台服务发送心跳信息")
    @PostMapping("save-heartbeat")
    public void saveHeartBeatTime(@ApiParam(value = "心跳信息") @RequestBody HeartBeatModel heartBeatModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.heartbeat"));
        receivceMessageVO.setContent(heartBeatModel);
        serMqMessageService.save(receivceMessageVO, 0, heartBeatModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        String exchangeName = null;
        String routingKey = null;
        HeartBeatReplyModel heartBeatReplyModel = new HeartBeatReplyModel();
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        //设置Rabbitmq的主题密钥和路由密钥
        exchangeName = BackgroundServiceUtil.getConfig("topic.inter.dev.sys.status");
        routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.heartbeat");
        int checkResult = heartBeatModel.checkValid();
        if(checkResult == 1) {
            heartBeatReplyModel.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            heartBeatReplyModel.setResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue());
        } else {
            try {
                // 从数据库获取设备(get device data from database)
                SysDevice sysDevice = SysDevice.builder().guid(heartBeatModel.getGuid()).build();
                sysDevice = sysDeviceService.find(sysDevice);

                // 检查设备是否开启( check device is on)
                if(sysDeviceService.checkDeviceLogin(sysDevice)) {
                    SerHeartBeat serHeartBeat = SerHeartBeat.builder().deviceId(sysDevice.getDeviceId()).deviceType(sysDevice.getDeviceType()).build();
                    SerHeartBeat oldSerHeartBeat = serHeartBeatService.find(serHeartBeat);
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
                    heartBeatReplyModel.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                }
            } catch (Exception e) {
                log.error("无法保存心跳");
                log.error(e.getMessage());
                heartBeatReplyModel.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }

        heartBeatReplyModel.setGuid(heartBeatModel.getGuid());
        heartBeatReplyModel.setHeartbeatTime(heartBeatModel.getHeartbeatTime());
        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(heartBeatModel);
        messageSender.sendHeartBeatReplyMessage(resultMessageVO, exchangeName, routingKey);
        serMqMessageService.save(resultMessageVO, 1, heartBeatModel.getGuid(), null, heartBeatReplyModel.getResult().toString());
    }

    /**
     * 安检仪向后台服务更新版本号信息（算法切换后更新）
     *
     * @param sysDeviceVersionModel 更新版本号信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.1.9 安检仪向后台服务更新版本号信息（算法切换后更新）")
    @PostMapping("send-device-version")
    public ResultMessageVO sendDeviceVersion(@ApiParam("安检仪向后台服务更新版本号信息") @RequestBody SysDeviceVersionModel sysDeviceVersionModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.updateversion"));
        receivceMessageVO.setContent(sysDeviceVersionModel);
        serMqMessageService.save(receivceMessageVO, 0, sysDeviceVersionModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysDeviceVersionModel.getGuid());
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.updateversion"));
        int checkResult = sysDeviceVersionModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else {
            try {

                // 从数据库获取设备(get device data from database)
                SysDevice sysDevice = SysDevice.builder().guid(sysDeviceVersionModel.getGuid()).build();
                sysDevice = sysDeviceService.find(sysDevice);
                // 检查设备是否开启( check device is on)
                if(sysDeviceService.checkDeviceLogin(sysDevice)) {
                    //更新设备信息
                    sysDevice.setSoftwareVersion(sysDeviceVersionModel.getSoftwareVersion());
                    sysDevice.setAlgorithmVersion(sysDeviceVersionModel.getAlgorithmVersion());
                    sysDeviceService.save(sysDevice);

                    //将设备配置上传到Redis
                    judgeSysController.uploadDeviceConfig();
                    judgeSysController.uploadDeviceStatus();

                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                } else {
                    result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                }
            } catch (Exception e) {
                log.error("发送设备版本失败");
                log.error(e.toString());
                result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }

        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);

        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendSysDeviceVersionReplyMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, sysDeviceVersionModel.getGuid(), null, result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 安检仪向后台服务发送 flow 信息（即时状态）
     *
     * @param serDeviceStatusModel flow信息
     * @return ResultMessage
     */
    @ApiOperation("4.3.1.10 安检仪向后台服务发送 flow 信息（即时状态）")
    @PostMapping("send-ser-device-status")
    public ResultMessageVO sendSerDeviceStatus(@ApiParam("serDeviceStatus") @RequestBody SerDeviceStatusModel serDeviceStatusModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.currentstatus"));
        receivceMessageVO.setContent(serDeviceStatusModel);
        serMqMessageService.save(receivceMessageVO, 0, serDeviceStatusModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        CommonResultVO result = new CommonResultVO();
        result.setGuid(serDeviceStatusModel.getGuid());
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.currentstatus"));
        int checkResult = serDeviceStatusModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else {
            try {
                SysDevice sysDevice = SysDevice.builder()
                        .guid(serDeviceStatusModel.getGuid())
                        .build();

                // 从数据库获取设备(get device data from database)
                sysDevice = sysDeviceService.find(sysDevice);
                // 检查设备是否开启( check device is on)
                if(sysDeviceService.checkDeviceLogin(sysDevice)) {
                    //更新或创建设备状态(update or create device stauts)
                    SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
                    serDeviceStatus.setDeviceId(sysDevice.getDeviceId());

                    Example<SerDeviceStatus> devStatusEx = Example.of(serDeviceStatus);
                    serDeviceStatus = devStatusRepo.findOne(devStatusEx);

                    if (serDeviceStatus == null) {
                        serDeviceStatus = new SerDeviceStatus();
                        serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
                    }
                    serDeviceStatus.setCurrentWorkflow(serDeviceStatusModel.getFlowName());
                    serDeviceStatus.setCurrentStatus(serDeviceStatusModel.getFlowStatus());
                    serDeviceStatus.setDiskSpace(serDeviceStatusModel.getDiskSpace());

                    serDeviceStatusService.save(serDeviceStatus);
                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                } else {
                    log.error("设备已关闭");
                    result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                }

            } catch (Exception e) {
                log.error("无法发送设备状态");
                log.error(e.toString());
                result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);

        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendSerDeviceStatusReplyMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, serDeviceStatusModel.getGuid(), null, result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 设备向后台服务发送 flow 信息。每次状态更新即发送。
     *
     * @param hardwareStatusModel
     * @return resultMessage
     * @throws Exception
     */
    @ApiOperation("4.3.1.11 设备向后台服务发送 flow 信息。每次状态更新即发送")
    @PostMapping("send-hardwarestatus")
    public ResultMessageVO sendHardwareStatus(@ApiParam(value = "HardwareStatus") @RequestBody HardwareStatusModel hardwareStatusModel) throws Exception {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.hardwarestatus"));
        receivceMessageVO.setContent(hardwareStatusModel);
        serMqMessageService.save(receivceMessageVO, 0, hardwareStatusModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        ObjectMapper objectMapper = new ObjectMapper();

        CommonResultVO result = new CommonResultVO();
        result.setGuid(hardwareStatusModel.getGuid());

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.hardwarestatus"));
        int checkResult = hardwareStatusModel.checkValid();
        if(checkResult == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue());
        } else {
            try {
                // 从数据库获取设备(get device data from database)
                SysDevice sysDevice = SysDevice.builder()
                        .guid(hardwareStatusModel.getGuid())
                        .build();
                // get SysDevice by guid if exist
                sysDevice = sysDeviceService.find(sysDevice);
                // 检查设备是否开启( check device is on)
                if (!sysDeviceService.checkDeviceLogin(sysDevice)) {
                    log.error("设备已关闭");
                    result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());

                } else {
                    //更新或创建设备状态(update or create device stauts)
                    SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
                    serDeviceStatus.setDeviceId(sysDevice.getDeviceId());

                    Example<SerDeviceStatus> devStatusEx = Example.of(serDeviceStatus);
                    serDeviceStatus = devStatusRepo.findOne(devStatusEx);

                    if (serDeviceStatus == null) {
                        serDeviceStatus = new SerDeviceStatus();
                        serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
                    }

                    serDeviceStatus.setPlcStatus(hardwareStatusModel.getPLC());
                    serDeviceStatus.setMasterCardStatus(hardwareStatusModel.getCaptureCardMainStatus());
                    serDeviceStatus.setSlaveCardStatus(hardwareStatusModel.getCaptureCardSecondStatus());
                    serDeviceStatus.setSerVo(hardwareStatusModel.getServoStatus());
                    serDeviceStatus.setEmergencyStop(hardwareStatusModel.getEmergencyStop());
                    serDeviceStatus.setFootWarning(hardwareStatusModel.getFootAlarmOnLine());

                    devStatusRepo.save(serDeviceStatus);
                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                }

            } catch (Exception e) {
                log.error("无法发送硬件状态");
                log.error(e.getMessage());
                result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }

        resultMessageVO.setContent(result);
        messageSender.sendHardwareStatusReplyMessage(CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
        serMqMessageService.save(resultMessageVO, 1, hardwareStatusModel.getGuid(), null, result.getResult().toString());
        return resultMessageVO;
    }

    /**
     * 安检仪向后台服务发送日志信息
     *
     * @param serDevLogModel 安检仪向后台服务发送日志信息
     * @Param deviceType     设备类型
     */
    @ApiOperation("4.3.1.12 安检仪向后台服务发送日志信息")
    @PostMapping("save-dev-log")
    public void saveSerDevLog(@ApiParam("安检仪向后台服务发送日志信息") @RequestBody SerDevLogModel serDevLogModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.log"));
        receivceMessageVO.setContent(serDevLogModel);
        serMqMessageService.save(receivceMessageVO, 0, serDevLogModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

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
            result.setResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue());
        } else {
            try {
                SysDevice sysDevice = SysDevice.builder().guid(serDevLogModel.getGuid()).build();
                sysDevice = sysDeviceService.find(sysDevice);

                if (!sysDeviceService.checkDeviceLogin(sysDevice)) {
                    log.error("设备已关闭");
                    result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());

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
                result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }

        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(result);
        messageSender.sendSerDevLogReplyMessage(resultMessageVO, exchangeName, routingKey);
        serMqMessageService.save(resultMessageVO, 1, serDevLogModel.getGuid(), null, result.getResult().toString());
    }





    /**
     * 安检仪向后台服务发送扫描图像信息
     *
     * @param devSerImageInfoModel
     * @return resultMessage
     */
    @ApiOperation("4.3.1.13 安检仪向后台服务发送扫描图像信息")
    @PostMapping("save-scan-result")
    public ResultMessageVO saveScanResult(@RequestBody @ApiParam("请求报文定义") DevSerImageInfoModel devSerImageInfoModel) {

        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.imageinfo"));
        receivceMessageVO.setContent(devSerImageInfoModel);
        serMqMessageService.save(receivceMessageVO, 0, devSerImageInfoModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        String logStart = "安检仪向后台服务发送扫描图像信息输入";
        String logEnd = "安检仪向后台服务发送扫描图像信息输出";
        writeResult(logStart, devSerImageInfoModel);
        ObjectMapper objectMapper = new ObjectMapper();

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        SendMessageModel result = new SendMessageModel();
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.imageinfo");

        String guid = devSerImageInfoModel.getGuid();
        result.setGuid(guid);
        result.setImageGuid(devSerImageInfoModel.getImageData().getImageGuid());
        resultMessageVO.setKey(routingKey);

        if(devSerImageInfoModel.getImageData().checkValid() == 1 || devSerImageInfoModel.getPersonData().checkValid() == 1) {
            return sendResult(CommonConstant.RESULT_EMPTY.getValue(), logEnd, result, resultMessageVO);
        }
        if(devSerImageInfoModel.getImageData().checkValid() == 2 || devSerImageInfoModel.getPersonData().checkValid() == 2) {
            return sendResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue(), logEnd, result, resultMessageVO);
        }
        // 判断 GUID有效
        boolean isAvailable = sysDeviceService.checkGuid(guid);
        if(!isAvailable) {
            log.error("不存在对应的GUID");
            return sendResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue(), logEnd, result, resultMessageVO);
        }


        // 接收数据以后保存
        ScanInfoSaveResultVO saveScanResult = securityImageInfoService.saveScanResult(devSerImageInfoModel);
        result.setMode(saveScanResult.getModeId());




        // 无法保存数据
        if (!saveScanResult.getIsSucceed()) {
            log.error("无法保存结果");
            return sendResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue(), logEnd, result, resultMessageVO);
        }

        // 数据有效
        if (devSerImageInfoModel.getImageData().getInvalidScan().equals(DeviceDefaultType.TRUE.getValue())) {
            log.error("无效的扫描图像");
            securityImageInfoService.sendInvalidResult(saveScanResult);
            return sendResult(CommonConstant.RESULT_SUCCESS.getValue(), logEnd, result, resultMessageVO);
        }

        sendResult(CommonConstant.RESULT_SUCCESS.getValue(), logEnd, result, resultMessageVO);

        // 安检仪+审图端 和 安检仪+审图端+手检端
        if (saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_JUDGE.getValue())
                || saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
            // 4.3.2.11 后台服务向判图站推送待判图图像信息
            sysJudgeController.sendJudgeImageInfo(devSerImageInfoModel);
        }

        // 安检仪+手检端 和 安检仪+审图端+手检端
        if (saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_MANUAL.getValue())
                || saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
            if (devSerImageInfoModel.getImageData().getAtrResult().equals(DeviceDefaultType.FALSE.getValue())) {          // 报警、有嫌疑
                try {
                    // 4.3.1.16 后台服务向安检仪推送调度的手检站信息
                    sysSecurityController.dispatchManual(saveScanResult.getGuid(), saveScanResult.getTaskInfoVO());
                } catch (Exception e) {
                    log.error("后台服务向安检仪推送调度的手检站信息 报错：" + e.getMessage());
                }
            } else {        //ATR 通过, 无嫌疑
                SysDevice deviceModel = new SysDevice();
                deviceModel.setGuid(guid);
                SysDevice sysDevice = sysDeviceService.find(deviceModel);
                SysDeviceConfig sysDeviceConfig = sysDeviceConfigService.findLastConfig(sysDevice.getDeviceId());


                if (sysDeviceConfig.getAtrSwitch().equals(DeviceDefaultType.TRUE.getValue())) {       // 无嫌疑配置
                    log.error("无嫌疑配置");
                    securityImageInfoService.sendInvalidResult(saveScanResult);
                    return sendResult(CommonConstant.RESULT_SUCCESS.getValue(), logEnd, result, resultMessageVO);
                } else {
                    try {
                        // 4.3.1.16 后台服务向安检仪推送调度的手检站信息
                        sysSecurityController.dispatchManual(saveScanResult.getGuid(), saveScanResult.getTaskInfoVO());
                    } catch (Exception e) {
                        log.error("后台服务向安检仪推送调度的手检站信息 报错：" + e.getMessage());
                    }
                }
            }
        }

        // 安检仪+手检端
        if (saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_MANUAL.getValue())) {
            //check assigned hand device or not
            DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO = sysDeviceService.isManualDeviceDispatched(saveScanResult.getTaskNumber());
            if(dispatchManualDeviceInfoVO == null) {
                //4.3.1.20 后台服务向安检仪推送手检结论
                dispatchManualDeviceInfoVO = new DispatchManualDeviceInfoVO();
                dispatchManualDeviceInfoVO.setGuid(guid);
                dispatchManualDeviceInfoVO.setImageGuid(saveScanResult.getTaskNumber());
                assignSecurityDevice(dispatchManualDeviceInfoVO);
            }
        }

        if (saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY.getValue())) {
            securityImageInfoService.sendInvalidResult(saveScanResult);
        }

        return resultMessageVO;
    }

    /**
     * 安检仪向后台服务同步数据
     *
     * @param devSerDataSyncModel
     * @return resultMessage
     */
    @ApiOperation("4.3.1.14 安检仪向后台服务同步数据")
    @PostMapping("synchronize-scan-result")
    public ResultMessageVO synchronizeScanResult(@RequestBody @ApiParam("请求报文定义") DevSerDataSyncModel devSerDataSyncModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.data.synchronization"));
        receivceMessageVO.setContent(devSerDataSyncModel);
        serMqMessageService.save(receivceMessageVO, 0, devSerDataSyncModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        CommonResultVO result = new CommonResultVO();
        String guid = devSerDataSyncModel.getGuid();
        result.setGuid(guid);
        if(devSerDataSyncModel.getImageData().checkValid() == 1 || devSerDataSyncModel.getPersonData().checkValid() == 1) {
            result.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(devSerDataSyncModel.getImageData().checkValid() == 2 || devSerDataSyncModel.getPersonData().checkValid() == 2) {
            result.setResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue());
        } else {
            // 判断 GUID有效
            boolean isAvailable = sysDeviceService.checkGuid(guid);
            // 接收数据以后保存
            boolean isSucceed = securityImageInfoService.synchronizeScanResult(devSerDataSyncModel);
            if (isAvailable) {      // GUID有效
                if (devSerDataSyncModel.getImageData().getInvalidScan().equals(DeviceDefaultType.FALSE.getValue())) {
                    if (isSucceed) {
                        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                    } else {
                        result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                    }
                } else {
                    result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
                }
            } else {
                result.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }

        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.data.synchronization");

        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendSysDeviceSecurityInfoSynchronizeReplyMessage(encryptMsg);
        serMqMessageService.save(resultMessageVO, 1, devSerDataSyncModel.getGuid(), null, result.getResult().toString());
        return resultMessageVO;
    }





    /**
     * 后台服务向安检仪推送手检结论
     *
     * @param handSerResultModel
     * @return resultMessage
     */
    @ApiOperation("4.3.1.19 后台服务向安检仪推送手检结论")
    @PostMapping("save-hand-result")
    public ResultMessageVO saveHandResult(@RequestBody @ApiParam("请求报文定义") HandSerResultModel handSerResultModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
//        SendMessageModel sendMessageModel = new SendMessageModel();
//        sendMessageModel.setGuid(handSerResultModel.getGuid());
//        sendMessageModel.setImageGuid(handSerResultModel.getCheckResult().getImageGuid());
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.dev.manual.result");
        //sendMessageModel.setResult(CommonConstant.RESULT_SUCCESS.getValue());

        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(handSerResultModel);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendSysDevMessage(encryptMsg, routingKey);
        serMqMessageService.save(resultMessageVO, 1, handSerResultModel.getGuid(), handSerResultModel.getCheckResult().getImageGuid(),
                handSerResultModel.getCheckResult().getResult());
        return resultMessageVO;
    }

    /**
     * 后台服务向安检仪推送手检结论
     *
     * @param dispatchManualDeviceInfoVO
     * @return resultMessage
     */
    @ApiOperation("4.3.1.20 后台服务向安检仪推送手检结论")
    @PostMapping("assign-security-device")
    public ResultMessageVO assignSecurityDevice(@RequestBody @ApiParam("请求报文定义") DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
//        SendMessageModel sendMessageModel = new SendMessageModel();
//        sendMessageModel.setGuid(dispatchManualDeviceInfoVO.getGuid());
//        sendMessageModel.setImageGuid(dispatchManualDeviceInfoVO.getImageGuid());

        String routingKey = BackgroundServiceUtil.getConfig("routingKey.dev.manual.notice");

//        boolean isSucceed = sysDeviceService.checkSecurityHandDevice(dispatchManualDeviceInfoVO.getGuid());
//        if (isSucceed) {
//            sendMessageModel.setResult(CommonConstant.RESULT_SUCCESS.getValue());
//        } else {
//            sendMessageModel.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
//        }

        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(dispatchManualDeviceInfoVO);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendSysDevMessage(encryptMsg, routingKey);
        serMqMessageService.save(resultMessageVO, 1, dispatchManualDeviceInfoVO.getGuid(), dispatchManualDeviceInfoVO.getImageGuid(),
                CommonConstant.RESULT_SUCCESS.getValue().toString());
        return resultMessageVO;
    }


    /**
     * 安检仪向后台服务请求本机手检
     *
     * @param dispatchManualDeviceInfoVO
     * @return resultMessage
     */
    @ApiOperation("4.3.1.21 安检仪向后台服务请求本机手检")
    @PostMapping("available-hand")
    public ResultMessageVO availableCheckHand(@RequestBody @ApiParam("请求报文定义") DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.manual.task"));
        receivceMessageVO.setContent(dispatchManualDeviceInfoVO);
        serMqMessageService.save(receivceMessageVO, 0, dispatchManualDeviceInfoVO.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        SendMessageModel sendMessageModel = new SendMessageModel();
        sendMessageModel.setGuid(dispatchManualDeviceInfoVO.getGuid());
        sendMessageModel.setImageGuid(dispatchManualDeviceInfoVO.getImageGuid());

        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.manual.task");

        if(dispatchManualDeviceInfoVO.checkValid() == 1) {
            sendMessageModel.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else {
            boolean isSucceed = securityImageInfoService.checkAssignHand(dispatchManualDeviceInfoVO);
            if (isSucceed) {
                sendMessageModel.setResult(CommonConstant.RESULT_SUCCESS.getValue());
            } else {
                sendMessageModel.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }


        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(sendMessageModel);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendDevSysMessage(encryptMsg, routingKey);
        serMqMessageService.save(resultMessageVO, 1, dispatchManualDeviceInfoVO.getGuid(), dispatchManualDeviceInfoVO.getImageGuid(),
                CommonConstant.RESULT_SUCCESS.getValue().toString());
        return resultMessageVO;
    }

    /**
     * 安检仪本地手检站后台服务请求提交手检结论
     *
     * @param handSerResultModel
     * @return resultMessage
     */
    @ApiOperation("4.3.1.22 安检仪本地手检站后台服务请求提交手检结论")
    @PostMapping("save-hand-result-security")
    public ResultMessageVO saveHandResultFromSecurity(@RequestBody @ApiParam("请求报文定义") HandSerResultModel handSerResultModel) {
        ResultMessageVO receivceMessageVO = new ResultMessageVO();
        receivceMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.manual.conclusion"));
        receivceMessageVO.setContent(handSerResultModel);
        serMqMessageService.save(receivceMessageVO, 0, handSerResultModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        SendMessageModel sendMessageModel = new SendMessageModel();
        sendMessageModel.setGuid(handSerResultModel.getGuid());
        sendMessageModel.setImageGuid(handSerResultModel.getCheckResult().getImageGuid());
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.manual.conclusion");
        int checkResult = handSerResultModel.checkValid();
        if(checkResult == 1) {
            sendMessageModel.setResult(CommonConstant.RESULT_EMPTY.getValue());
        } else if(checkResult == 2) {
            sendMessageModel.setResult(CommonConstant.RESULT_INVALID_PARAM_DATA.getValue());
        } else {
            boolean isSucceed = serHandResultService.saveHandResult(handSerResultModel);

            if (isSucceed) {
                // 4.3.1.19 后台服务向设备推送安检仪结论
                saveHandResult(handSerResultModel);
                sendMessageModel.setResult(CommonConstant.RESULT_SUCCESS.getValue());
            } else {
                sendMessageModel.setResult(CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue());
            }
        }



        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(sendMessageModel);
        String encryptMsg = CryptUtil.encrypt(CryptUtil.getJSONString(resultMessageVO));
        messageSender.sendDevSysMessage(encryptMsg, routingKey);
        serMqMessageService.save(resultMessageVO, 1, handSerResultModel.getGuid(), handSerResultModel.getCheckResult().getImageGuid(),
                CommonConstant.RESULT_SUCCESS.getValue().toString());

        return resultMessageVO;
    }

}
