package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.constants.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.*;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.repositories.*;
import com.nuctech.securitycheck.backgroundservice.service.ISysDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SysDeviceServiceImpl
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Service
@Transactional
@Slf4j
public class SysDeviceServiceImpl implements ISysDeviceService {

    @Autowired
    private SysDeviceRepository sysDeviceRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SerAssignRepository serAssignRepository;

    @Autowired
    private SerDeviceRegisterRepository serDeviceRegisterRepository;

    @Autowired
    private SerDeviceStatusRepository serDeviceStatusRepository;

    @Autowired
    private SysDeviceConfigRepository sysDeviceConfigRepository;

    @Autowired
    private SysJudgeGroupRepository sysJudgeGroupRepository;

    @Autowired
    private SysManualGroupRepository sysManualGroupRepository;

    @Autowired
    private SysJudgeDeviceRepository sysJudgeDeviceRepository;

    @Autowired
    private SysManualDeviceRepository sysManualDeviceRepository;

    @Autowired
    private SerLoginInfoRepository serLoginInfoRepository;

    @Autowired
    private SysWorkModeRepository sysWorkModeRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SerTaskRepository serTaskRepository;

    @Autowired
    private SysWorkflowRepository sysWorkflowRepository;

    /**
     * findSecurityInfoList
     *
     * @return List<SysSecurityInfo>
     */
    @Override
    public List<SysSecurityInfoVO> findSecurityInfoList() {
        List<SysSecurityInfoVO> result = new ArrayList<SysSecurityInfoVO>();
        List<SysDevice> sysDeviceList = sysDeviceRepository.findAll();
        for (SysDevice item : sysDeviceList) {
            if (item.getDeviceType() != null && item.getDeviceType().equals(DeviceType.SECURITY.getValue())) {
                SysSecurityInfoVO sysSecurityInfoVO = new SysSecurityInfoVO();
                sysSecurityInfoVO.setGuid(item.getGuid());
                sysSecurityInfoVO.setDeviceId(item.getDeviceId());
                sysSecurityInfoVO.setDeviceName(item.getDeviceName());
                sysSecurityInfoVO.setDeviceSerial(item.getDeviceSerial());
                sysSecurityInfoVO.setCurrentStatus(item.getCurrentStatus());
                sysSecurityInfoVO.setWorkStatus(item.getWorkStatus());

                SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findLatestConfig(item.getDeviceId());
                SysDeviceConfigInfoVO sysDeviceConfigInfoVO = new SysDeviceConfigInfoVO();
                if (sysDeviceConfig != null) {
                    sysDeviceConfigInfoVO.setConfigId(sysDeviceConfig.getConfigId());
                    sysDeviceConfigInfoVO.setModeId(sysDeviceConfig.getSysWorkMode() == null ? null : sysDeviceConfig.getSysWorkMode().getModeId());
                    sysDeviceConfigInfoVO.setManualSwitch(sysDeviceConfig.getManualSwitch());
                    sysDeviceConfigInfoVO.setManRemoteGender(sysDeviceConfig.getManRemoteGender());
                    sysDeviceConfigInfoVO.setWomanRemoteGender(sysDeviceConfig.getWomanRemoteGender());
                    sysDeviceConfigInfoVO.setManManualGender(sysDeviceConfig.getManManualGender());
                    sysDeviceConfigInfoVO.setWomanManualGender(sysDeviceConfig.getWomanManualGender());
                    sysDeviceConfigInfoVO.setManDeviceGender(sysDeviceConfig.getManDeviceGender());
                    sysDeviceConfigInfoVO.setWomanDeviceGender(sysDeviceConfig.getWomanDeviceGender());

                    if (sysDeviceConfig.getSysWorkMode() != null) {
                        SysWorkMode sysWorkMode = new SysWorkMode();
                        sysWorkMode.setModeId(sysDeviceConfig.getSysWorkMode().getModeId());
                        sysWorkMode = sysWorkModeRepository.findOne(Example.of(sysWorkMode));
                        if (sysWorkMode != null) {
                            sysSecurityInfoVO.setWorkMode(sysWorkMode.getModeName());
                        }
                    }
                }
                sysSecurityInfoVO.setConfigInfo(sysDeviceConfigInfoVO);

                List<SysJudgeInfoVO> sysJudgeInfoVOS = new ArrayList<SysJudgeInfoVO>();
                List<SysManualInfoVO> sysManualInfoVOS = new ArrayList<SysManualInfoVO>();

                if (sysDeviceConfig != null) {
                    // judge device list
                    SysJudgeGroup sysJudgeGroup = new SysJudgeGroup();
                    sysJudgeGroup.setConfigId(sysDeviceConfig.getConfigId());
                    List<SysJudgeGroup> sysJudgeGroups = sysJudgeGroupRepository.findAll(Example.of(sysJudgeGroup));
                    for (SysJudgeGroup group : sysJudgeGroups) {
                        SysDevice judgeDevice = sysDeviceRepository.findOne(group.getJudgeDeviceId());
                        if (judgeDevice != null) {
                            SysJudgeInfoVO sysJudgeInfoVO = new SysJudgeInfoVO();
                            sysJudgeInfoVO.setGuid(judgeDevice.getGuid());
                            sysJudgeInfoVO.setDeviceId(judgeDevice.getDeviceId());
                            sysJudgeInfoVO.setDeviceName(judgeDevice.getDeviceName());
                            sysJudgeInfoVO.setDeviceSerial(judgeDevice.getDeviceSerial());
                            sysJudgeInfoVO.setCurrentStatus(judgeDevice.getCurrentStatus());
                            sysJudgeInfoVO.setWorkStatus(judgeDevice.getWorkStatus());

                            SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                            sysJudgeDevice.setJudgeDeviceId(group.getJudgeDeviceId());
                            sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                            if (sysJudgeDevice != null) {
                                sysJudgeInfoVO.setDeviceCheckerGender(sysJudgeDevice.getDeviceCheckGender());
                            }

                            sysJudgeInfoVO.setConfigInfo(null);

                            SerLoginInfo serLoginInfo = serLoginInfoRepository.findLatestLoginInfo(judgeDevice.getDeviceId());
                            if (serLoginInfo != null) {
                                if (serLoginInfo.getLoginCategory().equals(LogType.LOGIN.getValue())) {
                                    sysJudgeInfoVO.setLogInedUserId(serLoginInfo.getUserId());
                                }
                            }

                            sysJudgeInfoVOS.add(sysJudgeInfoVO);
                        }
                    }

                    // manual device list
                    SysManualGroup sysManualGroup = new SysManualGroup();
                    sysManualGroup.setConfigId(sysDeviceConfig.getConfigId());
                    List<SysManualGroup> sysManualGroups = sysManualGroupRepository.findAll(Example.of(sysManualGroup));
                    for (SysManualGroup group : sysManualGroups) {
                        SysDevice manualDevice = sysDeviceRepository.findOne(group.getManualDeviceId());
                        if (manualDevice != null) {
                            SysManualInfoVO sysManualInfoVO = new SysManualInfoVO();
                            sysManualInfoVO.setGuid(manualDevice.getGuid());
                            sysManualInfoVO.setDeviceId(manualDevice.getDeviceId());
                            sysManualInfoVO.setDeviceName(manualDevice.getDeviceName());
                            sysManualInfoVO.setDeviceSerial(manualDevice.getDeviceSerial());
                            sysManualInfoVO.setCurrentStatus(manualDevice.getCurrentStatus());
                            sysManualInfoVO.setWorkStatus(manualDevice.getWorkStatus());

                            SysManualDevice sysManualDevice = new SysManualDevice();
                            sysManualDevice.setManualDeviceId(group.getManualDeviceId());
                            sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                            if (sysManualDevice != null) {
                                sysManualInfoVO.setDeviceCheckerGender(sysManualDevice.getDeviceCheckGender());
                            }

                            sysManualInfoVO.setConfigInfo(null);

                            SerLoginInfo serLoginInfo = serLoginInfoRepository.findLatestLoginInfo(manualDevice.getDeviceId());
                            if (serLoginInfo != null) {
                                if (serLoginInfo.getLoginCategory().equals(LogType.LOGIN.getValue())) {
                                    sysManualInfoVO.setLogInedUserId(serLoginInfo.getUserId());
                                }
                            }

                            sysManualInfoVOS.add(sysManualInfoVO);
                        }
                    }
                }
                sysSecurityInfoVO.setManualDeviceModelList(sysManualInfoVOS);
                sysSecurityInfoVO.setJudgeDeviceModelList(sysJudgeInfoVOS);

                SerLoginInfo serLoginInfo = serLoginInfoRepository.findLatestLoginInfo(item.getDeviceId());
                if (serLoginInfo != null) {
                    if (serLoginInfo.getLoginCategory().equals(LogType.LOGIN.getValue())) {
                        sysSecurityInfoVO.setLogInedUserId(serLoginInfo.getUserId());
                    }
                }

                result.add(sysSecurityInfoVO);
            }
        }

        return result;
    }

    /**
     * 设备端 保存
     *
     * @param sysDevice
     * @return SysDevice
     */
    @Override
    public SysDevice save(SysDevice sysDevice) {
        return sysDeviceRepository.save(sysDevice);
    }

    /**
     * 设备端 查询
     *
     * @param sysDevice
     * @return SysDevice
     */
    @Override
    public SysDevice find(SysDevice sysDevice) {
        Example<SysDevice> ex = Example.of(sysDevice);
        return sysDeviceRepository.findOne(ex);
    }

    /**
     * 设备端 查询
     *
     * @return List<SysDevice>
     */
    @Override
    public List<SysDevice> findAll() {
        return sysDeviceRepository.findAll();
    }

    /**
     * @param guid
     * @param taskInfoVO
     * @return resultMessage
     * @throws Exception
     */
    @Override
    public ResultMessageVO dispatchManual(String guid, TaskInfoVO taskInfoVO) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.dev.dispatch.manual"));

        DispatchManualDeviceInfoVO ret = new DispatchManualDeviceInfoVO();
        DispatchManualDeviceInternalInfoVO internalData = new DispatchManualDeviceInternalInfoVO();

        ret.setGuid(guid);
        ret.setImageGuid(taskInfoVO.getTaskNumber());
        internalData.setGuid(guid);
        internalData.setImageGuid(taskInfoVO.getTaskNumber());

        SerTask taskModel = new SerTask();
        taskModel.setTaskNumber(taskInfoVO.getTaskNumber());
        Example<SerTask> serTaskEx = Example.of(taskModel);
        SerTask serTask = serTaskRepository.findOne(serTaskEx);

        Integer timeLimit = 0;
        try {

            SerAssign assign = new SerAssign();
            SerTask task = SerTask.builder()
                    .taskId(taskInfoVO.getTaskId()).build();
            //SysWorkflow workflow = SysWorkflow.builder().workflowId(securityDeviceModel.getConfigInfo().getModeId()).build();

            Date assignStartTime = new Date();

            loop:
            while (true) {
                ObjectMapper objectRedisMapper = new ObjectMapper();
                String dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.security.info")));

                JSONArray dataContent = JSONArray.parseArray(dataStr);
                List<SysSecurityInfoVO> securityDeviceModelList = dataContent.toJavaList(SysSecurityInfoVO.class);
                //是否已分派
                if (isManualDeviceDispatched(taskInfoVO.getTaskId()) == false) {
                    for (SysSecurityInfoVO securityDeviceModel : securityDeviceModelList) {
                        if (securityDeviceModel.getGuid().equals(guid)) {

                            if (securityDeviceModel.getWorkMode().equals(WorkModeType.SECURITY_MANUAL.getValue()) ||
                                    securityDeviceModel.getWorkMode().equals(WorkModeType.SECURITY_JUDGE_MANUAL.getValue())) {
                                List<SysManualInfoVO> manualDeviceModelList = securityDeviceModel.getManualDeviceModelList();
                                for (SysManualInfoVO manualModel : manualDeviceModelList) {
                                    //是否有资源
                                    if ((manualModel.getCurrentStatus().equals(DeviceStatusType.LOGIN.getValue()) ||
                                            manualModel.getCurrentStatus().equals(DeviceStatusType.START.getValue())) &&
                                            manualModel.getWorkStatus().equals(DeviceWorkStatusType.FREE.getValue())) {
                                        //引导员性别匹配
                                        if (genderCheck(manualModel.getDeviceCheckerGender(), taskInfoVO, securityDeviceModel)) {
                                            ret.setLocalRecheck(false);
                                            ret.setRecheckNumber(manualModel.getDeviceSerial());
                                            internalData.setLocalRecheck(false);
                                            internalData.setRecheckNumber(manualModel.getDeviceSerial());

                                            //insert & update db
                                            //SysWorkflow workflow = SysWorkflow.builder().workflowId(securityDeviceModel.getConfigInfo().getModeId()).build();
                                            SysWorkMode workMode = SysWorkMode.builder()
                                                    .modeId(securityDeviceModel.getConfigInfo().getModeId()).build();

                                            SysWorkflow sysWorkflowModel = new SysWorkflow();
                                            sysWorkflowModel.setSysWorkMode(workMode);
                                            sysWorkflowModel.setTaskType(TaskType.HAND.getValue());
                                            Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
                                            SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);

                                            SysDevice manualDeviceModel = new SysDevice();
                                            manualDeviceModel.setDeviceId(manualModel.getDeviceId());
                                            Example<SysDevice> manualDeviceEx = Example.of(manualDeviceModel);
                                            SysDevice manualDevice = sysDeviceRepository.findOne(manualDeviceEx);
                                            manualDevice.setWorkStatus(DeviceWorkStatusType.BUSY.getValue());

                                            SysUser logInedUser = SysUser.builder()
                                                    .userId(manualModel.getLogInedUserId()).build();

                                            assign.setSerTask(task);
                                            assign.setSysWorkflow(sysWorkflow);
                                            assign.setSysWorkMode(workMode);
                                            assign.setAssignUser(logInedUser);
                                            assign.setAssignHandDevice(manualDevice);
                                            assign.setAssignEndTime(new Date());
                                            assign.setAssignStartTime(assignStartTime);

                                            serAssignRepository.save(assign);
                                            sysDeviceRepository.save(manualDevice);

                                            manualModel.setWorkStatus(DeviceWorkStatusType.BUSY.getValue());
                                            securityDeviceModel.setManualDeviceModelList(manualDeviceModelList);

                                            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.security.info"),
                                                    CryptUtil.encrypt(objectRedisMapper.writeValueAsString(findSecurityInfoList())),
                                                    CommonConstant.EXPIRE_TIME);

                                            internalData.setAssignId(assign.getAssignId());
                                            redisUtil.set(BackgroundServiceUtil.getConfig(
                                                    "redisKey.sys.manual.assign.guid.info") + manualDevice.getDeviceId(),
                                                    CryptUtil.encrypt(objectRedisMapper.writeValueAsString(internalData)), CommonConstant.EXPIRE_TIME);

                                            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.manual.assign.task.info") + taskInfoVO.getTaskNumber(),
                                                    CryptUtil.encrypt(objectRedisMapper.writeValueAsString(internalData)), CommonConstant.EXPIRE_TIME);
                                            break loop;
                                        }
                                    }
                                }

                                //安检仪能否处理
                                if (securityDeviceModel.getConfigInfo().getManualSwitch().equals(DefaultType.TRUE.getValue())) {
                                    String key = "dev.service.image.info" + serTask.getTaskNumber();
                                    String str = CryptUtil.decrypt(redisUtil.get(key));

                                    DevSerImageInfoModel serImgInfoModel = objectMapper.readValue(str, DevSerImageInfoModel.class);
                                    //是否有判图结论
                                    if (serImgInfoModel.getImageResult() != null) {

                                        SerLoginInfo serLoginInfo = serLoginInfoRepository.findLatestLoginInfo(securityDeviceModel.getDeviceId());
                                        SysUser sysUser = new SysUser();
                                        sysUser.setUserId(serLoginInfo.getUserId());
                                        sysUser = sysUserRepository.findOne(Example.of(sysUser));
                                        //引导员性别匹配
                                        if (genderCheck(sysUser.getGender(), taskInfoVO, securityDeviceModel)) {
                                            ret.setLocalRecheck(true);
                                            ret.setRecheckNumber(securityDeviceModel.getDeviceSerial());
                                            internalData.setLocalRecheck(true);
                                            internalData.setRecheckNumber(securityDeviceModel.getDeviceSerial());

                                            //insert & update db
                                            //SysWorkflow workflow = SysWorkflow.builder().workflowId(securityDeviceModel.getConfigInfo().getModeId()).build();
                                            SysWorkMode workMode = SysWorkMode.builder()
                                                    .modeId(securityDeviceModel.getConfigInfo().getModeId()).build();

                                            SysWorkflow sysWorkflowModel = new SysWorkflow();
                                            sysWorkflowModel.setSysWorkMode(workMode);
                                            sysWorkflowModel.setTaskType(TaskType.HAND.getValue());
                                            Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
                                            SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);

                                            SysDevice manualDevice = SysDevice.builder()
                                                    .deviceId(securityDeviceModel.getDeviceId())
                                                    .build();
                                            SysUser logInedUser = SysUser.builder()
                                                    .userId(securityDeviceModel.getLogInedUserId()).build();

                                            assign.setSerTask(task);
                                            assign.setSysWorkflow(sysWorkflow);
                                            assign.setSysWorkMode(workMode);
                                            assign.setAssignUser(logInedUser);
                                            assign.setAssignHandDevice(manualDevice);
                                            assign.setAssignEndTime(new Date());

                                            serAssignRepository.save(assign);

                                            internalData.setAssignId(assign.getAssignId());
                                            redisUtil.set(BackgroundServiceUtil.getConfig(
                                                    "redisKey.sys.manual.assign.guid.info") + securityDeviceModel.getDeviceId(),
                                                    CryptUtil.encrypt(objectRedisMapper.writeValueAsString(internalData)), CommonConstant.EXPIRE_TIME);
                                            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.manual.assign.task.info") + taskInfoVO.getTaskNumber(),
                                                    CryptUtil.encrypt(objectRedisMapper.writeValueAsString(internalData)), CommonConstant.EXPIRE_TIME);

                                            break loop;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            ret.setRecheckNumber(null);
            ret.setLocalRecheck(null);
            internalData.setLocalRecheck(null);
            internalData.setRecheckNumber(null);
        }
        resultMessageVO.setContent(ret);
        messageSender.sendDispatchManual(CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
        return resultMessageVO;
    }

    /**
     * @param gender
     * @param taskInfoVO
     * @return boolean : is able to check
     */
    public Boolean genderCheck(String gender, TaskInfoVO taskInfoVO, SysSecurityInfoVO securityDeviceModel) {
        String checkerGender = gender;
        String configGender = GenderType.MALE_AND_FEMALE.getValue();
        String customerGender = taskInfoVO.getCustomerGender();
        Boolean ret;
        if (checkerGender.equals(GenderType.MALE.getValue())) {
            configGender = securityDeviceModel.getConfigInfo().getManManualGender();
        } else if (checkerGender.equals(GenderType.FEMALE.getValue())) {
            configGender = securityDeviceModel.getConfigInfo().getWomanManualGender();
        }

        if (configGender.equals(GenderType.MALE_AND_FEMALE.getValue()))
            ret = true;
        else {
            if (configGender == customerGender)
                ret = true;
            else
                ret = false;
        }
        return ret;
    }

    public Boolean isManualDeviceDispatched(Long taskId) {
        SerAssign serAssign = new SerAssign();
        SerTask task = new SerTask();
        task.setTaskId(taskId);
        serAssign.setSerTask(task);
        Example<SerAssign> assignExample = Example.of(serAssign);
        SerAssign assign = serAssignRepository.findOne(assignExample);
        if (assign == null || assign.getAssignHandDevice() == null)
            return false;
        return true;
    }

    /**
     * register
     *
     * @param sysDevice
     * @param sysRegisterModel
     * @return booelan
     */
    @Override
    public boolean register(SysDevice sysDevice, SysRegisterModel sysRegisterModel) {
        try {
            // sys_device
            sysDevice.setCurrentStatus(DeviceStatusType.REGISTER.getValue());
            sysDevice.setDeviceIP(sysRegisterModel.getIp());
            sysDevice.setSoftwareVersion(sysRegisterModel.getSoftwareVersion());
            sysDevice.setAlgorithmVersion(sysRegisterModel.getAlgorithmVersion());
            sysDeviceRepository.save(sysDevice);

            // ser_device_register
            SerDeviceRegister serDeviceRegister = new SerDeviceRegister();
            serDeviceRegister.setDeviceId(sysDevice.getDeviceId());
            serDeviceRegister = serDeviceRegisterRepository.findOne(Example.of(serDeviceRegister));
            if (serDeviceRegister == null) {
                serDeviceRegister = new SerDeviceRegister();
                serDeviceRegister.setDeviceId(sysDevice.getDeviceId());
            }
            serDeviceRegister.setDeviceId(sysDevice.getDeviceId());
            serDeviceRegister.setRegisterTime(sysRegisterModel.getTime());
            serDeviceRegisterRepository.save(serDeviceRegister);

            // ser_device_status
            SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
            serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
            serDeviceStatus = serDeviceStatusRepository.findOne(Example.of(serDeviceStatus));
            if (serDeviceStatus == null) {
                serDeviceStatus = new SerDeviceStatus();
                serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
            }
            serDeviceStatus.setIpAddress(sysRegisterModel.getIp());
            serDeviceStatusRepository.save(serDeviceStatus);

            if (sysDevice.getDeviceType().equals(DeviceType.JUDGE.getValue())) {
                SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                sysJudgeDevice.setJudgeDeviceId(sysDevice.getDeviceId());
                sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                if (sysJudgeDevice != null) {
                    sysJudgeDevice.setDeviceStatus(DeviceStatusType.REGISTER.getValue());
                    sysJudgeDeviceRepository.save(sysJudgeDevice);
                }
            }

            if (sysDevice.getDeviceType().equals(DeviceType.MANUAL.getValue())) {
                SysManualDevice sysManualDevice = new SysManualDevice();
                sysManualDevice.setManualDeviceId(sysDevice.getDeviceId());
                sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                if (sysManualDevice != null) {
                    sysManualDevice.setDeviceStatus(DeviceStatusType.REGISTER.getValue());
                    sysManualDeviceRepository.save(sysManualDevice);
                }
            }

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * unRegister
     *
     * @param sysDevice
     * @param sysUnregisterModel
     * @return boolean
     */
    @Override
    public boolean unRegister(SysDevice sysDevice, SysUnregisterModel sysUnregisterModel) {
        try {
            // sys_device
            sysDevice.setCurrentStatus(DeviceStatusType.UNREGISTER.getValue());
            sysDeviceRepository.save(sysDevice);

            if (sysDevice.getDeviceType().equals(DeviceType.JUDGE.getValue())) {
                SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                sysJudgeDevice.setJudgeDeviceId(sysDevice.getDeviceId());
                sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                if (sysJudgeDevice != null) {
                    sysJudgeDevice.setDeviceStatus(DeviceStatusType.UNREGISTER.getValue());
                    sysJudgeDevice.setDeviceCheckGender(null);
                    sysJudgeDeviceRepository.save(sysJudgeDevice);
                }
            }

            if (sysDevice.getDeviceType().equals(DeviceType.MANUAL.getValue())) {
                SysManualDevice sysManualDevice = new SysManualDevice();
                sysManualDevice.setManualDeviceId(sysDevice.getDeviceId());
                sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                if (sysManualDevice != null) {
                    sysManualDevice.setDeviceStatus(DeviceStatusType.UNREGISTER.getValue());
                    sysManualDevice.setDeviceCheckGender(null);
                    sysManualDeviceRepository.save(sysManualDevice);
                }
            }

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * login
     *
     * @param sysDevice
     * @param sysLoginModel
     * @return boolean
     */
    @Override
    public boolean login(SysDevice sysDevice, SysLoginModel sysLoginModel) {
        try {
            SysUser sysUser = new SysUser();
            sysUser.setUserAccount(sysLoginModel.getUserName());
            sysUser.setPassword(sysLoginModel.getPassword());
            sysUser = sysUserRepository.findOne(Example.of(sysUser));

            if (sysUser == null) {
                return false;
            }

            // sys_device
            sysDevice.setCurrentStatus(DeviceStatusType.LOGIN.getValue());
            sysDeviceRepository.save(sysDevice);

            // ser_login_info
            SerLoginInfo serLoginInfo = new SerLoginInfo();
            serLoginInfo.setUserId(sysUser.getUserId());
            serLoginInfo.setDeviceId(sysDevice.getDeviceId());
            serLoginInfo.setLoginCategory(LogType.LOGIN.getValue());
            serLoginInfo.setTime(sysLoginModel.getLoginTime());
            serLoginInfoRepository.save(serLoginInfo);

            // ser_device_status
            SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
            serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
            serDeviceStatus = serDeviceStatusRepository.findOne(Example.of(serDeviceStatus));
            if (serDeviceStatus == null) {
                return false;
            }
            serDeviceStatus.setLoginTime(sysLoginModel.getLoginTime());
            serDeviceStatusRepository.save(serDeviceStatus);

            if (sysDevice.getDeviceType().equals(DeviceType.JUDGE.getValue())) {
                SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                sysJudgeDevice.setJudgeDeviceId(sysDevice.getDeviceId());
                sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                if (sysJudgeDevice != null) {
                    sysJudgeDevice.setDeviceCheckGender(sysUser.getGender());
                    sysJudgeDeviceRepository.save(sysJudgeDevice);
                }
            }

            if (sysDevice.getDeviceType().equals(DeviceType.MANUAL.getValue())) {
                SysManualDevice sysManualDevice = new SysManualDevice();
                sysManualDevice.setManualDeviceId(sysDevice.getDeviceId());
                sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                if (sysManualDevice != null) {
                    sysManualDevice.setDeviceCheckGender(sysUser.getGender());
                    sysManualDeviceRepository.save(sysManualDevice);
                }
            }

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * logout
     *
     * @param sysDevice
     * @param sysLogoutModel
     * @return boolean
     */
    @Override
    public boolean logout(SysDevice sysDevice, SysLogoutModel sysLogoutModel) {
        try {
            SysUser sysUser = new SysUser();
            sysUser.setUserAccount(sysLogoutModel.getUserName());
            sysUser = sysUserRepository.findOne(Example.of(sysUser));

            if (sysUser == null) {
                return false;
            }

            // sys_device
            sysDevice.setCurrentStatus(DeviceStatusType.LOGOUT.getValue());
            sysDeviceRepository.save(sysDevice);

            // ser_login_info
            SerLoginInfo serLoginInfo = new SerLoginInfo();
            serLoginInfo.setUserId(sysUser.getUserId());
            serLoginInfo.setDeviceId(sysDevice.getDeviceId());
            serLoginInfo.setLoginCategory(LogType.LOGOUT.getValue());
            serLoginInfo.setTime(sysLogoutModel.getLogoutTime());
            serLoginInfoRepository.save(serLoginInfo);

            if (sysDevice.getDeviceType().equals(DeviceType.JUDGE.getValue())) {
                SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                sysJudgeDevice.setJudgeDeviceId(sysDevice.getDeviceId());
                sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                if (sysJudgeDevice != null) {
                    sysJudgeDevice.setDeviceStatus(DeviceStatusType.LOGOUT.getValue());
                    sysJudgeDevice.setDeviceCheckGender(null);
                    sysJudgeDeviceRepository.save(sysJudgeDevice);
                }
            }

            if (sysDevice.getDeviceType().equals(DeviceType.MANUAL.getValue())) {
                SysManualDevice sysManualDevice = new SysManualDevice();
                sysManualDevice.setManualDeviceId(sysDevice.getDeviceId());
                sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                if (sysManualDevice != null) {
                    sysManualDevice.setDeviceStatus(DeviceStatusType.LOGOUT.getValue());
                    sysManualDevice.setDeviceCheckGender(null);
                    sysManualDeviceRepository.save(sysManualDevice);
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * findMonitoringInfoList
     *
     * @return List<SysMonitoringDeviceStatusInfo>
     */
    @Override
    public List<SysMonitoringDeviceStatusInfoVO> findMonitoringInfoList() {
        List<SysMonitoringDeviceStatusInfoVO> result = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
        List<SysDevice> sysDeviceList = sysDeviceRepository.findAll();
        for (SysDevice sysDevice : sysDeviceList) {
            SysMonitoringDeviceStatusInfoVO info = new SysMonitoringDeviceStatusInfoVO();

            SerLoginInfo serLoginInfo = serLoginInfoRepository.findLatestLoginInfo(sysDevice.getDeviceId());
            if (serLoginInfo != null) {
                if (serLoginInfo.getLoginCategory() != null && serLoginInfo.getLoginCategory().equals(LogType.LOGIN.getValue())) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserId(serLoginInfo.getUserId());
                    sysUser = sysUserRepository.findOne(Example.of(sysUser));

                    if (sysUser != null) {
                        info.setLoginUser(sysUser);

                        SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
                        serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
                        serDeviceStatus = serDeviceStatusRepository.findOne(Example.of(serDeviceStatus));
                        if (serDeviceStatus != null) {
                            info.setLoginTime(serDeviceStatus.getLoginTime());
                        }
                    }
                }
            }

            info.setGuid(sysDevice.getGuid());
            info.setDeviceId(sysDevice.getDeviceId());
            info.setDeviceType(sysDevice.getDeviceType());
            info.setStatus(sysDevice.getStatus());
            info.setCurrentStatus(sysDevice.getCurrentStatus());
            info.setWorkStatus(sysDevice.getWorkStatus());

            result.add(info);
        }
        return result;
    }

    /**
     * 判断 Guid有效
     */
    @Override
    public boolean checkGuid(String guid) {
        boolean isAvailable = false;
        String deviceListStr = redisUtil.get(BackgroundServiceUtil
                .getConfig("redisKey.sys.monitoring.device.status.info"));
        if (deviceListStr != null) {
            // 判断 guid有效 从 redis
            deviceListStr = CryptUtil.decrypt(deviceListStr);
            JSONArray deviceListJsonArray = JSONArray.parseArray(deviceListStr);
            List<SysMonitoringDeviceStatusInfoVO> deviceList = deviceListJsonArray.toJavaList(SysMonitoringDeviceStatusInfoVO.class);
            for (SysMonitoringDeviceStatusInfoVO item : deviceList) {
                if (item.getGuid().equals(guid)) {
                    if (item.getCurrentStatus().equals(DeviceStatusType.LOGIN.getValue())
                            || item.getCurrentStatus().equals(DeviceStatusType.START.getValue())) {
                        isAvailable = true;
                    }
                }
            }
        } else {
            // redis上没有数据的时候 判断从数据库
            SysDevice deviceModel = new SysDevice();
            deviceModel.setGuid(guid);
            Example<SysDevice> sysDeviceEx = Example.of(deviceModel);
            SysDevice sysDevice = sysDeviceRepository.findOne(sysDeviceEx);
            if (sysDevice != null && sysDevice.getCurrentStatus() != null) {
                if (sysDevice.getCurrentStatus().equals(DeviceStatusType.LOGIN.getValue())
                        || sysDevice.getCurrentStatus().equals(DeviceStatusType.START.getValue())) {
                    isAvailable = true;
                }
            }
        }
        return isAvailable;
    }

}
