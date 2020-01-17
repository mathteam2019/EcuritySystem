package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.*;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.*;
import com.nuctech.securitycheck.backgroundservice.common.vo.DispatchManualDeviceInfoVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.ScanInfoSaveResultVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.TaskInfoVO;
import com.nuctech.securitycheck.backgroundservice.controller.JudgeSysController;
import com.nuctech.securitycheck.backgroundservice.repositories.*;
import com.nuctech.securitycheck.backgroundservice.service.ISecurityImageInfoService;
import com.nuctech.securitycheck.backgroundservice.service.ISerJudgeGraphService;
import com.nuctech.securitycheck.backgroundservice.service.ISysDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * SecurityImageInfoServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Slf4j
@Service
@Transactional
public class SecurityImageInfoServiceImpl implements ISecurityImageInfoService {

    @Autowired
    private SerTaskRepository serTaskRepository;

    @Autowired
    private SysDeviceRepository sysDeviceRepository;

    @Autowired
    private SysDeviceConfigRepository sysDeviceConfigRepository;

    @Autowired
    private SysWorkflowRepository sysWorkflowRepository;

    @Autowired
    private SerTaskStepRepository serTaskStepRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SerScanRepository serScanRepository;

    @Autowired
    private SerImageRepository serImageRepository;

    @Autowired
    private SerImagePropertyRepository serImagePropertyRepository;

    @Autowired
    private SerInspectedRepository serInspectedRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISysDeviceService sysDeviceService;

    @Autowired
    private SerLoginInfoRepository serLoginInfoRepository;

    @Autowired
    private SerAssignRepository serAssignRepository;

    @Autowired
    private ISerJudgeGraphService serJudgeGraphService;

    /**
     * 4.3.1.13 发送无效的结果
     *
     * @param scanInfoSaveResultVO
     *
     */
    @Override
    public void sendInvalidResult(ScanInfoSaveResultVO scanInfoSaveResultVO) {

        try {
            String taskNumber = scanInfoSaveResultVO.getTaskNumber();


            JudgeSerResultModel judgeSerResultModel = new JudgeSerResultModel();
            judgeSerResultModel.setImageResult(new ImageResultModel());
            judgeSerResultModel.getImageResult().setImageGuid(taskNumber);
            judgeSerResultModel.getImageResult().setTime(DateUtil.getDateTmeAsString(DateUtil.getCurrentDate()));
            judgeSerResultModel.getImageResult().setResult(DeviceDefaultType.FALSE.getValue());
            judgeSerResultModel.getImageResult().setUserName(BackgroundServiceUtil.getConfig("default.user"));      // 默认用户

            // 4.3.2.9 判图站向后台服务提交判图结论(超时结论)
            serJudgeGraphService.saveJudgeGraphResult(judgeSerResultModel);
            //JudgeSysController judgeSysController = SpringContextHolder.getBean(JudgeSysController.class);
            //judgeSysController.saveJudgeGraphResult(judgeSerResultModel);
        } catch (Exception ex) {
            log.error("无法获取图像信息");
            log.error(ex.getMessage());
        }

    }

    /**
     * 4.3.1.13 安检仪向后台服务发送扫描图像信息
     *
     * @param devSerImageInfo
     * @return saveScanResult
     */
    @Override
    public ScanInfoSaveResultVO saveScanResult(DevSerImageInfoModel devSerImageInfo) {
        ScanInfoSaveResultVO scanInfoSaveResultVO = new ScanInfoSaveResultVO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 从数据库获取设备(get device data from database)
            String guid = devSerImageInfo.getGuid();
            SysDevice deviceModel = new SysDevice();
            deviceModel.setGuid(guid);
            Example<SysDevice> serDeviceEx = Example.of(deviceModel);
            SysDevice device = sysDeviceRepository.findOne(serDeviceEx);
            // 获取当前设备的工作模式
            SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findLatestConfig(device.getDeviceId());
            SysWorkMode sysWorkMode = sysDeviceConfig.getSysWorkMode();
            scanInfoSaveResultVO.setModeId(sysWorkMode.getModeId().intValue());
            // 获取设备当前的工作流程
            SysWorkflow sysWorkflowModel = new SysWorkflow();
            sysWorkflowModel.setSysWorkMode(sysWorkMode);
            sysWorkflowModel.setTaskType(TaskType.SECURITY.getValue());
            Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
            SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);
            // 通过Image GUID获取任务，如果没有，则创建
            boolean isNewTask = true;
            String taskNumber = devSerImageInfo.getImageData().getImageGuid();
            SerTask serTask = null;
            if (StringUtils.isNotBlank(taskNumber)) {
                SerTask taskModel = new SerTask();
                taskModel.setTaskNumber(taskNumber);
                Example<SerTask> serTaskEx = Example.of(taskModel);
                serTask = serTaskRepository.findOne(serTaskEx);
                if (serTask != null) {
                    isNewTask = false;
                }
            }



            if (isNewTask) {
                // if not exist
                // save ser_task
                serTask = saveSerTask(guid, device, sysWorkMode, sysWorkflow, sysDeviceConfig, taskNumber);
            } else {
                log.error("图片GUID已存在");
                scanInfoSaveResultVO.setIsSucceed(false);
                return scanInfoSaveResultVO;
            }

            // 从具有登录名的数据库中获取用户信息
            SysUser sysUserModel = new SysUser();
            sysUserModel.setUserAccount(devSerImageInfo.getImageData().getLoginName());
            Example<SysUser> sysUserEx = Example.of(sysUserModel);
            SysUser sysUser = sysUserRepository.findOne(sysUserEx);

            devSerImageInfo.getImageData().setImageGuid(serTask.getTaskNumber());

            // 将扫描图像信息保存到数据库
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<DeviceImageModel> deviceImageModels = devSerImageInfo.getImageData().getDeviceImages();
            String deviceImageJsonStr = objectMapper.writeValueAsString(deviceImageModels);
            SerScan serScan = SerScan.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .sysDevice(device)
                    .scanDeviceImages(deviceImageJsonStr)
                    .scanATRResult(BackgroundServiceUtil.dictConvert(devSerImageInfo.getImageData().getAtrResult()))
                    .scanInvalid(devSerImageInfo.getImageData().getInvalidScan())
                    .scanFootAlarm(BackgroundServiceUtil.dictConvert(devSerImageInfo.getImageData().getFootAlarmed()))
                    .scanStartTime(sdf.parse(devSerImageInfo.getImageData().getScanBeginTime()))
                    .scanEndTime(sdf.parse(devSerImageInfo.getImageData().getScanEndTime()))
                    .scanPointsman(sysUser)
                    .scanAssignTimeout(TimeDefaultType.FALSE.getValue())
                    .scanOffline(Integer.valueOf(devSerImageInfo.getImageData().getOffline()))
                    .scanImageGender(BackgroundServiceUtil.genderConvert(devSerImageInfo.getImageData().getImageGender()))
                    .scanData(devSerImageInfo.getImageData().getData())
                    .scanRandomAlarm(devSerImageInfo.getImageData().getRandomAlarm())
                    .scanImageId(taskNumber)
                    .scanKeyPoint(objectMapper.writeValueAsString(devSerImageInfo.getImageData().getKeyPoint()))
                    .build();
            serScan = serScanRepository.save(serScan);

            // 将图像信息保存到ser_image数据库
            for (DeviceImageModel deviceImageModel : devSerImageInfo.getImageData().getDeviceImages()) {
                // scan image save
                SerImage serImage = SerImage.builder()
                        .serTask(serTask)
                        .serScan(serScan)
                        .imageFormat(ImageFormatType.IMAGE.getValue())
                        .imageUrl(deviceImageModel.getImage())
                        .build();
                serImage = serImageRepository.save(serImage);
                for (ImageRectModel imageRectModel : deviceImageModel.getImageRects()) {
                    saveImageProperty(serImage, "x", imageRectModel.getX().toString());
                    saveImageProperty(serImage, "y", imageRectModel.getY().toString());
                    saveImageProperty(serImage, "width", imageRectModel.getWidth().toString());
                    saveImageProperty(serImage, "height", imageRectModel.getHeight().toString());
                }
                // scan cartoon image save
                SerImage serCartoon = SerImage.builder()
                        .serTask(serTask)
                        .serScan(serScan)
                        .imageFormat(ImageFormatType.CARTOON.getValue())
                        .imageUrl(deviceImageModel.getCartoon())
                        .build();
                serCartoon = serImageRepository.save(serCartoon);
                for (ImageRectModel imageRectModel : deviceImageModel.getCartoonRects()) {
                    saveImageProperty(serCartoon, "x", imageRectModel.getX().toString());
                    saveImageProperty(serCartoon, "y", imageRectModel.getY().toString());
                    saveImageProperty(serCartoon, "width", imageRectModel.getWidth().toString());
                    saveImageProperty(serCartoon, "height", imageRectModel.getHeight().toString());
                }
            }

            // 保存检查人员数据
            SerInspected serInspected = saveInspected(serTask, devSerImageInfo);

            // 将扫描信息保存到历史记录
            History history = History.builder()
                    .serTask(serTask)
                    .sysWorkMode(sysWorkMode)
                    .serScan(serScan)
                    .sysWorkflow(sysWorkflow)
                    .scanDevice(device)
                    .scanATRResult(serScan.getScanATRResult())
                    .scanFootAlarm(serScan.getScanFootAlarm())
                    .scanStartTime(serScan.getScanStartTime())
                    .scanEndTime(serScan.getScanEndTime())
                    .scanPointsman(serScan.getScanPointsman())
                    .scanPointsmanName(serScan.getScanPointsman().getUserName())
                    .build();
            historyRepository.save(history);



            // 将图像数据插入Redis(insert data to redis)
            String key = "dev.service.image.info" + serTask.getTaskNumber();
            String devSerImageInfoStr = objectMapper.writeValueAsString(devSerImageInfo);
            redisUtil.set(key, CryptUtil.encrypt(devSerImageInfoStr), CommonConstant.EXPIRE_TIME.getValue());

            // 使TaskInfoVO
            TaskInfoVO taskInfoVO = new TaskInfoVO();
            taskInfoVO.setCustomerGender(serInspected.getGender());
            taskInfoVO.setTaskId(serTask.getTaskId());
            taskInfoVO.setTaskNumber(serTask.getTaskNumber());
            scanInfoSaveResultVO.setWorkModeName(sysWorkMode.getModeName());
            scanInfoSaveResultVO.setGuid(device.getGuid());
            scanInfoSaveResultVO.setIsSucceed(true);
            scanInfoSaveResultVO.setTaskNumber(serTask.getTaskNumber());
            scanInfoSaveResultVO.setTaskInfoVO(taskInfoVO);
            return scanInfoSaveResultVO;
        } catch (Exception ex) {
            log.error("保存扫描结果");
            log.error(ex.getMessage());
            scanInfoSaveResultVO.setIsSucceed(false);
            return scanInfoSaveResultVO;
        }
    }

    /**
     *  保存检查人员数据
     *
     */
    private SerInspected saveInspected(SerTask serTask, DevSerImageInfoModel devSerImageInfo) {
        String inspectedGender = "";

        // 得到性别
        if (devSerImageInfo.getPersonData().getSex().equals(Integer.valueOf(DeviceGenderType.MALE.getValue()))) {
            inspectedGender = GenderType.MALE.getValue();
        } else if (devSerImageInfo.getPersonData().getSex().equals(Integer.valueOf(DeviceGenderType.FEMALE.getValue()))) {
            inspectedGender = GenderType.FEMALE.getValue();
        }
        SerInspected serInspected = SerInspected.builder()
                .serTask(serTask)
                .suspectName(devSerImageInfo.getPersonData().getName())
                .identityCard(devSerImageInfo.getPersonData().getNumber())
                .identityCategory(devSerImageInfo.getPersonData().getType().toString())
                .gender(inspectedGender)
                .face(devSerImageInfo.getPersonData().getFace())
                .address(devSerImageInfo.getPersonData().getAddress())
                .build();
        serInspected = serInspectedRepository.save(serInspected);
        return serInspected;
    }

    /**
     * 插入到 ser_image_property
     */
    private void saveImageProperty(SerImage serImage, String name, String value) {
        SerImageProperty serImagePropertyX = SerImageProperty.builder()
                .serImage(serImage)
                .imagePropertyName(name)
                .imagePropertyValue(value)
                .build();
        serImagePropertyRepository.save(serImagePropertyX);
    }

    /**
     * 创建任务信息
     *
     */
    private SerTask saveSerTask(String guid, SysDevice device, SysWorkMode sysWorkMode, SysWorkflow sysWorkflow, SysDeviceConfig sysDeviceConfig, String taskNumber) {
        if(StringUtils.isBlank(taskNumber)) {
            taskNumber = BackgroundServiceUtil.generateTaskNumber(guid);
        }
        SysWorkflow sysWorkflowModel = new SysWorkflow();
        Example<SysWorkflow> sysWorkflowEx;

        // 从数据库获取服务任务信息
        SerTask serTask = new SerTask();

        serTask.setTaskNumber(taskNumber);
        serTask.setDevice(device);
        serTask.setSysWorkMode(sysWorkMode);
        if (sysWorkMode != null) {
            serTask.setSysWorkModeName(sysWorkMode.getModeName());
        }
        serTask.setTaskStatus(TaskStatusType.SECURITY.getValue());
        serTask.setSysWorkflow(sysWorkflow);
        if (sysDeviceConfig != null) {
            serTask.setSysField(sysDeviceConfig.getSysField());
        }
        serTask = serTaskRepository.save(serTask);

        // 保存到ser_task_step
        sysWorkflowModel.setSysWorkMode(sysWorkMode);
        if (sysWorkflow != null) {
            SerTaskStep serTaskStepSecurity = SerTaskStep.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .build();
            serTaskStepRepository.save(serTaskStepSecurity);

            // 检查设备类型并创建判断步骤和手册
            if (sysWorkMode.getModeName().equals(WorkModeType.SECURITY_JUDGE.getValue())
                    || sysWorkMode.getModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
                sysWorkflowModel.setTaskType(TaskType.JUDGE.getValue());
                sysWorkflowEx = Example.of(sysWorkflowModel);
                SysWorkflow sysWorkflowJudge = sysWorkflowRepository.findOne(sysWorkflowEx);
                SerTaskStep serTaskStepJudge = SerTaskStep.builder()
                        .serTask(serTask)
                        .sysWorkflow(sysWorkflowJudge)
                        .build();
                serTaskStepRepository.save(serTaskStepJudge);
            }
            if (sysWorkMode.getModeName().equals(WorkModeType.SECURITY_MANUAL.getValue())
                    || sysWorkMode.getModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
                sysWorkflowModel.setTaskType(TaskType.HAND.getValue());
                sysWorkflowEx = Example.of(sysWorkflowModel);
                SysWorkflow sysWorkflowHand = sysWorkflowRepository.findOne(sysWorkflowEx);
                SerTaskStep serTaskStepHand = SerTaskStep.builder()
                        .serTask(serTask)
                        .sysWorkflow(sysWorkflowHand)
                        .build();
                serTaskStepRepository.save(serTaskStepHand);
            }
        }
        return serTask;
    }

    /**
     * 4.3.1.14 安检仪向后台服务同步数据
     *
     * @param devSerImageInfo
     * @return boolean
     */
    @Override
    public Boolean synchronizeScanResult(DevSerDataSyncModel devSerImageInfo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 从数据库获取设备信息
            String guid = devSerImageInfo.getGuid();
            SysDevice device = null;
            SysDeviceConfig sysDeviceConfig = null;
            SysWorkMode sysWorkMode = null;
            SysWorkflow sysWorkflow = null;
            SysDevice deviceModel = new SysDevice();
            deviceModel.setGuid(guid);
            if (guid != null) {
                Example<SysDevice> serDeviceEx = Example.of(deviceModel);
                device = sysDeviceRepository.findOne(serDeviceEx);
                // 获取设备配置和工作流程
                sysDeviceConfig = sysDeviceConfigRepository.findLatestConfig(device.getDeviceId());
                sysWorkMode = sysDeviceConfig.getSysWorkMode();
                SysWorkflow sysWorkflowModel = new SysWorkflow();
                sysWorkflowModel.setSysWorkMode(sysWorkMode);
                sysWorkflowModel.setTaskType(TaskType.SECURITY.getValue());
                Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
                sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);
            }
            // 更新任务信息
            boolean isNewTask = true;
            String taskNumber = devSerImageInfo.getImageData().getImageGuid();
            SerTask serTask = new SerTask();
            if (StringUtils.isNotBlank(taskNumber)) {
                SerTask taskModel = new SerTask();
                taskModel.setTaskNumber(taskNumber);
                Example<SerTask> serTaskEx = Example.of(taskModel);
                serTask = serTaskRepository.findOne(serTaskEx);
                if (serTask != null) {
                    isNewTask = false;
                }
            }
            if (isNewTask) {
                // if not exist
                // save ser_task
                serTask = saveSerTask(guid, device, sysWorkMode, sysWorkflow, sysDeviceConfig, taskNumber);
            } else {
                serTask = updateSerTask(serTask, device, sysWorkMode, sysWorkflow, sysDeviceConfig);
            }


            // 从具有登录名的数据库中获取用户信息
            SysUser sysUserModel = new SysUser();
            sysUserModel.setUserAccount(devSerImageInfo.getImageData().getLoginName());
            Example<SysUser> sysUserEx = Example.of(sysUserModel);
            SysUser sysUser = sysUserRepository.findOne(sysUserEx);

            devSerImageInfo.getImageData().setImageGuid(serTask.getTaskNumber());



            // 将扫描图像信息保存到数据库
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<DeviceImageModel> deviceImageModels = devSerImageInfo.getImageData().getDeviceImages();
            String deviceImageJsonStr = objectMapper.writeValueAsString(deviceImageModels);
            // 获取ser_scan信息
            SerScan serScanModel = new SerScan();
            serScanModel.setSerTask(serTask);
            Example<SerScan> serScanEx = Example.of(serScanModel);
            SerScan serScan = serScanRepository.findOne(serScanEx);

            // 更新ser_scan
            if (serScan == null) {
                // create serScan
                serScan = SerScan.builder()
                        .serTask(serTask)
                        .sysWorkflow(sysWorkflow)
                        .sysDevice(device)
                        .scanDeviceImages(deviceImageJsonStr)
                        .scanATRResult(BackgroundServiceUtil.dictConvert(devSerImageInfo.getImageData().getAtrResult()))
                        .scanInvalid((devSerImageInfo.getImageData().getInvalidScan()))
                        .scanFootAlarm(BackgroundServiceUtil.dictConvert(devSerImageInfo.getImageData().getFootAlarmed()))
                        .scanStartTime(sdf.parse(devSerImageInfo.getImageData().getScanBeginTime()))
                        .scanEndTime(sdf.parse(devSerImageInfo.getImageData().getScanEndTime()))
                        .scanPointsman(sysUser)
                        .scanAssignTimeout(TimeDefaultType.FALSE.getValue())
                        .scanOffline(Integer.valueOf(devSerImageInfo.getImageData().getOffline()))
                        .scanImageGender(BackgroundServiceUtil.genderConvert(devSerImageInfo.getImageData().getImageGender()))
                        .scanData(devSerImageInfo.getImageData().getData())
                        .scanRandomAlarm((devSerImageInfo.getImageData().getRandomAlarm()))
                        .scanKeyPoint(objectMapper.writeValueAsString(devSerImageInfo.getImageData().getKeyPoint()))
                        .build();
            } else {
                // update serScan
                serScan.setSerTask(serTask);
                serScan.setSysWorkflow(sysWorkflow);
                serScan.setSysDevice(device);
                serScan.setScanDeviceImages(deviceImageJsonStr);
                serScan.setScanATRResult(BackgroundServiceUtil.dictConvert(devSerImageInfo.getImageData().getAtrResult()));
                serScan.setScanInvalid((devSerImageInfo.getImageData().getInvalidScan()));
                serScan.setScanFootAlarm(BackgroundServiceUtil.dictConvert(devSerImageInfo.getImageData().getFootAlarmed()));
                serScan.setScanStartTime(sdf.parse(devSerImageInfo.getImageData().getScanBeginTime()));
                serScan.setScanEndTime(sdf.parse(devSerImageInfo.getImageData().getScanEndTime()));
                serScan.setScanPointsman(sysUser);
                serScan.setScanAssignTimeout(TimeDefaultType.FALSE.getValue());
                serScan.setScanOffline(Integer.valueOf(devSerImageInfo.getImageData().getOffline()));
                serScan.setScanImageGender(BackgroundServiceUtil.genderConvert(devSerImageInfo.getImageData().getImageGender()));
                serScan.setScanData(devSerImageInfo.getImageData().getData());
                serScan.setScanRandomAlarm((devSerImageInfo.getImageData().getRandomAlarm()));
                serScan.setScanKeyPoint(objectMapper.writeValueAsString(devSerImageInfo.getImageData().getKeyPoint()));
                serScan.setScanImageId(taskNumber);
            }
            serScan = serScanRepository.save(serScan);


            // 删除相应的图像和图像属性
            SerImage serImageModel = new SerImage();
            serImageModel.setSerScan(serScan);
            Example<SerImage> serImageEx = Example.of(serImageModel);
            List<SerImage> serImages = serImageRepository.findAll(serImageEx);

            for (SerImage item : serImages) {
                SerImageProperty serImagePropertyModel = new SerImageProperty();
                serImagePropertyModel.setSerImage(item);
                Example<SerImageProperty> serImagePropertyEx = Example.of(serImagePropertyModel);
                List<SerImageProperty> serImageProperties = serImagePropertyRepository.findAll(serImagePropertyEx);
                serImagePropertyRepository.delete(serImageProperties);
            }
            serImageRepository.delete(serImages);


            // 创建图像和图像属性
            for (DeviceImageModel deviceImageModel : devSerImageInfo.getImageData().getDeviceImages()) {
                SerImage serImage = SerImage.builder()
                        .serTask(serTask)
                        .serScan(serScan)
                        .imageFormat(ImageFormatType.IMAGE.getValue())
                        .imageUrl(deviceImageModel.getImage())
                        .build();
                serImage = serImageRepository.save(serImage);
                for (ImageRectModel imageRectModel : deviceImageModel.getImageRects()) {
                    saveImageProperty(serImage, "x", imageRectModel.getX().toString());
                    saveImageProperty(serImage, "y", imageRectModel.getY().toString());
                    saveImageProperty(serImage, "width", imageRectModel.getWidth().toString());
                    saveImageProperty(serImage, "height", imageRectModel.getHeight().toString());
                }
                SerImage serCartoon = SerImage.builder()
                        .serTask(serTask)
                        .serScan(serScan)
                        .imageFormat(ImageFormatType.CARTOON.getValue())
                        .imageUrl(deviceImageModel.getCartoon())
                        .build();
                serCartoon = serImageRepository.save(serCartoon);
                for (ImageRectModel imageRectModel : deviceImageModel.getCartoonRects()) {
                    saveImageProperty(serCartoon, "x", imageRectModel.getX().toString());
                    saveImageProperty(serCartoon, "y", imageRectModel.getY().toString());
                    saveImageProperty(serCartoon, "width", imageRectModel.getWidth().toString());
                    saveImageProperty(serCartoon, "height", imageRectModel.getHeight().toString());
                }
            }


            // 获取检查的数据信息
            SerInspected serInspectedModel = new SerInspected();
            serInspectedModel.setSerTask(serTask);
            Example<SerInspected> serInspectedEx = Example.of(serInspectedModel);
            SerInspected serInspected = serInspectedRepository.findOne(serInspectedEx);
            // 更新检查的数据
            String inspectedGender = "";
            if (devSerImageInfo.getPersonData().getSex().equals(Integer.valueOf(DeviceGenderType.MALE.getValue()))) {
                inspectedGender = GenderType.MALE.getValue();
            } else if (devSerImageInfo.getPersonData().getSex().equals(Integer.valueOf(DeviceGenderType.FEMALE.getValue()))) {
                inspectedGender = GenderType.FEMALE.getValue();
            }
            // if serInspected exists then update or not exists then create
            if (serInspected == null) {
                // create serInspected
                serInspected = SerInspected.builder()
                        .serTask(serTask)
                        .suspectName(devSerImageInfo.getPersonData().getName())
                        .identityCard(devSerImageInfo.getPersonData().getNumber())
                        .identityCategory(devSerImageInfo.getPersonData().getType().toString())
                        .gender(inspectedGender)
                        .face(devSerImageInfo.getPersonData().getFace())
                        .address(devSerImageInfo.getPersonData().getAddress())
                        .build();
            } else {
                // update serInspected
                serInspected.setSerTask(serTask);
                serInspected.setSuspectName(devSerImageInfo.getPersonData().getName());
                serInspected.setIdentityCard(devSerImageInfo.getPersonData().getNumber());
                serInspected.setIdentityCategory(devSerImageInfo.getPersonData().getType().toString());
                serInspected.setGender(inspectedGender);
                serInspected.setFace(devSerImageInfo.getPersonData().getFace());
                serInspected.setAddress(devSerImageInfo.getPersonData().getAddress());
            }
            serInspectedRepository.save(serInspected);


            // 更新历史记录数据
            History historyModel = new History();
            historyModel.setSerTask(serTask);
            historyModel.setSysWorkflow(sysWorkflow);
            historyModel.setScanDevice(device);
            Example<History> historyEx = Example.of(historyModel);
            History history = historyRepository.findOne(historyEx);

            if (history == null) {
                // create history
                history = History.builder()
                        .serTask(serTask)
                        .sysWorkMode(sysWorkMode)
                        .serScan(serScan)
                        .sysWorkflow(sysWorkflow)
                        .scanDevice(device)
                        .scanATRResult(serScan.getScanATRResult())
                        .scanFootAlarm(serScan.getScanFootAlarm())
                        .scanStartTime(serScan.getScanStartTime())
                        .scanEndTime(serScan.getScanEndTime())
                        .scanPointsman(serScan.getScanPointsman())
                        .scanPointsmanName(serScan.getScanPointsman().getUserName())
                        .build();
            } else {
                // update history
                history.setSysWorkMode(sysWorkMode);
                history.setScanATRResult(serScan.getScanATRResult());
                history.setScanFootAlarm(serScan.getScanFootAlarm());
                history.setScanStartTime(serScan.getScanStartTime());
                history.setScanEndTime(serScan.getScanEndTime());
                history.setScanPointsman(serScan.getScanPointsman());
                history.setScanPointsmanName(serScan.getScanPointsman().getUserName());
            }
            historyRepository.save(history);

            return true;
        } catch (Exception ex) {
            log.error("无法同步扫描结果");
            log.error(ex.getMessage());
            return false;
        }
    }

    /**
     * 更新任务信息
     *
     * @param serTask
     * @param device
     * @param sysWorkMode
     * @param sysWorkflow
     * @param sysDeviceConfig
     * @return
     */
    private SerTask updateSerTask(SerTask serTask, SysDevice device, SysWorkMode sysWorkMode, SysWorkflow sysWorkflow, SysDeviceConfig sysDeviceConfig) {

        //创建任务
        serTask.setDevice(device);
        serTask.setSysWorkMode(sysWorkMode);
        serTask.setSysWorkModeName(sysWorkMode.getModeName());
        serTask.setTaskStatus(TaskStatusType.SECURITY.getValue());
        serTask.setSysField(sysDeviceConfig.getSysField());
        serTaskRepository.save(serTask);

        // 保存到ser_task_step
        SerTaskStep serTaskStepModel = new SerTaskStep();
        serTaskStepModel.setSerTask(serTask);
        serTaskStepModel.setSysWorkflow(sysWorkflow);
        Example<SerTaskStep> serTaskStepEx = Example.of(serTaskStepModel);
        SerTaskStep serTaskStep = serTaskStepRepository.findOne(serTaskStepEx);
        if (serTaskStep == null) {
            serTaskStep = SerTaskStep.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .build();
            serTaskStepRepository.save(serTaskStep);
        }

        // 检查设备类型并创建判断步骤和手册

        SysWorkflow sysWorkflowModel = new SysWorkflow();
        Example<SysWorkflow> sysWorkflowEx;
        if (sysWorkMode.getModeName().equals(WorkModeType.SECURITY_JUDGE.getValue())
                || sysWorkMode.getModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
            sysWorkflowModel.setTaskType(TaskType.HAND.getValue());
            sysWorkflowEx = Example.of(sysWorkflowModel);
            SysWorkflow sysWorkflowJudge = sysWorkflowRepository.findOne(sysWorkflowEx);
            serTaskStepModel.setSerTask(serTask);
            serTaskStepModel.setSysWorkflow(sysWorkflowJudge);
            serTaskStepEx = Example.of(serTaskStepModel);
            SerTaskStep serTaskStepJudge = serTaskStepRepository.findOne(serTaskStepEx);
            if (serTaskStepJudge == null) {
                serTaskStepJudge = SerTaskStep.builder()
                        .serTask(serTask)
                        .sysWorkflow(sysWorkflowJudge)
                        .build();
            }
            serTaskStepRepository.save(serTaskStepJudge);
        }


        if (sysWorkMode.getModeName().equals(WorkModeType.SECURITY_MANUAL.getValue())
                || sysWorkMode.getModeName().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
            sysWorkflowModel.setTaskType(TaskType.HAND.getValue());
            sysWorkflowEx = Example.of(sysWorkflowModel);
            SysWorkflow sysWorkflowHand = sysWorkflowRepository.findOne(sysWorkflowEx);
            serTaskStepModel.setSerTask(serTask);
            serTaskStepModel.setSysWorkflow(sysWorkflowHand);
            serTaskStepEx = Example.of(serTaskStepModel);
            SerTaskStep serTaskStepHand = serTaskStepRepository.findOne(serTaskStepEx);
            if (serTaskStepHand == null) {
                serTaskStepHand = SerTaskStep.builder()
                        .serTask(serTask)
                        .sysWorkflow(sysWorkflowHand)
                        .build();
            }
            serTaskStepRepository.save(serTaskStepHand);
        }
        return serTask;
    }

    /**
     * 4.3.1.15 后台服务向安检仪推送判图结论
     *
     * @param taskNumber
     * @return serDevJudgeGraphResult
     */
    @Override
    public SerDevJudgeGraphResultModel sendJudgeResultToSecurity(String taskNumber) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            //获得当前任务
            SerTask taskModel = new SerTask();
            taskModel.setTaskNumber(taskNumber);
            Example<SerTask> serTaskEx = Example.of(taskModel);
            SerTask serTask = serTaskRepository.findOne(serTaskEx);

            // 从redis获取分配数据
            String assignInfoStr = "";
            assignInfoStr = redisUtil.get(BackgroundServiceUtil
                        .getConfig("redisKey.sys.manual.assign.task.info") + taskNumber);

            DispatchManualDeviceInfoVO assignInfo = objectMapper.readValue(assignInfoStr, DispatchManualDeviceInfoVO.class);
            SysDevice sysDeviceModel = SysDevice.builder()
                    .deviceSerial(assignInfo.getRecheckNumber())
                    .build();
            Example<SysDevice> sysDeviceEx = Example.of(sysDeviceModel);
            SysDevice assignedSecurityDevice = sysDeviceRepository.findOne(sysDeviceEx);

            // 从redis获取扫描数据
            String serDevJudgeResultkey = "ser.security.judge.result" + serTask.getTaskNumber();
            String serManImageInfoStr = redisUtil.get(serDevJudgeResultkey);
            SerDevJudgeGraphResultModel serDevJudgeGraphResult = objectMapper.readValue(serManImageInfoStr, SerDevJudgeGraphResultModel.class);
            serDevJudgeGraphResult.setGuid(assignedSecurityDevice.getGuid());
            return serDevJudgeGraphResult;
        } catch (Exception ex) {
            log.error("无法将判断结果发送到安检仪");
            log.error(ex.getMessage());
            SerDevJudgeGraphResultModel serDevJudgeGraphResult = SerDevJudgeGraphResultModel.builder().build();
            return serDevJudgeGraphResult;
        }
    }

    @Override
    public boolean checkAssignHand(DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO) {
        String taskNumber = dispatchManualDeviceInfoVO.getImageGuid();
        String identityKey = "";
        try {
            SerTask serTaskModel = SerTask.builder()
                    .taskNumber(taskNumber)
                    .build();
            Example<SerTask> serTaskEx = Example.of(serTaskModel);
            SerTask serTask = serTaskRepository.findOne(serTaskEx);

            // 对应的安检仪
            SysDevice securityDevice = serTask.getDevice();



            if(securityDevice.getFieldId() != null) {
                identityKey = securityDevice.getFieldId().toString();
            } else {
                identityKey = securityDevice.getGuid();
            }
            while (true) {
                log.info("任务" + taskNumber + "正在等待分配手检站");

                //check locked or not.
                boolean lockResult = redisUtil.aquirePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.manual.process.running.info") + identityKey,
                        taskNumber + identityKey, CommonConstant.MAX_MANUAL_REDIS_LOCK.getValue());

                if (!lockResult) {
                    log.error("任务" + taskNumber + "无法检查手检站");
                    Thread.sleep(100);
                    continue;
                }
                DispatchManualDeviceInfoVO oldDispatchManualDeviceInfoVO = sysDeviceService.isManualDeviceDispatched(taskNumber);
                if(oldDispatchManualDeviceInfoVO != null) {
                    log.error("Already assigned hand device");
                    redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.manual.process.running.info") + identityKey,
                            taskNumber + identityKey);
                    return false;
                }

                SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findLatestConfig(serTask.getDevice().getDeviceId());
                SysWorkMode sysWorkMode = sysDeviceConfig.getSysWorkMode();
                SysWorkflow sysWorkflowModel = new SysWorkflow();
                sysWorkflowModel.setSysWorkMode(sysWorkMode);
                sysWorkflowModel.setTaskType(TaskType.HAND.getValue());
                Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
                SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);


                SerLoginInfo serLoginInfo = serLoginInfoRepository.findLatestLoginInfo(securityDevice.getDeviceId());
                SysUser logInedUser = SysUser.builder()
                        .userId(serLoginInfo.getUserId()).build();

                SerAssign assign = new SerAssign();
                assign.setSerTask(serTask);
                assign.setSysWorkflow(sysWorkflow);
                assign.setSysWorkMode(sysWorkMode);
                assign.setAssignUser(logInedUser);
                assign.setAssignHandDevice(securityDevice);
                assign.setAssignEndTime(DateUtil.getCurrentDate());
                assign.setAssignStartTime(DateUtil.getCurrentDate());

                serAssignRepository.save(assign);
                dispatchManualDeviceInfoVO = new DispatchManualDeviceInfoVO();
                dispatchManualDeviceInfoVO.setAssignId(assign.getAssignId());
                dispatchManualDeviceInfoVO.setImageGuid(taskNumber);
                dispatchManualDeviceInfoVO.setLocalRecheck(false);
                dispatchManualDeviceInfoVO.setRecheckNumber(securityDevice.getDeviceSerial());
                dispatchManualDeviceInfoVO.setGuid(securityDevice.getGuid());

                ObjectMapper objectMapper = new ObjectMapper();
                redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.manual.assign.task.info") + taskNumber,
                        CryptUtil.encrypt(objectMapper.writeValueAsString(dispatchManualDeviceInfoVO)), CommonConstant.EXPIRE_TIME.getValue());
                redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.manual.process.running.info") + identityKey,
                        taskNumber + identityKey);
                break;
            }
        }catch (Exception ex) {
            log.error("Failed to assign security device to hand device");
            log.error(ex.getMessage());
            redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.manual.process.running.info") + identityKey,
                    taskNumber + identityKey);
            return false;
        }

        return true;
    }
}
