package com.nuctech.securitycheck.backgroundservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.*;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
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
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendSysDeviceSecurityInfoSaveReplyMessage(encryptMsg);
        writeResult(output, resultMessageVO);
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
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.register"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysRegisterModel.getGuid());
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysRegisterModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
            // 从数据库获取设备(get device data from database)
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            } else {
                //注册设备(register device)
                boolean isSuccess = sysDeviceService.register(sysDevice, sysRegisterModel);
                if (isSuccess) {

                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                    // 将设备配置上传到Redis
                    judgeSysController.uploadDeviceConfig();
                    judgeSysController.uploadDeviceStatus();

                    //发送字典给rabbitmq
                    sysSecurityController.sendDictData(sysRegisterModel.getGuid());

                    //发送设备配置信息到rabbitmq
                    sysSecurityController.sendDeviceConfig(sysRegisterModel.getGuid());

                    //后台服务向安检仪下发用户列表
                    sysSecurityController.sendUserList(sysRegisterModel.getGuid());
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                }
            }
        } catch (Exception e) {
            log.error("注册失败");
            log.error(e.getMessage());
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }

        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendDevRegisterMessage(encryptMsg);

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
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.login"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysLoginModel.getGuid());
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysLoginModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
            // 从数据库获取设备(get device data from database)
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            } else {
                boolean isSuccess = sysDeviceService.login(sysDevice, sysLoginModel);
                if (isSuccess) {

                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());

                    //将设备状态和设备配置上传到Redis( upload device status and device config to redis)
                    judgeSysController.uploadDeviceStatus();
                    judgeSysController.uploadDeviceConfig();
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                }
            }
        } catch (Exception e) {
            log.error("登录失败");
            log.error(e.getMessage());
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }

        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendDevLoginMessage(encryptMsg);

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
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.logout"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysLogoutModel.getGuid());
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysLogoutModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
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
                    judgeSysController.uploadDeviceStatus();
                    judgeSysController.uploadDeviceConfig();
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                }
            }
        } catch (Exception e) {
            log.error("登出失败");
            log.error(e.getMessage());
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }

        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendDevLogoutMessage(encryptMsg);

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
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.unregister"));
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysUnregisterModel.getGuid());
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysUnregisterModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
            // 从数据库获取设备(get device data from database)
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            } else {
                // 取消注册设备
                boolean isSuccess = sysDeviceService.unRegister(sysDevice, sysUnregisterModel);
                if (isSuccess) {
                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                    //将设备配置上传到Redis
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

        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendDevUnregisterMessage(encryptMsg);

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
        String exchangeName = null;
        String routingKey = null;
        HeartBeatReplyModel heartBeatReplyModel = new HeartBeatReplyModel();
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        //设置Rabbitmq的主题密钥和路由密钥
        exchangeName = BackgroundServiceUtil.getConfig("topic.inter.dev.sys");
        routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.heartbeat");

        try {
            // 从数据库获取设备(get device data from database)
            SysDevice sysDevice = SysDevice.builder().guid(heartBeatModel.getGuid()).build();
            sysDevice = sysDeviceService.find(sysDevice);

            // 检查设备是否开启( check device is on)
            if(sysDeviceService.checkDeviceLogin(sysDevice)) {
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
        heartBeatReplyModel.setGuid(heartBeatModel.getGuid());
        heartBeatReplyModel.setHeartbeatTime(heartBeatModel.getHeartbeatTime());
        // 将结果发送到rabbitmq
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(heartBeatModel);
        messageSender.sendHeartBeatReplyMessage(resultMessageVO, exchangeName, routingKey);
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
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        CommonResultVO result = new CommonResultVO();
        result.setGuid(sysDeviceVersionModel.getGuid());
        try {
            resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.updateversion"));

            // 从数据库获取设备(get device data from database)
            SysDevice sysDevice = SysDevice.builder().guid(sysDeviceVersionModel.getGuid()).build();
            sysDevice = sysDeviceService.find(sysDevice);
            // 检查设备是否开启( check device is on)
            if(sysDeviceService.checkDeviceLogin(sysDevice)) {
                if (sysDevice == null) {
                    sysDevice = new SysDevice();
                    sysDevice.setGuid(sysDeviceVersionModel.getGuid());
                }

                //更新设备信息
                sysDevice.setSoftwareVersion(sysDeviceVersionModel.getSoftwareVersion());
                sysDevice.setAlgorithmVersion(sysDeviceVersionModel.getAlgorithmVersion());
                sysDeviceService.save(sysDevice);

                //将设备配置上传到Redis
                judgeSysController.uploadDeviceConfig();
                judgeSysController.uploadDeviceStatus();

                result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
            } else {
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
                log.error("设备已关闭");
            }
        } catch (Exception e) {
            log.error("发送设备版本失败");
            log.error(e.toString());
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }
        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);

        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendSysDeviceVersionReplyMessage(encryptMsg);

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
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        CommonResultVO result = new CommonResultVO();
        result.setGuid(serDeviceStatusModel.getGuid());
        try {
            resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.currentstatus"));

            SysDevice sysDevice = SysDevice.builder()
                    .guid(serDeviceStatusModel.getGuid())
                    .build();

            // 从数据库获取设备(get device data from database)
            sysDevice = sysDeviceService.find(sysDevice);
            // 检查设备是否开启( check device is on)
            if(sysDeviceService.checkDeviceLogin(sysDevice)) {
                if (sysDevice == null) {
                    resultMessageVO.setContent(null);
                    return resultMessageVO;
                }

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
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            }

        } catch (Exception e) {
            log.error("无法发送设备状态");
            log.error(e.toString());
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }

        // 将结果发送到rabbitmq
        resultMessageVO.setContent(result);

        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendSerDeviceStatusReplyMessage(encryptMsg);

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
        ObjectMapper objectMapper = new ObjectMapper();

        CommonResultVO result = new CommonResultVO();
        result.setGuid(hardwareStatusModel.getGuid());

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.sys.hardwarestatus"));

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
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
                resultMessageVO.setContent(result);
                messageSender.sendHardwareStatusReplyMessage(CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
                return resultMessageVO;
            }

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
        } catch (Exception e) {
            log.error("无法发送硬件状态");
            log.error(e.getMessage());
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }
        resultMessageVO.setContent(result);
        messageSender.sendHardwareStatusReplyMessage(CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
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
        String exchangeName = null;
        String routingKey = null;
        CommonResultVO result = new CommonResultVO();
        result.setGuid(serDevLogModel.getGuid());
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        //设置Rabbitmq的主题密钥和路由密钥
        exchangeName = BackgroundServiceUtil.getConfig("topic.inter.dev.sys");
        routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.log");


        try {
            SysDevice sysDevice = SysDevice.builder().guid(serDevLogModel.getGuid()).build();
            sysDevice = sysDeviceService.find(sysDevice);

            if (!sysDeviceService.checkDeviceLogin(sysDevice)) {
                log.error("设备已关闭");
                result.setResult(CommonConstant.RESULT_FAIL.getValue());

            } else {
                // 将设备日志添加到数据库
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

                result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
            }
        } catch (Exception e) {
            log.error("无法保存设备日志");
            log.error(e.toString());
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }
        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(result);
        messageSender.sendSerDevLogReplyMessage(resultMessageVO, exchangeName, routingKey);
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
        // 判断 GUID有效
        boolean isAvailable = sysDeviceService.checkGuid(guid);
        if(!isAvailable) {
            log.error("不存在对应的GUID");
            return sendResult(CommonConstant.RESULT_FAIL.getValue(), logEnd, result, resultMessageVO);
        }


        // 接收数据以后保存
        ScanInfoSaveResultVO saveScanResult = securityImageInfoService.saveScanResult(devSerImageInfoModel);
        result.setMode(saveScanResult.getModeId());




        // 无法保存数据
        if (!saveScanResult.getIsSucceed()) {
            log.error("无法保存结果");
            return sendResult(CommonConstant.RESULT_FAIL.getValue(), logEnd, result, resultMessageVO);
        }

        // 数据有效
        if (devSerImageInfoModel.getImageData().getInvalidScan().equals(DeviceDefaultType.TRUE.getValue())) {
            log.error("无效的扫描图像");
            securityImageInfoService.sendInvalidResult(saveScanResult);
            return sendResult(CommonConstant.RESULT_SUCCESS.getValue(), logEnd, result, resultMessageVO);
        }



        // 安检仪+审图端 和 安检仪+审图端+手检端
        if (saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_JUDGE.getValue())
                || saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
            // 4.3.2.11 后台服务向判图站推送待判图图像信息
            sysJudgeController.sendJudgeImageInfo(saveScanResult.getTaskNumber());
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

        }

        if (saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY.getValue())) {
            securityImageInfoService.sendInvalidResult(saveScanResult);
        }
        return sendResult(CommonConstant.RESULT_SUCCESS.getValue(), logEnd, result, resultMessageVO);
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
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        CommonResultVO result = new CommonResultVO();
        String guid = devSerDataSyncModel.getGuid();
        result.setGuid(guid);
        // 判断 GUID有效
        boolean isAvailable = sysDeviceService.checkGuid(guid);
        // 接收数据以后保存
        boolean isSucceed = securityImageInfoService.synchronizeScanResult(devSerDataSyncModel);
        if (isAvailable) {      // GUID有效
            if (devSerDataSyncModel.getImageData().getInvalidScan().equals(DeviceDefaultType.FALSE.getValue())) {
                if (isSucceed) {
                    result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL.getValue());
                }
            } else {
                result.setResult(CommonConstant.RESULT_FAIL.getValue());
            }
        } else {
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.synchronization");

        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendSysDeviceSecurityInfoSynchronizeReplyMessage(encryptMsg);
        return resultMessageVO;
    }

}
