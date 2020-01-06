package com.nuctech.securitycheck.backgroundservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.constants.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.DefaultType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceDefaultType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.enums.WorkModeType;
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
import java.util.ArrayList;
import java.util.List;

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
    private ISysDeviceConfigService sysDeviceConfigService;

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
        List<SysSecurityInfoVO> sysSecurityInfoVOList = new ArrayList<SysSecurityInfoVO>();
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysRegisterModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
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

                    sysSecurityController.sendDictData(sysRegisterModel.getGuid(), sysDevice.getDeviceType());
                    sysSecurityController.sendDeviceConfig(sysRegisterModel.getGuid());
                    sysSecurityController.sendUserList(sysRegisterModel.getGuid());
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
        List<SysMonitoringDeviceStatusInfoVO> monitoringList = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
        List<SysSecurityInfoVO> sysSecurityInfoVOList = new ArrayList<SysSecurityInfoVO>();
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysLoginModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
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
        List<SysMonitoringDeviceStatusInfoVO> monitoringList = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
        List<SysSecurityInfoVO> sysSecurityInfoVOList = new ArrayList<SysSecurityInfoVO>();
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysLogoutModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
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
        List<SysSecurityInfoVO> sysSecurityInfoVOList = new ArrayList<SysSecurityInfoVO>();
        try {
            SysDevice sysDevice = new SysDevice();
            sysDevice.setGuid(sysUnregisterModel.getGuid());
            sysDevice.setStatus(DefaultType.TRUE.getValue());
            sysDevice.setDeviceType(DeviceType.SECURITY.getValue());
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

            SysDevice sysDevice = SysDevice.builder().guid(sysDeviceVersionModel.getGuid()).build();
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                sysDevice = new SysDevice();
                sysDevice.setGuid(sysDeviceVersionModel.getGuid());
            }
            sysDevice.setSoftwareVersion(sysDeviceVersionModel.getSoftwareVersion());
            sysDevice.setAlgorithmVersion(sysDeviceVersionModel.getAlgorithmVersion());
            sysDeviceService.save(sysDevice);

            // redis
            List<SysSecurityInfoVO> sysSecurityInfoVOList = sysDeviceService.findSecurityInfoList();
            String jsonStr = JSONObject.toJSONString(sysSecurityInfoVOList);

            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CryptUtil.encrypt(jsonStr));
            redisUtil.expire(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"), CommonConstant.EXPIRE_TIME);

            result.setResult(CommonConstant.RESULT_SUCCESS);
        } catch (Exception e) {
            log.error(e.toString());
            result.setResult(CommonConstant.RESULT_FAIL);
        }

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

            // get SysDevice by guid if exist
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                resultMessageVO.setContent(null);
                return resultMessageVO;
            }

            // get SerDeviceStatus by device_id
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
            result.setResult(CommonConstant.RESULT_SUCCESS);
        } catch (Exception e) {
            log.error(e.toString());
            result.setResult(CommonConstant.RESULT_FAIL);
        }

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

            SysDevice sysDevice = SysDevice.builder()
                    .guid(hardwareStatusModel.getGuid())
                    .build();
            // get SysDevice by guid if exist
            sysDevice = sysDeviceService.find(sysDevice);
            if (sysDevice == null) {
                result.setResult(CommonConstant.RESULT_FAIL);
                resultMessageVO.setContent(result);
                messageSender.sendHardwareStatusReplyMessage(CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
                return resultMessageVO;
            }

            // get SerDeviceStatus by device_id
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
            result.setResult(CommonConstant.RESULT_SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setResult(CommonConstant.RESULT_FAIL);
        }
        resultMessageVO.setContent(result);
        messageSender.sendHardwareStatusReplyMessage(CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
        return resultMessageVO;
    }

    /**
     * 安检仪向后台服务发送日志信息
     *
     * @param serDevLogModel 安检仪向后台服务发送日志信息
     * @param deviceType     设备类型
     */
    @ApiOperation("4.3.1.12 安检仪向后台服务发送日志信息")
    @PostMapping("save-dev-log")
    public void saveSerDevLog(@ApiParam("安检仪向后台服务发送日志信息") @RequestBody SerDevLogModel serDevLogModel, @ApiParam("设备类型") @RequestParam DeviceType deviceType) {
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
     * 安检仪向后台服务发送扫描图像信息
     *
     * @param devSerImageInfoModel
     * @return resultMessage
     */
    @ApiOperation("4.3.1.13 安检仪向后台服务发送扫描图像信息")
    @PostMapping("save-scan-result")
    public ResultMessageVO saveScanResult(@RequestBody @ApiParam("请求报文定义") DevSerImageInfoModel devSerImageInfoModel) {
        ObjectMapper objectMapper = new ObjectMapper();
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        CommonResultVO result = new CommonResultVO();

        String guid = devSerImageInfoModel.getGuid();
        result.setGuid(guid);
        // 判断 GUID有效
        boolean isAvailable = sysDeviceService.checkGuid(guid);
        // 接收数据以后保存
        ScanInfoSaveResultVO saveScanResult = securityImageInfoService.saveScanResult(devSerImageInfoModel);
        if (isAvailable) {      // GUID有效
            if (devSerImageInfoModel.getInvalidScan().equals(DeviceDefaultType.FALSE.getValue())) {       // 数据有效
                String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.imageinfo");
                if (saveScanResult.getIsSucceed()) {
                    result.setResult(CommonConstant.RESULT_SUCCESS);

                    // 安检仪+审图端 和 安检仪+审图端+手检端
                    if (saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_JUDGE.getValue())
                            || saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
                        // 4.3.2.11 后台服务向判图站推送待判图图像信息
                        sysJudgeController.sendJudgeImageInfo(saveScanResult.getTaskNumber());
                    }

                    // 安检仪+手检端 和 安检仪+审图端+手检端
                    if (saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_MANUAL.getValue())
                            || saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
                        if (devSerImageInfoModel.getAtrResult().equals(DeviceDefaultType.TRUE.getValue())) {          // 报警、有嫌疑
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
                            if (sysDeviceConfig.getAtrSwitch().equals(DefaultType.TRUE.getValue())) {       // 无嫌疑配置
                                // 放行
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
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL);
                }
                resultMessageVO.setKey(routingKey);
                resultMessageVO.setContent(result);
                String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
                messageSender.sendSysDeviceSecurityInfoSaveReplyMessage(encryptMsg);

                // 安检仪+手检端
                if (saveScanResult.getIsSucceed()) {
                    if (saveScanResult.getWorkModeName().equals(WorkModeType.SECURITY_MANUAL.getValue())) {
                        String dispatchManualDeviceInfoStr = "";
                        int i = 0;
                        while (StringUtils.isBlank(dispatchManualDeviceInfoStr)) {      // 等 手检站分派
                            i++;
                            // 分派手检任务
                            dispatchManualDeviceInfoStr = redisUtil.get(BackgroundServiceUtil
                                    .getConfig("redisKey.sys.manual.assign.task.info") + saveScanResult.getTaskNumber());
                            if (StringUtils.isBlank(dispatchManualDeviceInfoStr)) {     // 等 手检站分派
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    log.error(e.getMessage());
                                }
                            }
                            if (i == 10) {
                                break;
                            }
                        }
                        try {
                            DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO = objectMapper.readValue(dispatchManualDeviceInfoStr,
                                    DispatchManualDeviceInfoVO.class);
                            if (dispatchManualDeviceInfoVO.getLocalRecheck()) {             // 分派手检端 安检仪
                                // 4.3.1.15 后台服务向安检仪推送判图结论
                                sysSecurityController.sendJudgeResultToSecurityDevice(saveScanResult.getTaskNumber(), dispatchManualDeviceInfoVO.getGuid());
                            } else {                                                        // 分派手检端 手检端
                                // 4.3.3.12 后台服务向手检站推送业务数据
                                sysManualController.sendJudgeResultToHandDevice(saveScanResult.getTaskNumber());
                            }
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
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
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        CommonResultVO result = new CommonResultVO();
        String guid = devSerDataSyncModel.getGuid();
        result.setGuid(guid);
        // 判断 GUID有效
        boolean isAvailable = sysDeviceService.checkGuid(guid);
        // 接收数据以后保存
        boolean isSucceed = securityImageInfoService.synchronizeScanResult(devSerDataSyncModel);
        if (isAvailable) {      // GUID有效
            if (devSerDataSyncModel.getInvalidScan().equals(DeviceDefaultType.FALSE.getValue())) {
                if (isSucceed) {
                    result.setResult(CommonConstant.RESULT_SUCCESS);
                } else {
                    result.setResult(CommonConstant.RESULT_FAIL);
                }
            } else {
                result.setResult(CommonConstant.RESULT_FAIL);
            }
        } else {
            result.setResult(CommonConstant.RESULT_FAIL);
        }
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.sys.synchronization");

        resultMessageVO.setKey(routingKey);
        resultMessageVO.setContent(result);
        String encryptMsg = CryptUtil.encrypt(JSONObject.toJSONString(resultMessageVO));
        messageSender.sendSysDeviceSecurityInfoSynchronizeReplyMessage(encryptMsg);
        return resultMessageVO;
    }

}
