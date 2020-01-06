package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.constants.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceImageJudgeType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceWorkStatusType;
import com.nuctech.securitycheck.backgroundservice.common.enums.ImageJudgeType;
import com.nuctech.securitycheck.backgroundservice.common.enums.TaskType;
import com.nuctech.securitycheck.backgroundservice.common.models.HandSerResultModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SerManImageInfoModel;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;
import com.nuctech.securitycheck.backgroundservice.repositories.*;
import com.nuctech.securitycheck.backgroundservice.service.ISerHandResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SerHandResultServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
@Slf4j
public class SerHandResultServiceImpl implements ISerHandResultService {

    @Autowired
    private SysDeviceRepository sysDeviceRepository;

    @Autowired
    private SysDeviceConfigRepository sysDeviceConfigRepository;

    @Autowired
    private SysWorkflowRepository sysWorkflowRepository;

    @Autowired
    private SerTaskRepository serTaskRepository;

    @Autowired
    private SerAssignRepository serAssignRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SerCheckResultRepository serCheckResultRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private SerHandExaminationRepository serHandExaminationRepository;

    /**
     * 4.3.3.9 手检站后台服务请求提交手检结论
     *
     * @param handSerResult
     * @return isSucceed
     */
    @Override
    public Boolean saveHandResult(HandSerResultModel handSerResult) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // retrieve judge device by guid
            String guid = handSerResult.getGuid();
            SysDevice deviceModel = new SysDevice();
            deviceModel.setGuid(guid);
            Example<SysDevice> serDeviceEx = Example.of(deviceModel);
            SysDevice handDevice = sysDeviceRepository.findOne(serDeviceEx);

            // get SysUser login in security scanner
            String deviceListStr = redisUtil.get(BackgroundServiceUtil
                    .getConfig("redisKey.sys.monitoring.device.status.info"));
            deviceListStr = CryptUtil.decrypt(deviceListStr);
            JSONArray deviceListArray = JSON.parseArray(deviceListStr);
            SysUser sysUser = new SysUser();
            List<SysMonitoringDeviceStatusInfoVO> deviceList = deviceListArray.toJavaList(SysMonitoringDeviceStatusInfoVO.class);
            if (deviceList != null) {
                for (SysMonitoringDeviceStatusInfoVO item : deviceList) {
                    if (StringUtils.isNotBlank(item.getGuid()) && item.getGuid().equals(guid)) {
                        sysUser = item.getLoginUser();
                        break;
                    }
                }
                // get redis device current status and update workStatus
                for (SysMonitoringDeviceStatusInfoVO item : deviceList) {
                    if (StringUtils.isNotBlank(item.getGuid()) && item.getGuid().equals(guid)) {
                        item.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
                        break;
                    }
                }
                deviceListStr = objectMapper.writeValueAsString(deviceList);
                deviceListStr = CryptUtil.encrypt(deviceListStr);
                redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"),
                        deviceListStr, CommonConstant.EXPIRE_TIME);
            }

            // get redis security current status data and update workstatus
            List<SysSecurityInfoVO> sysSecurityInfoVOS;
            String sysSecurityInfoStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"));
            JSONArray sysSecurityInfoListStr = JSONArray.parseArray(sysSecurityInfoStr);
            sysSecurityInfoVOS = sysSecurityInfoListStr.toJavaList(SysSecurityInfoVO.class);
            List<SysManualInfoVO> judgeDeviceList;
            if (sysSecurityInfoVOS != null) {
                for (SysSecurityInfoVO item : sysSecurityInfoVOS) {
                    judgeDeviceList = item.getManualDeviceModelList();
                    if (judgeDeviceList != null) {
                        for (SysManualInfoVO judgeItem : judgeDeviceList) {
                            if (StringUtils.isNotBlank(judgeItem.getDeviceId().toString()) && judgeItem.getDeviceId().equals(handDevice.getDeviceId())) {
                                judgeItem.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
                                break;
                            }
                        }
                        break;
                    }
                }
                redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"),
                        CryptUtil.encrypt(objectMapper.writeValueAsString(sysSecurityInfoVOS)), CommonConstant.EXPIRE_TIME);
            }
            // get this device's current task
            String dispatchManualDeviceStr = redisUtil.get(BackgroundServiceUtil
                    .getConfig("redisKey.sys.manual.assign.guid.info") + handDevice.getDeviceId());
            dispatchManualDeviceStr = CryptUtil.decrypt(dispatchManualDeviceStr);
            DispatchManualDeviceInfoVO dispatchManualDevice = objectMapper.readValue(dispatchManualDeviceStr, DispatchManualDeviceInfoVO.class);
            String taskNumber = dispatchManualDevice.getImageGuid();
            SerTask serTaskModel = SerTask.builder()
                    .taskNumber(taskNumber)
                    .build();
            Example<SerTask> serTaskEx = Example.of(serTaskModel);
            SerTask serTask = serTaskRepository.findOne(serTaskEx);
            // get this device's work mode
            SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findLatestConfig(serTask.getDevice().getDeviceId());
            SysWorkMode sysWorkMode = sysDeviceConfig.getSysWorkMode();
            // get this device's current workflow
            SysWorkflow sysWorkflowModel = new SysWorkflow();
            sysWorkflowModel.setSysWorkMode(sysWorkMode);
            sysWorkflowModel.setTaskType(TaskType.HAND.getValue());
            Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
            SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);

            // save hand result to ser_hand_examination
            SerHandExamination serHandExamination = SerHandExamination.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .handDevice(handDevice)
                    .handResult(BackgroundServiceUtil.dictConvert(handSerResult.getResult()))
                    .handUser(sysUser)
                    .build();
            serHandExamination = serHandExaminationRepository.save(serHandExamination);

            // save hand result to ser_check_result
            String handSubmitRectsStr = objectMapper.writeValueAsString(handSerResult.getSubmitRects());
            String imageJudgeType = "";
            if (handSerResult.getImageJudge().equals(DeviceImageJudgeType.MISJUDGE.getValue())) {
                imageJudgeType = ImageJudgeType.MISJUDGE.getValue();
            } else if (handSerResult.getImageJudge().equals(DeviceImageJudgeType.LEAKJUDGE.getValue())) {
                imageJudgeType = ImageJudgeType.LEAKJUDGE.getValue();
            }
            SerCheckResult serCheckResult = SerCheckResult.builder()
                    .serTask(serTask)
                    .sysDevice(handDevice)
                    .handTaskResult(BackgroundServiceUtil.dictConvert(handSerResult.getResult()))
                    .handGoods(handSerResult.getChecklist())
                    .handCollectSign(BackgroundServiceUtil.dictConvert(handSerResult.getImageKeep()))
                    .handAppraise(imageJudgeType)
                    .handAttached(handSerResult.getFiles())
                    .handSubmitRects(handSubmitRectsStr)
                    .build();
            serCheckResult.setNote(handSerResult.getNote());
            serCheckResultRepository.save(serCheckResult);
            // update hand device work status
            handDevice.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
            sysDeviceRepository.save(handDevice);

            // save check history
            History historyModel = History.builder()
                    .serTask(serTask)
                    .build();
            Example<History> historyEx = Example.of(historyModel);
            History history = historyRepository.findOne(historyEx);
            // get this task's judge assign data
            SerAssign serAssignModel = SerAssign.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .build();
            Example<SerAssign> serAssignEx = Example.of(serAssignModel);
            SerAssign serAssign = serAssignRepository.findOne(serAssignEx);

            history.setAssignHandDevice(serAssign.getAssignHandDevice());
            history.setAssignWorkflowHand(serAssign.getSysWorkflow());
            history.setAssignUserHand(serAssign.getAssignUser());
            history.setAssignHandUserName(serAssign.getAssignUser().getUserName());
            history.setAssignHandStartTime(serAssign.getAssignStartTime());
            history.setAssignHandEndTime(serAssign.getAssignEndTime());
            history.setAssignHandTimeout(serAssign.getAssignTimeout());
            // todo set assign judge status
            history.setAssignHandStatus("");
            history.setHandExamination(serHandExamination);
            history.setHandWorkflow(sysWorkflow);
            history.setHandDevice(handDevice);
            history.setHandResult(BackgroundServiceUtil.dictConvert(handSerResult.getResult()));
            history.setHandUser(sysUser);
            history.setHandTaskResult(serCheckResult.getHandTaskResult());
            history.setHandGoods(serCheckResult.getHandGoods());
            history.setHandGoodsGrade(serCheckResult.getHandGoodsGrade());
            history.setHandCollectSign(serCheckResult.getHandCollectSign());
            history.setHandAttached(serCheckResult.getHandAttached());
            history.setHandCollectLabel(serCheckResult.getHandCollectLabel());
            history.setHandAppraise(serCheckResult.getHandAppraise());
            historyRepository.save(history);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 4.3.3.12 后台服务向手检站推送业务数据
     *
     * @param taskNumber
     * @return serManImageInfo
     */
    @Override
    public SerManImageInfoModel sendScanInfoToHand(String taskNumber) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            //get the current task
            SerTask taskModel = new SerTask();
            taskModel.setTaskNumber(taskNumber);
            Example<SerTask> serTaskEx = Example.of(taskModel);
            SerTask serTask = serTaskRepository.findOne(serTaskEx);

            // get the assign data from redis
            String assignInfoStr = "";
            int i = 0;
            while (StringUtils.isBlank(assignInfoStr)) {
                i++;
                assignInfoStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.manual.assign.task.info") + taskNumber);
                if (StringUtils.isBlank(assignInfoStr)) {
                    Thread.sleep(1000);
                }
                if (i == 15) {
                    throw new Exception("There is no assignable hand checker for this task now.");
                }
            }
            DispatchManualDeviceInfoVO assignInfo = objectMapper.readValue(assignInfoStr, DispatchManualDeviceInfoVO.class);
            SysDevice sysDeviceModel = SysDevice.builder()
                    .deviceSerial(assignInfo.getRecheckNumber())
                    .build();
            Example<SysDevice> sysDeviceEx = Example.of(sysDeviceModel);
            SysDevice assignedHandDevice = sysDeviceRepository.findOne(sysDeviceEx);

            // get the before scan data from redis
            String key = "dev.service.image.info" + serTask.getTaskNumber();
            String serManImageInfoStr = redisUtil.get(key);
            serManImageInfoStr = CryptUtil.decrypt(serManImageInfoStr);
            SerManImageInfoModel serManImageInfo = objectMapper.readValue(serManImageInfoStr, SerManImageInfoModel.class);
            serManImageInfo.setGuid(assignedHandDevice.getGuid());
            return serManImageInfo;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new SerManImageInfoModel();
    }

}
