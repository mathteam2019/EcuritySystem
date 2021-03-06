package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.*;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.*;
import com.nuctech.securitycheck.backgroundservice.common.vo.JudgeInfoSaveResultVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.SysJudgeInfoVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.SysMonitoringDeviceStatusInfoVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.SysSecurityInfoVO;
import com.nuctech.securitycheck.backgroundservice.controller.JudgeSysController;
import com.nuctech.securitycheck.backgroundservice.controller.SysJudgeController;
import com.nuctech.securitycheck.backgroundservice.repositories.*;
import com.nuctech.securitycheck.backgroundservice.service.ISerJudgeGraphService;
import com.nuctech.securitycheck.backgroundservice.service.ISerPlatformCheckParamsService;
import com.nuctech.securitycheck.backgroundservice.service.ISysDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * SerJudgeGraphServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
@Slf4j
public class SerJudgeGraphServiceImpl implements ISerJudgeGraphService {

    @Autowired
    private SysDeviceRepository sysDeviceRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysDeviceConfigRepository sysDeviceConfigRepository;

    @Autowired
    private SysWorkflowRepository sysWorkflowRepository;

    @Autowired
    private SerTaskRepository serTaskRepository;

    @Autowired
    private SerJudgeGraphRepository serJudgeGraphRepository;

    @Autowired
    private SerAssignRepository serAssignRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private SerInspectedRepository serInspectedRepository;

    @Autowired
    private ISysDeviceService sysDeviceService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISerPlatformCheckParamsService serPlatformCheckParamsService;

    @Autowired
    private SerCheckResultRepository serCheckResultRepository;



    /**
     * 4.3.2.9 判图站向后台服务提交判图结论
     *
     * @param judgeSerResult
     * @return saveResult
     */
    @Override
    public JudgeInfoSaveResultVO saveJudgeGraphResult(JudgeSerResultModel judgeSerResult) {
        JudgeInfoSaveResultVO result = new JudgeInfoSaveResultVO();
        String securityGuid = "";
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            SysDevice judgeDevice = null;
            if (judgeSerResult.getGuid() != null) {         // 分派超时 guid=null
                // get 分派判图站设备
                String guid = judgeSerResult.getGuid();
                SysDevice deviceModel = new SysDevice();
                deviceModel.setGuid(guid);
                Example<SysDevice> serDeviceEx = Example.of(deviceModel);
                judgeDevice = sysDeviceRepository.findOne(serDeviceEx);
            }
            // 判图站用户(超时的时候 默认用户)
            SysUser sysUserModel = new SysUser();
            sysUserModel.setUserAccount(judgeSerResult.getImageResult().getUserName());
            Example<SysUser> sysUserEx = Example.of(sysUserModel);
            SysUser sysUser = sysUserRepository.findOne(sysUserEx);

            // 当前任务和安检仪设备
            String taskNumber = judgeSerResult.getImageResult().getImageGuid();
            SerTask taskModel = new SerTask();
            taskModel.setTaskNumber(taskNumber);
            Example<SerTask> serTaskEx = Example.of(taskModel);
            SerTask serTask = serTaskRepository.findOne(serTaskEx);
            SysDevice securityDevice = serTask.getDevice();
            securityGuid = securityDevice.getGuid();

            String settingModelStr = serTask.getModeConfig();
            SerDeviceConfigSettingModel serDeviceConfigSettingModel = objectMapper.readValue(settingModelStr, SerDeviceConfigSettingModel.class);
            SysDeviceConfig sysDeviceConfig = serDeviceConfigSettingModel.getDeviceConfig();
            // 当前安检仪的工作方式
            SysWorkMode sysWorkMode = serTask.getSysWorkMode();
            result.setWorkModeName(sysWorkMode.getModeName());
            // 当前工作流
            SysWorkflow sysWorkflowModel = new SysWorkflow();
            sysWorkflowModel.setSysWorkMode(sysWorkMode);

            sysWorkflowModel.setTaskType(TaskType.JUDGE.getValue());
            Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
            SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);

            // 获取分配数据
            SerAssign serAssignModel = SerAssign.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .build();
            Example<SerAssign> serAssignEx = Example.of(serAssignModel);
            SerAssign serAssign = serAssignRepository.findOne(serAssignEx);



            //检查手结果存在于此

            // 判断回收任务
            SerJudgeGraph serJudgeGraphModel = SerJudgeGraph.builder()
                    .serTask(serTask)
                    .build();
            Example<SerJudgeGraph> serJudgeGraphEx = Example.of(serJudgeGraphModel);
            SerJudgeGraph serJudgeGraphOld = serJudgeGraphRepository.findOne(serJudgeGraphEx);
            if (serJudgeGraphOld == null) {         // 判断回收任务(没有数据)
                // 保存判图站结果
                String judgeSubmitRectsStr = objectMapper.writeValueAsString(judgeSerResult.getImageResult().getSubmitRects());
                SerJudgeGraph serJudgeGraph = SerJudgeGraph.builder()
                        .serTask(serTask)
                        .sysWorkflow(sysWorkflow)
                        .judgeDevice(judgeDevice)
                        .judgeResult(judgeSerResult.getImageResult().getResult())
                        .judgeStartTime(DateUtil.stringDateToDate(judgeSerResult.getImageResult().getTime()))
                        .judgeUser(sysUser)
                        .judgeEndTime(DateUtil.getCurrentDate())
                        .judgeSubmitRects(judgeSubmitRectsStr)
                        .judgeCartoonRects(objectMapper.writeValueAsString(judgeSerResult.getImageResult().getSubmitCartoonRects()))
                        .build();
                if(sysUser != null) {
                    serJudgeGraph.setCreatedBy(sysUser.getUserId());
                }
                if(serAssign != null) {
                    serJudgeGraph.setJudgeStartTime(serAssign.getAssignEndTime());
                }
                boolean isSystem = true;
                if (judgeSerResult.getImageResult().getUserName().equals(BackgroundServiceUtil.getConfig("default.user"))) {    // 是不是超时
                    serJudgeGraph.setJudgeTimeout(TimeDefaultType.TRUE.getValue());
                } else {
                    serJudgeGraph.setJudgeTimeout(TimeDefaultType.FALSE.getValue());
                    isSystem = false;
                }


                serJudgeGraph = serJudgeGraphRepository.save(serJudgeGraph);
                // 更新目前判图站的工作状态 - free 数据库里面
                if (judgeSerResult.getGuid() != null) {
                    judgeDevice.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
                    sysDeviceRepository.save(judgeDevice);
                    redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.device.current.status") + judgeDevice.getGuid());

                }
                String workMode = "";
                // 更新目前判图站的工作状态 - free redis 里面
                if (judgeSerResult.getGuid() != null) {
                    List<SysJudgeInfoVO> judgeDeviceList = new ArrayList<SysJudgeInfoVO>();
                    String sysJudgeInfoStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.judge.info"));
                    JSONArray sysJudgeInfoListStr = JSONArray.parseArray(sysJudgeInfoStr);
                    judgeDeviceList = sysJudgeInfoListStr.toJavaList(SysJudgeInfoVO.class);

                    // 将对应的判断器状态设置为空闲
                    workMode = sysDeviceConfig.getSysWorkMode().getModeName();
                    for (SysJudgeInfoVO judgeItem : judgeDeviceList) {
                        if (StringUtils.isNotBlank(judgeItem.getDeviceId().toString()) && judgeItem.getDeviceId().equals(judgeDevice.getDeviceId())) {
                            judgeItem.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
                            break;
                        }
                    }

                    //将安全服务信息更新为Redis
                    redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.judge.info"),
                            CryptUtil.encrypt(objectMapper.writeValueAsString(judgeDeviceList)), CommonConstant.EXPIRE_TIME.getValue());


                    // 将设备状态信息更新为Redis

                }



                // 保存历史 ser_history
                History historyModel = History.builder()
                        .serTask(serTask)
                        .build();
                Example<History> historyEx = Example.of(historyModel);
                History history = historyRepository.findOne(historyEx);

                if(serAssign != null) {
                    history.setAssignJudgeDevice(serAssign.getAssignJudgeDevice());
                    history.setAssignWorkflowJudge(serAssign.getSysWorkflow());
                    history.setAssignUserJudge(serAssign.getAssignUser());
                    history.setAssignJudgeUserName(serAssign.getAssignUser().getUserName());
                    history.setAssignJudgeStartTime(serAssign.getAssignStartTime());
                    history.setAssignJudgeEndTime(serAssign.getAssignEndTime());
                    history.setAssignJudgeTimeout(serAssign.getAssignTimeout());
                    history.setJudgeAssignTimeout(serAssign.getAssignTimeout());
                }

                // todo set assign judge status
                history.setAssignJudgeStatus("");
                history.setJudgeDevice(judgeDevice);
                history.setJudgeWorkflow(sysWorkflow);
                history.setJudgeGraph(serJudgeGraph);
                history.setJudgeResult(serJudgeGraph.getJudgeResult());
                history.setJudgeTimeout(serJudgeGraph.getJudgeTimeout());
                history.setJudgeStartTime(serJudgeGraph.getJudgeStartTime());
                history.setJudgeEndTime(serJudgeGraph.getJudgeEndTime());
                history.setJudgeUser(serJudgeGraph.getJudgeUser());
                history.setTaskStatus(serTask.getTaskStatus());

                // todo set judge status
                history.setJudgeStatus("");


                // get the scan data from redis and add judge result to it
                String key = "dev.service.image.info" + serTask.getTaskNumber();
                String devSerImageInfoStr = redisUtil.get(key);
                devSerImageInfoStr = CryptUtil.decrypt(devSerImageInfoStr);
                DevSerImageInfoModel devSerImageInfo = objectMapper.readValue(devSerImageInfoStr, DevSerImageInfoModel.class);
                boolean isFinish = false;
                if(!(WorkModeType.SECURITY_MANUAL.getValue().equals(sysWorkMode.getModeName())) && !(WorkModeType.SECURITY_JUDGE_MANUAL.getValue().equals(sysWorkMode.getModeName()))) {
                    isFinish = true;
                } else {
                    if (DeviceDefaultType.FALSE.getValue().equals(devSerImageInfo.getImageData().getAtrResult())) {          // 报警、有嫌疑
                        if (DeviceDefaultType.TRUE.getValue().equals(sysDeviceConfig.getAtrSwitch())) {       // 无嫌疑配置
                            isFinish = true;
                        }
                    }
                }



                if(isFinish == true){
                    //将数据保存到ser_check_result
                    SerCheckResult serCheckResultModel = SerCheckResult.builder()
                            .serTask(serTask)
                            .build();
                    Example<SerCheckResult> serCheckResultEx = Example.of(serCheckResultModel);
                    SerCheckResult serCheckResult = serCheckResultRepository.findOne(serCheckResultEx);
                    if(serCheckResult == null) {
                        serCheckResult = SerCheckResult.builder()
                                .serTask(serTask)
                                .sysDevice(securityDevice)
                                .build();

                    }
                    if(isSystem == true) {
                        serCheckResult.setConclusionType(ConclusionType.SYSTEM.getValue());
                    } else {
                        serCheckResult.setConclusionType(ConclusionType.MANUAL.getValue());
                    }
                    serCheckResult.setJudgeResult(serJudgeGraph.getJudgeResult());
                    serCheckResultRepository.save(serCheckResult);
                    serTask.setTaskStatus(TaskStatusType.ALL.getValue());
                    serTaskRepository.save(serTask);
                } else {
                    serTask.setTaskStatus(TaskStatusType.HAND.getValue());
                    serTaskRepository.save(serTask);
                }
                history.setTaskStatus(serTask.getTaskStatus());
                historyRepository.save(history);




                SerManImageInfoModel serManImageInfo = SerManImageInfoModel.builder()
                        .imageData(devSerImageInfo.getImageData())
                        .imageResult(judgeSerResult.getImageResult())
                        .build();

                //将判断结果更新为Redis
                String manImageInfoKey = "dev.service.image.result.info" + serTask.getTaskNumber();
                String serManImageInfoStr = objectMapper.writeValueAsString(serManImageInfo);
                serManImageInfoStr = CryptUtil.encrypt(serManImageInfoStr);
                redisUtil.set(manImageInfoKey, serManImageInfoStr, CommonConstant.EXPIRE_TIME.getValue());

                String serDevJudgeResultkey = "ser.security.judge.result" + serTask.getTaskNumber();
                SerDevJudgeGraphResultModel serDevJudgeGraphResult = SerDevJudgeGraphResultModel.builder()
                        .imageResult(judgeSerResult.getImageResult())
                        .build();
                String serDevJudgeGraphResultStr = objectMapper.writeValueAsString(serDevJudgeGraphResult);
                serDevJudgeGraphResultStr = CryptUtil.encrypt(serDevJudgeGraphResultStr);
                redisUtil.set(serDevJudgeResultkey, serDevJudgeGraphResultStr, CommonConstant.EXPIRE_TIME.getValue());
                result.setIsSucceed(true);
            } else {
                result.setIsSucceed(false);
                log.error("判断图结果已发送");
            }
            result.setGuid(securityGuid);
            result.setTaskNumber(serTask.getTaskNumber());
            return result;
        } catch (Exception e) {
            log.error("无法保存判断结果");
            log.error(e.getMessage());
            result.setIsSucceed(false);
            return result;
        }
    }



    /**
     * 4.3.2.11 后台服务向判图站推送待判图图像信息
     *
     * @param taskNumber
     * @return serJudgeImageInfo
     */
    @Override
    public SerJudgeImageInfoModel sendJudgeImageInfo(String taskNumber) {
        try {
            boolean isTimeout = false; // 是否超时
            ObjectMapper objectMapper = new ObjectMapper();

            // 目前任务
            SerTask taskModel = new SerTask();
            taskModel.setTaskNumber(taskNumber);
            Example<SerTask> serTaskEx = Example.of(taskModel);
            SerTask serTask = serTaskRepository.findOne(serTaskEx);


            // 对应的安检仪
            SysDevice securityDevice = serTask.getDevice();

            // 运行配置

            SysWorkMode sysWorkMode = serTask.getSysWorkMode();
            String settingModelStr = serTask.getModeConfig();
            SerDeviceConfigSettingModel serDeviceConfigSettingModel = objectMapper.readValue(settingModelStr, SerDeviceConfigSettingModel.class);
            SysDeviceConfig sysDeviceConfig = serDeviceConfigSettingModel.getDeviceConfig();
            // 工作流程
            SysWorkflow sysWorkflowModel = new SysWorkflow();
            sysWorkflowModel.setSysWorkMode(sysWorkMode);
            sysWorkflowModel.setTaskType(TaskType.JUDGE.getValue());
            Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
            SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);

            // 插入到ser_assign
            SerAssign serAssign = SerAssign.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .sysWorkMode(sysWorkMode)
                    .assignStartTime(DateUtil.getCurrentDate())
                    .build();

            String key = "dev.service.image.info" + serTask.getTaskNumber();
            String devSerImageInfoStr = redisUtil.get(key);
            devSerImageInfoStr = CryptUtil.decrypt(devSerImageInfoStr);
            DevSerImageInfoModel devSerImageInfo = objectMapper.readValue(devSerImageInfoStr, DevSerImageInfoModel.class);

            // 性别匹配逻辑
            boolean manAvailable = false;
            boolean womanAvailable = false;
            if (DeviceGenderType.MALE.getValue().equals(devSerImageInfo.getImageData().getImageGender())) {
                if (GenderType.MALE.getValue().equals(sysDeviceConfig.getWomanRemoteGender())
                        || GenderType.MALE_AND_FEMALE.getValue().equals(sysDeviceConfig.getWomanRemoteGender())) {
                    womanAvailable = true;
                }
                if (GenderType.MALE.getValue().equals(sysDeviceConfig.getManRemoteGender())
                        || GenderType.MALE_AND_FEMALE.getValue().equals(sysDeviceConfig.getManRemoteGender())) {
                    manAvailable = true;
                }
            } else {
                if (GenderType.FEMALE.getValue().equals(sysDeviceConfig.getWomanRemoteGender())
                        || GenderType.MALE_AND_FEMALE.getValue().equals(sysDeviceConfig.getWomanRemoteGender())) {
                    womanAvailable = true;
                }
                if (GenderType.FEMALE.getValue().equals(sysDeviceConfig.getManRemoteGender())
                        || GenderType.MALE_AND_FEMALE.getValue().equals(sysDeviceConfig.getManRemoteGender())) {
                    manAvailable = true;
                }
            }
            // 找到合适的判断器(find proper judge device)
            boolean isAssigned = false;
            // 设备状态信息 从 redis
            SysJudgeInfoVO assignedJudgeInfo = null;
            long start = System.currentTimeMillis();
            long end = System.currentTimeMillis();
            List<SysJudgeInfoVO> judgeDeviceList = new ArrayList<>();
            boolean isProcess = false;
            Long judgeProcessCount = 0L;
            String judgeProcessCountStr = "";

            String identityKey = "";

            if(securityDevice.getFieldId() != null) {
                identityKey = securityDevice.getFieldId().toString();
            } else {
                identityKey = securityDevice.getGuid();
            }
            SerPlatformCheckParams checkParamsTest = serPlatformCheckParamsService.getLastCheckParams();

            while (true) {               // 还没分派

                String checkParamsStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.setting.platform.check"));

//                SerPlatformCheckParams checkParamsTest = new SerPlatformCheckParams();
//                checkParamsTest.setJudgeAssignTime(Long.valueOf(CommonConstant.DEFAULT_JUDGE_ASSIGN_TIME.getValue()));
//                checkParamsTest.setJudgeProcessingTime(Long.valueOf(CommonConstant.DEFAULT_JUDGE_PROCESSING_TIME.getValue()));
                try {
                    checkParamsTest = objectMapper.readValue(checkParamsStr, SerPlatformCheckParams.class);
                } catch (Exception e) {
                    log.error("转换模型时发生错误");
                    log.error(e.getMessage());
                }
                // 检入设备的处理队列 ( 而不是使用redis的信号量属性)
                if(isProcess == false) {

                    // 获取对应队列中的进程数
                    try {
                        judgeProcessCountStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.judge.process.info") + securityDevice.getGuid());
                        judgeProcessCount = Long.valueOf(judgeProcessCountStr);
                    }catch (Exception ex) {
                        log.error("无法解析处理计数");
                        log.error(ex.getMessage());
                    }
                    if(judgeProcessCount >= CommonConstant.MAX_PROCESS.getValue()) { // 检查可以添加到队列
//                        Thread.sleep(1000);
                        continue;
                    } else {// 添加到队列
                        isProcess = true;
                        judgeProcessCount ++;
                        redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.judge.process.info") + securityDevice.getGuid(), judgeProcessCount.toString());
                        start = System.currentTimeMillis();
                    }
                }

                // 从设备配置中获取当前设备的判断设备列表（使用redis）
                String sysJudgeInfoStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.judge.info"));
                JSONArray sysJudgeInfoListStr = JSONArray.parseArray(sysJudgeInfoStr);
                if(sysJudgeInfoListStr == null) {
                    continue;
                }
                judgeDeviceList = sysJudgeInfoListStr.toJavaList(SysJudgeInfoVO.class);


                //随机洗牌
                Collections.shuffle(judgeDeviceList);

                //identityKey = taskNumber;
                // 检查已经为当前安检仪设备运行的进程
                for (SysJudgeInfoVO item : judgeDeviceList) {
                    boolean isExist = false;
                    for(Long judgeDeviceId: serDeviceConfigSettingModel.getJudgeDeviceIdList()) {
                        if(item.getDeviceId().equals(judgeDeviceId)) {
                            isExist = true;
                        }
                    }
                    if(isExist == false) {
                        continue;
                    }

                    //检查可以判断使用状态和性别
                    if (DeviceStatusType.LOGIN.getValue().equals(item.getCurrentStatus())
                            || DeviceStatusType.START.getValue().equals(item.getCurrentStatus())) {

                        if (DeviceWorkStatusType.FREE.getValue().equals(item.getWorkStatus())) {            // 空闲

                            if (GenderType.MALE.getValue().equals(item.getDeviceCheckerGender())) {
                                if (manAvailable) {
                                    boolean lockResult = redisUtil.aquirePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.device.current.status") + item.getGuid(),
                                            taskNumber, 0);

                                    if(!lockResult) {
                                        log.error("任务" + taskNumber + "无法检查判图设备");
                                        //Thread.sleep(100);
                                        continue;
                                    }

                                    isAssigned = true;
                                    assignedJudgeInfo = item;
                                    item.setWorkStatus(DeviceWorkStatusType.BUSY.getValue());
                                    isTimeout = false;                  // 分派成功(超时 false)
                                    break;
                                }
                            } else {
                                if (womanAvailable) {
                                    isAssigned = true;
                                    assignedJudgeInfo = item;
                                    item.setWorkStatus(DeviceWorkStatusType.BUSY.getValue());
                                    isTimeout = false;                  // 分派成功(超时 false)
                                    break;
                                }
                            }
                        }
                    }
                }


                // 检查超时
                if (!isAssigned) {          // 还没分派 等等 空闲

//                    Thread.sleep(1000);
                    end = System.currentTimeMillis();
                    Long judgeAssignTime = checkParamsTest.getJudgeAssignTime();
                    if (judgeAssignTime != null && (end - start) >= judgeAssignTime * 1000) {         // 超时 true
                        isTimeout = true;
                        break;
                    }
                } else {
                    break;
                }
            }

            //从队列弹出
            try {
                judgeProcessCountStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.judge.process.info") + securityDevice.getGuid());
                judgeProcessCount = Long.valueOf(judgeProcessCountStr);
            } catch (Exception ex) {
                log.error("无法解析处理计数");
                log.error(ex.getMessage());
            }
            judgeProcessCount --;
            if(judgeProcessCount < 0L) {
                judgeProcessCount = 0L;
            }
            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.judge.process.info") + securityDevice.getGuid(), judgeProcessCount.toString());

            if (isTimeout) {    // 分派判图任务 超时
                log.error("分派判图任务 超时");
                // 默认用户
                SysUser sysUserModel = SysUser.builder()
                        .userAccount(BackgroundServiceUtil.getConfig("default.user"))
                        .build();
                Example<SysUser> sysUserEx = Example.of(sysUserModel);
                SysUser assignUser = sysUserRepository.findOne(sysUserEx);

                //添加到ser_assign
                SysDevice assignedDevice = null;//new SysDevice();
                if(WorkModeType.SECURITY_JUDGE_MANUAL.getValue().equals(sysWorkMode.getModeName())) {
                    serTask.setTaskStatus(TaskStatusType.HAND.getValue());
                } else {
                    serTask.setTaskStatus(TaskStatusType.ALL.getValue());
                }

                serTaskRepository.save(serTask);

                serAssign.setAssignUser(assignUser);
                serAssign.setAssignJudgeDevice(assignedDevice);
                serAssign.setAssignEndTime(DateUtil.getCurrentDate());
                serAssign.setAssignTimeout(TimeDefaultType.TRUE.getValue());

                serAssignRepository.save(serAssign);

                // 从redis获取扫描数据


                JudgeSerResultModel judgeSerResultModel = new JudgeSerResultModel();
                judgeSerResultModel.setImageResult(new ImageResultModel());
                judgeSerResultModel.getImageResult().setTime(DateUtil.getDateTmeAsString(DateUtil.getCurrentDate()));
                judgeSerResultModel.getImageResult().setImageGuid(serTask.getTaskNumber());
                judgeSerResultModel.getImageResult().setResult(devSerImageInfo.getImageData().getAtrResult());
                judgeSerResultModel.getImageResult().setTimeout(true);
                judgeSerResultModel.getImageResult().setUserName(BackgroundServiceUtil.getConfig("default.user"));      // 默认用户

                // 4.3.2.9 判图站向后台服务提交判图结论(超时结论)
                JudgeInfoSaveResultVO saveResult = saveJudgeGraphResult(judgeSerResultModel);
                SysJudgeController sysJudgeController = SpringContextHolder.getBean(SysJudgeController.class);
                sysJudgeController.sendJudgeResultToDevice(judgeSerResultModel, saveResult);
                //JudgeSysController judgeSysController = SpringContextHolder.getBean(JudgeSysController.class);
                //judgeSysController.saveJudgeGraphResult(judgeSerResultModel);


                SerJudgeImageInfoModel serJudgeImageInfo = SerJudgeImageInfoModel.builder()
                        .imageData(devSerImageInfo.getImageData())
                        .personData(devSerImageInfo.getPersonData())
                        .timeout(checkParamsTest.getJudgeProcessingTime().intValue())
                        .build();
                return serJudgeImageInfo;

            } else {            // 分派判图任务 正常
                // 分派用户
                log.info("分派判图任务 正常");
                SysUser sysUserModel = SysUser.builder()
                        .userId(assignedJudgeInfo.getLogInedUserId())
                        .build();
                Example<SysUser> sysUserEx = Example.of(sysUserModel);
                SysUser assignUser = sysUserRepository.findOne(sysUserEx);

                SysDevice sysDeviceModel = SysDevice.builder()
                        .deviceSerial(assignedJudgeInfo.getDeviceSerial())
                        .build();
                Example<SysDevice> sysDeviceEx = Example.of(sysDeviceModel);
                SysDevice assignedDevice = sysDeviceRepository.findOne(sysDeviceEx);

                serTask.setTaskStatus(TaskStatusType.JUDGE.getValue());
                serTaskRepository.save(serTask);


                // 更新任务的分派
                serAssign.setAssignUser(assignUser);
                serAssign.setAssignJudgeDevice(assignedDevice);
                serAssign.setAssignEndTime(DateUtil.getCurrentDate());
                serAssign.setAssignTimeout(TimeDefaultType.FALSE.getValue());

                serAssignRepository.save(serAssign);

                // 更新设备的工作状态 数据库
                assignedDevice.setWorkStatus(DeviceWorkStatusType.BUSY.getValue());
                sysDeviceRepository.save(assignedDevice);

                // 更新设备的工作状态 Redis


                redisUtil.set(BackgroundServiceUtil
                                .getConfig("redisKey.sys.judge.info"),
                        CryptUtil.encrypt(objectMapper.writeValueAsString(judgeDeviceList)), CommonConstant.EXPIRE_TIME.getValue());



                // 从redis获取扫描数据


                SerJudgeImageInfoModel serJudgeImageInfo = SerJudgeImageInfoModel.builder()
                        .guid(assignedDevice.getGuid())
                        .imageData(devSerImageInfo.getImageData())
                        .personData(devSerImageInfo.getPersonData())
                        .timeout(checkParamsTest.getJudgeProcessingTime().intValue())
                        .build();
                return serJudgeImageInfo;
            }
        } catch (Exception e) {
            log.error("无法发送判断图像结果");
            log.error(e.getMessage());
            e.printStackTrace();
            return SerJudgeImageInfoModel.builder().build();
        }
    }
}
