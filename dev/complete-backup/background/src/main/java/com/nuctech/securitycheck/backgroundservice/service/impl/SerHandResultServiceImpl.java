package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.*;
import com.nuctech.securitycheck.backgroundservice.common.models.DevSerImageInfoModel;
import com.nuctech.securitycheck.backgroundservice.common.models.HandSerResultModel;
import com.nuctech.securitycheck.backgroundservice.common.models.SerManImageInfoModel;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.DateUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;
import com.nuctech.securitycheck.backgroundservice.repositories.*;
import com.nuctech.securitycheck.backgroundservice.service.ISerHandResultService;
import com.nuctech.securitycheck.backgroundservice.service.ISysDeviceService;
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

    @Autowired
    private ISysDeviceService sysDeviceService;

    @Autowired
    private SerLoginInfoRepository serLoginInfoRepository;

    /**
     * 4.3.3.9 手检站后台服务请求提交手检结论
     *
     * @param handSerResult
     * @return isSucceed
     */
    @Override
    public Boolean saveHandResult(HandSerResultModel handSerResult) {
        String taskNumber = handSerResult.getCheckResult().getImageGuid();
        try {

            ObjectMapper objectMapper = new ObjectMapper();



            SerTask serTaskModel = SerTask.builder()
                    .taskNumber(taskNumber)
                    .build();
            Example<SerTask> serTaskEx = Example.of(serTaskModel);
            SerTask serTask = serTaskRepository.findOne(serTaskEx);

            // 对应的安检仪



            // 获取设备工作流程
            SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findLatestConfig(serTask.getDevice().getDeviceId());
            SysWorkMode sysWorkMode = sysDeviceConfig.getSysWorkMode();
            SysWorkflow sysWorkflowModel = new SysWorkflow();
            sysWorkflowModel.setSysWorkMode(sysWorkMode);
            sysWorkflowModel.setTaskType(TaskType.HAND.getValue());
            Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
            SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);


            // 从数据库获取手检站查点
            String guid = handSerResult.getGuid();
            SysDevice deviceModel = new SysDevice();
            deviceModel.setGuid(guid);
            Example<SysDevice> serDeviceEx = Example.of(deviceModel);
            SysDevice handDevice = sysDeviceRepository.findOne(serDeviceEx);

            //从redis获取设备状态
            String deviceListStr = redisUtil.get(BackgroundServiceUtil
                    .getConfig("redisKey.sys.monitoring.device.status.info"));
            deviceListStr = CryptUtil.decrypt(deviceListStr);
            JSONArray deviceListArray = JSON.parseArray(deviceListStr);
            List<SysMonitoringDeviceStatusInfoVO> deviceList = deviceListArray.toJavaList(SysMonitoringDeviceStatusInfoVO.class);

            // 获取登录用户信息
            SysUser sysUser = null;//new SysUser();

            if (deviceList != null) {
                for (SysMonitoringDeviceStatusInfoVO item : deviceList) {
                    // 检查guid并获取用户信息并将其状态更新为免费
                    if (StringUtils.isNotBlank(item.getGuid()) && item.getGuid().equals(guid)) {
                        sysUser = item.getLoginUser();
                        item.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
                        break;
                    }
                }

                //将设备状态信息更新为Redis
                deviceListStr = objectMapper.writeValueAsString(deviceList);
                deviceListStr = CryptUtil.encrypt(deviceListStr);
                redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info"),
                        deviceListStr, CommonConstant.EXPIRE_TIME.getValue());
            }

            // 从redis获取相应安全设备的设备配置信息
            List<SysSecurityInfoVO> sysSecurityInfoVOS;
            String sysSecurityInfoStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"));
            JSONArray sysSecurityInfoListStr = JSONArray.parseArray(sysSecurityInfoStr);
            sysSecurityInfoVOS = sysSecurityInfoListStr.toJavaList(SysSecurityInfoVO.class);
            List<SysManualInfoVO> manualDeviceList;
            if (sysSecurityInfoVOS != null) {
                for (SysSecurityInfoVO item : sysSecurityInfoVOS) {
                    manualDeviceList = item.getManualDeviceModelList();
                    if (manualDeviceList != null) {
                        for (SysManualInfoVO manualItem : manualDeviceList) {
                            // 检查guid并获取用户信息并将其状态更新为免费
                            if (StringUtils.isNotBlank(manualItem.getDeviceId().toString()) && manualItem.getDeviceId().equals(handDevice.getDeviceId())) {
                                manualItem.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
                                break;
                            }
                        }
                    }
                }
                redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"),
                        CryptUtil.encrypt(objectMapper.writeValueAsString(sysSecurityInfoVOS)), CommonConstant.EXPIRE_TIME.getValue());
            }




            SerHandExamination serHandExaminationModel = SerHandExamination.builder()
                    .serTask(serTask)
                    .build();
            Example<SerHandExamination> serHandExaminatioEx = Example.of(serHandExaminationModel);
            SerHandExamination serHandExaminationOld = serHandExaminationRepository.findOne(serHandExaminatioEx);

            if(serHandExaminationOld != null) {
                return false;
            }



            // 获取分配数据
            SerAssign serAssignModel = SerAssign.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .build();
            Example<SerAssign> serAssignEx = Example.of(serAssignModel);
            SerAssign serAssign = serAssignRepository.findOne(serAssignEx);

            // 将数据保存到ser_hand_examination
            SerHandExamination serHandExamination = SerHandExamination.builder()
                    .serTask(serTask)
                    .sysWorkflow(sysWorkflow)
                    .handDevice(handDevice)
                    .handResult((handSerResult.getCheckResult().getResult()))
                    .handEndTime(DateUtil.getCurrentDate())
                    .handUser(sysUser)
                    .build();
            if(serAssign != null) {
                serHandExamination.setHandStartTime(serAssign.getAssignEndTime());
            }
            serHandExamination = serHandExaminationRepository.save(serHandExamination);

            serTask.setTaskStatus(TaskStatusType.ALL.getValue());
            serTaskRepository.save(serTask);

            //将数据保存到ser_check_result
            String handSubmitRectsStr = objectMapper.writeValueAsString(handSerResult.getCheckResult().getSubmitRects());
            String imageJudgeType = "";
            if (handSerResult.getCheckResult().getImageJudge().equals(DeviceImageJudgeType.MISJUDGE.getValue())) {
                imageJudgeType = ImageJudgeType.MISJUDGE.getValue();
            } else if (handSerResult.getCheckResult().getImageJudge().equals(DeviceImageJudgeType.LEAKJUDGE.getValue())) {
                imageJudgeType = ImageJudgeType.LEAKJUDGE.getValue();
            }
            SerCheckResult serCheckResult = SerCheckResult.builder()
                    .serTask(serTask)
                    .sysDevice(handDevice)
                    .conclusionType(ConclusionType.HAND.getValue())
                    .handTaskResult((handSerResult.getCheckResult().getResult()))
                    .handGoods(handSerResult.getCheckResult().getChecklist())
                    .handCollectSign((handSerResult.getCheckResult().getImageKeep()))
                    .handAppraise(imageJudgeType)
                    .handAttached(handSerResult.getCheckResult().getFiles())
                    .handSubmitRects(handSubmitRectsStr)
                    .build();
            serCheckResult.setNote(handSerResult.getCheckResult().getNote());
            serCheckResultRepository.save(serCheckResult);


            // 将当前设备状态更新为免费
            handDevice.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
            sysDeviceRepository.save(handDevice);



            // 保存到历史
            History historyModel = History.builder()
                    .serTask(serTask)
                    .build();
            Example<History> historyEx = Example.of(historyModel);
            History history = historyRepository.findOne(historyEx);

            if(serAssign != null) {
                history.setAssignHandDevice(serAssign.getAssignHandDevice());
                history.setAssignWorkflowHand(serAssign.getSysWorkflow());
                history.setAssignUserHand(serAssign.getAssignUser());
                history.setAssignHandUserName(serAssign.getAssignUser().getUserName());
                history.setAssignHandStartTime(serAssign.getAssignStartTime());
                history.setAssignHandEndTime(serAssign.getAssignEndTime());
                history.setAssignHandTimeout(serAssign.getAssignTimeout());
                // todo set assign judge status
            }


            history.setAssignHandStatus("");
            history.setHandExamination(serHandExamination);
            history.setHandWorkflow(sysWorkflow);
            history.setHandDevice(handDevice);
            history.setHandResult((handSerResult.getCheckResult().getResult()));
            history.setHandUser(sysUser);
            history.setHandTaskResult(serCheckResult.getHandTaskResult());
            history.setHandGoods(serCheckResult.getHandGoods());
            history.setHandGoodsGrade(serCheckResult.getHandGoodsGrade());
            history.setHandCollectSign(serCheckResult.getHandCollectSign());
            history.setHandAttached(serCheckResult.getHandAttached());
            history.setHandCollectLabel(serCheckResult.getHandCollectLabel());
            history.setHandAppraise(serCheckResult.getHandAppraise());
            history.setHandEndTime(serHandExamination.getHandEndTime());
            history.setHandStartTime(serHandExamination.getHandStartTime());

            //change guid to security device guid
            historyRepository.save(history);

            handSerResult.setGuid(serTask.getDevice().getGuid());
            return true;

        } catch (Exception e) {
            log.error("未能保存手牌结果");
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

            // 获取任务信息
            SerTask taskModel = new SerTask();
            taskModel.setTaskNumber(taskNumber);
            Example<SerTask> serTaskEx = Example.of(taskModel);
            SerTask serTask = serTaskRepository.findOne(serTaskEx);

            //从redis获取分配信息
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

            // 从任务中获取相应的手动设备
            SysDevice sysDeviceModel = SysDevice.builder()
                    .deviceSerial(assignInfo.getRecheckNumber())
                    .build();
            Example<SysDevice> sysDeviceEx = Example.of(sysDeviceModel);
            SysDevice assignedHandDevice = sysDeviceRepository.findOne(sysDeviceEx);

            // 从redis获取扫描数据
            String key = "dev.service.image.result.info" + serTask.getTaskNumber();
            String serManImageInfoStr = redisUtil.get(key);
            serManImageInfoStr = CryptUtil.decrypt(serManImageInfoStr);
            SerManImageInfoModel serManImageInfo = objectMapper.readValue(serManImageInfoStr, SerManImageInfoModel.class);
            serManImageInfo.setGuid(assignedHandDevice.getGuid());
            return serManImageInfo;
        } catch (Exception e) {
            log.error("无法将扫描图像发送到手检端");
            log.error(e.getMessage());
        }
        return new SerManImageInfoModel();
    }

}
