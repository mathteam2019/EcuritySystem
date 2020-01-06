package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.constants.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.*;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.DispatchManualDeviceInfoVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.ScanInfoSaveResultVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.TaskInfoVO;
import com.nuctech.securitycheck.backgroundservice.repositories.*;
import com.nuctech.securitycheck.backgroundservice.service.ISecurityImageInfoService;
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
            // retrieve scan device by guid
            String guid = devSerImageInfo.getGuid();
            SysDevice deviceModel = new SysDevice();
            deviceModel.setGuid(guid);
            Example<SysDevice> serDeviceEx = Example.of(deviceModel);
            SysDevice device = sysDeviceRepository.findOne(serDeviceEx);
            // get this device's work mode
            SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findLatestConfig(device.getDeviceId());
            SysWorkMode sysWorkMode = sysDeviceConfig.getSysWorkMode();
            // get this device's current workflow
            SysWorkflow sysWorkflowModel = new SysWorkflow();
            sysWorkflowModel.setSysWorkMode(sysWorkMode);
            sysWorkflowModel.setTaskType(TaskType.SECURITY.getValue());
            Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
            SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);
            // if task exists or not?
            boolean isNewTask = true;
            String taskNumber = devSerImageInfo.getImageGuid();
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
                serTask = saveSerTask(guid, device, sysWorkMode, sysWorkflow, sysDeviceConfig);
            }
            devSerImageInfo.setImageGuid(serTask.getTaskNumber());

            // get SysUser login in security scanner
            SysUser sysUserModel = new SysUser();
            sysUserModel.setUserAccount(devSerImageInfo.getLoginName());
            Example<SysUser> sysUserEx = Example.of(sysUserModel);
            SysUser sysUser = sysUserRepository.findOne(sysUserEx);

            // save security scan data
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<DeviceImageModel> deviceImageModels = devSerImageInfo.getDeviceImages();
            String deviceImageJsonStr = objectMapper.writeValueAsString(deviceImageModels);
            SerScan serScan = SerScan.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .sysDevice(device)
                    .scanDeviceImages(deviceImageJsonStr)
                    .scanATRResult(BackgroundServiceUtil.dictConvert(devSerImageInfo.getAtrResult()))
                    .scanInvalid(BackgroundServiceUtil.dictConvert(devSerImageInfo.getInvalidScan()))
                    .scanFootAlarm(BackgroundServiceUtil.dictConvert(devSerImageInfo.getFootAlarmed()))
                    .scanStartTime(sdf.parse(devSerImageInfo.getScanBeginTime()))
                    .scanEndTime(sdf.parse(devSerImageInfo.getScanEndTime()))
                    .scanPointsman(sysUser)
                    .scanAssignTimeout(DefaultType.FALSE.getValue())
                    .scanOffline(devSerImageInfo.getOffline())
                    .scanImageGender(devSerImageInfo.getImageGender())
                    .scanData(devSerImageInfo.getData())
                    .scanRandomAlarm(BackgroundServiceUtil.dictConvert(devSerImageInfo.getRandomAlarm()))
                    .build();
            serScan = serScanRepository.save(serScan);
            for (DeviceImageModel deviceImageModel : devSerImageInfo.getDeviceImages()) {
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

            // save inspected person's data
            SerInspected serInspected = saveInspected(serTask, devSerImageInfo);

            // save scan result to history
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

            // insert data to redis
            String key = "dev.service.image.info" + serTask.getTaskNumber();
            String devSerImageInfoStr = objectMapper.writeValueAsString(devSerImageInfo);
            redisUtil.set(key, CryptUtil.encrypt(devSerImageInfoStr), CommonConstant.EXPIRE_TIME);

            // hand device assgin data setting
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
            log.error(ex.getMessage());
            scanInfoSaveResultVO.setIsSucceed(false);
            return scanInfoSaveResultVO;
        }
    }

    /**
     * insert ser_inspected
     *
     */
    private SerInspected saveInspected(SerTask serTask, DevSerImageInfoModel devSerImageInfo) {
        String inspectedGender = "";
        if (devSerImageInfo.getSex().equals(Integer.valueOf(DeviceGenderType.MALE.getValue()))) {
            inspectedGender = GenderType.MALE.getValue();
        } else if (devSerImageInfo.getSex().equals(Integer.valueOf(DeviceGenderType.FEMALE.getValue()))) {
            inspectedGender = GenderType.FEMALE.getValue();
        }
        SerInspected serInspected = SerInspected.builder()
                .serTask(serTask)
                .suspectName(devSerImageInfo.getName())
                .identityCard(devSerImageInfo.getNumber())
                .identityCategory(devSerImageInfo.getType().toString())
                .gender(inspectedGender)
                .face(devSerImageInfo.getFace())
                .address(devSerImageInfo.getAddress())
                .build();
        serInspected = serInspectedRepository.save(serInspected);
        return serInspected;
    }

    /**
     * insert ser_image_property
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
     * insert ser_task
     *
     */
    private SerTask saveSerTask(String guid, SysDevice device, SysWorkMode sysWorkMode, SysWorkflow sysWorkflow, SysDeviceConfig sysDeviceConfig) {
        String taskNumber = BackgroundServiceUtil.generateTaskNumber(guid);
        SysWorkflow sysWorkflowModel = new SysWorkflow();
        Example<SysWorkflow> sysWorkflowEx;
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

        // save ser_task_step
        sysWorkflowModel.setSysWorkMode(sysWorkMode);
        if (sysWorkflow != null) {
            SerTaskStep serTaskStepSecurity = SerTaskStep.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .build();
            serTaskStepRepository.save(serTaskStepSecurity);
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
            // retrieve scan device by guid
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
                // get this device's work mode
                sysDeviceConfig = sysDeviceConfigRepository.findLatestConfig(device.getDeviceId());
                sysWorkMode = sysDeviceConfig.getSysWorkMode();
                // get this device's current workflow
                SysWorkflow sysWorkflowModel = new SysWorkflow();
                sysWorkflowModel.setSysWorkMode(sysWorkMode);
                sysWorkflowModel.setTaskType(TaskType.SECURITY.getValue());
                Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
                sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);
            }
            // if task exists or not?
            boolean isNewTask = true;
            String taskNumber = devSerImageInfo.getImageGuid();
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
                serTask = saveSerTask(guid, device, sysWorkMode, sysWorkflow, sysDeviceConfig);
            } else {
                serTask = updateSerTask(serTask, device, sysWorkMode, sysWorkflow, sysDeviceConfig);
            }
            devSerImageInfo.setImageGuid(serTask.getTaskNumber());

            // get SysUser login in security scanner
            SysUser sysUserModel = new SysUser();
            sysUserModel.setUserAccount(devSerImageInfo.getLoginName());
            Example<SysUser> sysUserEx = Example.of(sysUserModel);
            SysUser sysUser = sysUserRepository.findOne(sysUserEx);

            // save security scan data
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<DeviceImageModel> deviceImageModels = devSerImageInfo.getDeviceImages();
            String deviceImageJsonStr = objectMapper.writeValueAsString(deviceImageModels);
            // if ser_scan data exists or not?
            SerScan serScanModel = new SerScan();
            serScanModel.setSerTask(serTask);
            Example<SerScan> serScanEx = Example.of(serScanModel);
            SerScan serScan = serScanRepository.findOne(serScanEx);
            // if serScan exists then update or not exists create
            if (serScan == null) {
                // create serScan
                serScan = SerScan.builder()
                        .serTask(serTask)
                        .sysWorkflow(sysWorkflow)
                        .sysDevice(device)
                        .scanDeviceImages(deviceImageJsonStr)
                        .scanATRResult(BackgroundServiceUtil.dictConvert(devSerImageInfo.getAtrResult()))
                        .scanInvalid(BackgroundServiceUtil.dictConvert(devSerImageInfo.getInvalidScan()))
                        .scanFootAlarm(BackgroundServiceUtil.dictConvert(devSerImageInfo.getFootAlarmed()))
                        .scanStartTime(sdf.parse(devSerImageInfo.getScanBeginTime()))
                        .scanEndTime(sdf.parse(devSerImageInfo.getScanEndTime()))
                        .scanPointsman(sysUser)
                        .scanAssignTimeout(DefaultType.FALSE.getValue())
                        .scanOffline(devSerImageInfo.getOffline())
                        .scanImageGender(devSerImageInfo.getImageGender())
                        .scanData(devSerImageInfo.getData())
                        .scanRandomAlarm(BackgroundServiceUtil.dictConvert(devSerImageInfo.getRandomAlarm()))
                        .build();
            } else {
                // update serScan
                serScan.setSerTask(serTask);
                serScan.setSysWorkflow(sysWorkflow);
                serScan.setSysDevice(device);
                serScan.setScanDeviceImages(deviceImageJsonStr);
                serScan.setScanATRResult(BackgroundServiceUtil.dictConvert(devSerImageInfo.getAtrResult()));
                serScan.setScanInvalid(BackgroundServiceUtil.dictConvert(devSerImageInfo.getInvalidScan()));
                serScan.setScanFootAlarm(BackgroundServiceUtil.dictConvert(devSerImageInfo.getFootAlarmed()));
                serScan.setScanStartTime(sdf.parse(devSerImageInfo.getScanBeginTime()));
                serScan.setScanEndTime(sdf.parse(devSerImageInfo.getScanEndTime()));
                serScan.setScanPointsman(sysUser);
                serScan.setScanAssignTimeout(DefaultType.FALSE.getValue());
                serScan.setScanOffline(devSerImageInfo.getOffline());
                serScan.setScanImageGender(devSerImageInfo.getImageGender());
                serScan.setScanData(devSerImageInfo.getData());
                serScan.setScanRandomAlarm(BackgroundServiceUtil.dictConvert(devSerImageInfo.getRandomAlarm()));
            }
            serScan = serScanRepository.save(serScan);
            // if ser_image & ser_image_property exists delete all
            SerImage serImageModel = new SerImage();
            serImageModel.setSerScan(serScan);
            Example<SerImage> serImageEx = Example.of(serImageModel);
            List<SerImage> serImages = serImageRepository.findAll(serImageEx);
            // if serImages for this task exist delete all their imageProperties
            for (SerImage item : serImages) {
                SerImageProperty serImagePropertyModel = new SerImageProperty();
                serImagePropertyModel.setSerImage(item);
                Example<SerImageProperty> serImagePropertyEx = Example.of(serImagePropertyModel);
                List<SerImageProperty> serImageProperties = serImagePropertyRepository.findAll(serImagePropertyEx);
                serImagePropertyRepository.delete(serImageProperties);
            }
            serImageRepository.delete(serImages);
            // save ser_image & ser_image_property
            for (DeviceImageModel deviceImageModel : devSerImageInfo.getDeviceImages()) {
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
            // if inspected person's data exists or not?
            SerInspected serInspectedModel = new SerInspected();
            serInspectedModel.setSerTask(serTask);
            Example<SerInspected> serInspectedEx = Example.of(serInspectedModel);
            SerInspected serInspected = serInspectedRepository.findOne(serInspectedEx);
            // save inspected person's data
            String inspectedGender = "";
            if (devSerImageInfo.getSex().equals(Integer.valueOf(DeviceGenderType.MALE.getValue()))) {
                inspectedGender = GenderType.MALE.getValue();
            } else if (devSerImageInfo.getSex().equals(Integer.valueOf(DeviceGenderType.FEMALE.getValue()))) {
                inspectedGender = GenderType.FEMALE.getValue();
            }
            // if serInspected exists then update or not exists then create
            if (serInspected == null) {
                // create serInspected
                serInspected = SerInspected.builder()
                        .serTask(serTask)
                        .suspectName(devSerImageInfo.getName())
                        .identityCard(devSerImageInfo.getNumber())
                        .identityCategory(devSerImageInfo.getType().toString())
                        .gender(inspectedGender)
                        .face(devSerImageInfo.getFace())
                        .address(devSerImageInfo.getAddress())
                        .build();
            } else {
                // update serInspected
                serInspected.setSerTask(serTask);
                serInspected.setSuspectName(devSerImageInfo.getName());
                serInspected.setIdentityCard(devSerImageInfo.getNumber());
                serInspected.setIdentityCategory(devSerImageInfo.getType().toString());
                serInspected.setGender(inspectedGender);
                serInspected.setFace(devSerImageInfo.getFace());
                serInspected.setAddress(devSerImageInfo.getAddress());
            }
            serInspectedRepository.save(serInspected);
            // if security scan history data exists or not?
            History historyModel = new History();
            historyModel.setSerTask(serTask);
            historyModel.setSysWorkflow(sysWorkflow);
            historyModel.setScanDevice(device);
            Example<History> historyEx = Example.of(historyModel);
            History history = historyRepository.findOne(historyEx);
            // if exist then update or not exist then create history
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
            log.error(ex.getMessage());
            return false;
        }
    }

    // update ser_task and ser_task_step
    private SerTask updateSerTask(SerTask serTask, SysDevice device, SysWorkMode sysWorkMode, SysWorkflow sysWorkflow, SysDeviceConfig sysDeviceConfig) {
        serTask.setDevice(device);
        serTask.setSysWorkMode(sysWorkMode);
        serTask.setSysWorkModeName(sysWorkMode.getModeName());
        serTask.setTaskStatus(TaskStatusType.SECURITY.getValue());
        serTask.setSysField(sysDeviceConfig.getSysField());
        serTaskRepository.save(serTask);
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
            //get the current task
            SerTask taskModel = new SerTask();
            taskModel.setTaskNumber(taskNumber);
            Example<SerTask> serTaskEx = Example.of(taskModel);
            SerTask serTask = serTaskRepository.findOne(serTaskEx);

            // get the assign data from redis
            String assignInfoStr = "";
            assignInfoStr = redisUtil.get(BackgroundServiceUtil
                        .getConfig("redisKey.sys.manual.assign.task.info") + taskNumber);

            DispatchManualDeviceInfoVO assignInfo = objectMapper.readValue(assignInfoStr, DispatchManualDeviceInfoVO.class);
            SysDevice sysDeviceModel = SysDevice.builder()
                    .deviceSerial(assignInfo.getRecheckNumber())
                    .build();
            Example<SysDevice> sysDeviceEx = Example.of(sysDeviceModel);
            SysDevice assignedSecurityDevice = sysDeviceRepository.findOne(sysDeviceEx);

            // get the before scan data from redis
            String serDevJudgeResultkey = "ser.security.judge.result" + serTask.getTaskNumber();
            String serManImageInfoStr = redisUtil.get(serDevJudgeResultkey);
            SerDevJudgeGraphResultModel serDevJudgeGraphResult = objectMapper.readValue(serManImageInfoStr, SerDevJudgeGraphResultModel.class);
            serDevJudgeGraphResult.setGuid(assignedSecurityDevice.getGuid());
            return serDevJudgeGraphResult;
        } catch (Exception ex) {
            SerDevJudgeGraphResultModel serDevJudgeGraphResult = SerDevJudgeGraphResultModel.builder().build();
            return serDevJudgeGraphResult;
        }
    }
}
