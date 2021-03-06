package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.alibaba.fastjson.JSONArray;
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
import com.nuctech.securitycheck.backgroundservice.repositories.*;
import com.nuctech.securitycheck.backgroundservice.service.ISerPlatformCheckParamsService;
import com.nuctech.securitycheck.backgroundservice.service.ISerPlatformOtherParamsService;
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
import java.util.stream.Collectors;

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

    @Autowired
    private ISerPlatformCheckParamsService serPlatformCheckParamsService;

    @Autowired
    private ISerPlatformOtherParamsService serPlatformOtherParamsService;

    @Autowired
    private SysDeviceDetailRepository sysDeviceDetailRepository;

    @Autowired
    private SysRoleResourceRepository sysRoleResourceRepository;

    @Autowired
    private SysRoleUserRepository sysRoleUserRepository;

    @Autowired
    private SysUserGroupRoleRepository sysUserGroupRoleRepository;

    @Autowired
    private SysUserGroupUserRepository sysUserGroupUserRepository;

    /**
     * 通过判断设备和手动检查点获取所有安全设备信息
     * findSecurityInfoList
     *
     * @return List<SysSecurityInfo>
     */
    @Override
    public List<SysSecurityInfoVO> findSecurityInfoList() {
        List<SysSecurityInfoVO> result = new ArrayList<SysSecurityInfoVO>();
        //获取所有设备列表
        List<SysDevice> sysDeviceList = sysDeviceRepository.findAll();
        for (SysDevice item : sysDeviceList) {
            if (item.getDeviceType() != null && DeviceType.SECURITY.getValue().equals(item.getDeviceType())) {
                SysSecurityInfoVO sysSecurityInfoVO = new SysSecurityInfoVO();
                sysSecurityInfoVO.setGuid(item.getGuid());
                sysSecurityInfoVO.setDeviceId(item.getDeviceId());
                sysSecurityInfoVO.setDeviceName(item.getDeviceName());
                sysSecurityInfoVO.setDeviceSerial(item.getDeviceSerial());
                sysSecurityInfoVO.setCurrentStatus(item.getCurrentStatus());
                sysSecurityInfoVO.setWorkStatus(item.getWorkStatus());

                //获取每个设备的设备配置
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

                // 获取安全设备的判断设备列表和手检站列表
                List<SysJudgeInfoVO> sysJudgeInfoVOS = new ArrayList<SysJudgeInfoVO>();
                List<SysManualInfoVO> sysManualInfoVOS = new ArrayList<SysManualInfoVO>();

                if (sysDeviceConfig != null) {
                    // 设置判断器信息
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
                                if (LogType.LOGIN.getValue().equals(serLoginInfo.getLoginCategory())) {
                                    sysJudgeInfoVO.setLogInedUserId(serLoginInfo.getUserId());
                                }
                            }

                            sysJudgeInfoVOS.add(sysJudgeInfoVO);
                        }
                    }

                    // 设置手检查点信息
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
                                if (LogType.LOGIN.getValue().equals(serLoginInfo.getLoginCategory())) {
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
                    if (LogType.LOGIN.getValue().equals(serLoginInfo.getLoginCategory())) {
                        sysSecurityInfoVO.setLogInedUserId(serLoginInfo.getUserId());
                    }
                }

                result.add(sysSecurityInfoVO);
            }
        }

        return result;
    }

    @Override
    public List<SysJudgeInfoVO> findJudgeInfoList() {
        //获取所有设备列表
        Date startTime = new Date();
        List<SysJudgeDevice> sysDeviceList = sysJudgeDeviceRepository.findAll();
        List<SysJudgeInfoVO> sysJudgeInfoVOS = new ArrayList<SysJudgeInfoVO>();
        for (SysJudgeDevice item : sysDeviceList) {
                // 获取安全设备的判断设备列表和手检站列表
            SysDevice judgeDevice = item.getDevice();
            if (judgeDevice != null) {
                SysJudgeInfoVO sysJudgeInfoVO = new SysJudgeInfoVO();
                sysJudgeInfoVO.setGuid(judgeDevice.getGuid());
                sysJudgeInfoVO.setDeviceId(judgeDevice.getDeviceId());
                sysJudgeInfoVO.setDeviceName(judgeDevice.getDeviceName());
                sysJudgeInfoVO.setDeviceSerial(judgeDevice.getDeviceSerial());
                sysJudgeInfoVO.setCurrentStatus(judgeDevice.getCurrentStatus());
                sysJudgeInfoVO.setWorkStatus(judgeDevice.getWorkStatus());
                sysJudgeInfoVO.setDeviceCheckerGender(item.getDeviceCheckGender());
                sysJudgeInfoVO.setConfigInfo(null);
                SerLoginInfo serLoginInfo = item.getLoginInfo();
                if (serLoginInfo != null) {
                    if (LogType.LOGIN.getValue().equals(serLoginInfo.getLoginCategory())) {
                        sysJudgeInfoVO.setLogInedUserId(serLoginInfo.getUserId());
                    }
                }

                sysJudgeInfoVOS.add(sysJudgeInfoVO);
            }
        }
        Date endTime = new Date();
        long calTime = endTime.getTime() - startTime.getTime();

        return sysJudgeInfoVOS;
    }

    @Override
    public List<SysManualInfoVO> findManualInfoList() {
        //获取所有设备列表
        List<SysManualDevice> sysDeviceList = sysManualDeviceRepository.findAll();
        List<SysManualInfoVO> sysManualInfoVOS = new ArrayList<SysManualInfoVO>();
        for (SysManualDevice item : sysDeviceList) {

            SysDevice manualDevice = item.getDevice();
            if (manualDevice != null) {
                SysManualInfoVO sysManualInfoVO = new SysManualInfoVO();
                sysManualInfoVO.setGuid(manualDevice.getGuid());
                sysManualInfoVO.setDeviceId(manualDevice.getDeviceId());
                sysManualInfoVO.setDeviceName(manualDevice.getDeviceName());
                sysManualInfoVO.setDeviceSerial(manualDevice.getDeviceSerial());
                sysManualInfoVO.setCurrentStatus(manualDevice.getCurrentStatus());
                sysManualInfoVO.setWorkStatus(manualDevice.getWorkStatus());
                sysManualInfoVO.setDeviceCheckerGender(item.getDeviceCheckGender());
                sysManualInfoVO.setConfigInfo(null);
                SerLoginInfo serLoginInfo = item.getLoginInfo();
                if (serLoginInfo != null) {
                    if (LogType.LOGIN.getValue().equals(serLoginInfo.getLoginCategory())) {
                        sysManualInfoVO.setLogInedUserId(serLoginInfo.getUserId());
                    }
                }
                sysManualInfoVOS.add(sysManualInfoVO);
            }
        }

        return sysManualInfoVOS;
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
     * 分配手检查点
     * @param guid:
     * @param taskInfoVO: 检查的用户信息
     * @return resultMessage
     * @throws Exception
     */
    @Override
    public DispatchManualDeviceInfoVO dispatchManual(String guid, TaskInfoVO taskInfoVO) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

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

        String settingModelStr = serTask.getModeConfig();

        Integer timeLimit = 0;

        String identityKey = "";

        // 对应的安检仪
        SysDevice securityDevice = serTask.getDevice();

        if(securityDevice.getFieldId() != null) {
            identityKey = securityDevice.getFieldId().toString();
        } else {
            identityKey = securityDevice.getGuid();
        }


        try {

            SerAssign assign = new SerAssign();
            SerTask task = SerTask.builder()
                    .taskId(taskInfoVO.getTaskId()).build();
            //SysWorkflow workflow = SysWorkflow.builder().workflowId(securityDeviceModel.getConfigInfo().getModeId()).build();
            SerDeviceConfigSettingModel serDeviceConfigSettingModel = objectMapper.readValue(settingModelStr, SerDeviceConfigSettingModel.class);
            SysDeviceConfig deviceConfigInfo = serDeviceConfigSettingModel.getDeviceConfig();
            Date assignStartTime = DateUtil.getCurrentDate();

            loop:
            while (true) {

                ObjectMapper objectRedisMapper = new ObjectMapper();
                String dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.manual.info")));

                JSONArray dataContent = JSONArray.parseArray(dataStr);
                if(dataContent == null) {
                    continue;
                }
                List<SysManualInfoVO> manualDeviceModelList = dataContent.toJavaList(SysManualInfoVO.class);

                //是否已分派
                DispatchManualDeviceInfoVO dispatchManualDeviceInfoVO = isManualDeviceDispatched(taskInfoVO.getTaskNumber());
                if (dispatchManualDeviceInfoVO != null) {
                    return dispatchManualDeviceInfoVO;
                }
                for (SysManualInfoVO manualModel : manualDeviceModelList) {
                    boolean isExist = false;
                    for(Long manualDeviceId: serDeviceConfigSettingModel.getManualDeviceIdList()) {
                        if(manualDeviceId.equals(manualModel.getDeviceId())) {
                            isExist = true;
                        }
                    }
                    if(isExist == false) {
                        continue;
                    }

                    //是否有资源
                    if ((DeviceStatusType.LOGIN.getValue().equals(manualModel.getCurrentStatus()) ||
                            DeviceStatusType.START.getValue().equals(manualModel.getCurrentStatus())) &&
                            DeviceWorkStatusType.FREE.getValue().equals(manualModel.getWorkStatus())) {
                        //引导员性别匹配
                        if (genderCheck(manualModel.getDeviceCheckerGender(), taskInfoVO, deviceConfigInfo)) {
                            boolean lockResult = redisUtil.aquirePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.device.current.status") + manualModel.getGuid(),
                                    taskInfoVO.getTaskNumber(), 0);

                            if(!lockResult) {
                                log.error("任务" + taskInfoVO.getTaskNumber() + "无法检查手检站");
                                //Thread.sleep(100);
                                continue;
                            }
                            ret.setLocalRecheck(false);
                            internalData.setLocalRecheck(false);
                            ret.setRecheckNumber(manualModel.getDeviceSerial());
                            internalData.setRecheckNumber(manualModel.getDeviceSerial());

                            //insert & update db
                            //SysWorkflow workflow = SysWorkflow.builder().workflowId(securityDeviceModel.getConfigInfo().getModeId()).build();
                            SysWorkMode workMode = deviceConfigInfo.getSysWorkMode();

                            SysWorkflow sysWorkflowModel = new SysWorkflow();
                            sysWorkflowModel.setSysWorkMode(workMode);
                            sysWorkflowModel.setTaskType(TaskType.HAND.getValue());
                            Example<SysWorkflow> sysWorkflowEx = Example.of(sysWorkflowModel);
                            SysWorkflow sysWorkflow = sysWorkflowRepository.findOne(sysWorkflowEx);
                            SysDevice manualDeviceModel = new SysDevice();
                            manualDeviceModel.setDeviceId(manualModel.getDeviceId());
                            Example<SysDevice> manualDeviceEx = Example.of(manualDeviceModel);
                            SysDevice manualDevice = sysDeviceRepository.findOne(manualDeviceEx);
                            manualDevice.setCurrentStatus(manualModel.getCurrentStatus());
                            manualDevice.setWorkStatus(DeviceWorkStatusType.BUSY.getValue());
                            //sysDeviceRepository.save(manualDevice);


                            serTask.setTaskStatus(TaskStatusType.HAND.getValue());
                            serTaskRepository.save(serTask);
                            SysUser logInedUserModel = SysUser.builder()
                                    .userId(manualModel.getLogInedUserId()).build();
                            Example<SysUser> logInedUserEx = Example.of(logInedUserModel);
                            SysUser logInedUser = sysUserRepository.findOne(logInedUserEx);

                            assign.setSerTask(task);
                            assign.setSysWorkflow(sysWorkflow);
                            assign.setSysWorkMode(workMode);
                            assign.setAssignUser(logInedUser);
                            assign.setAssignHandDevice(manualDevice);
                            assign.setAssignEndTime(DateUtil.getCurrentDate());
                            assign.setAssignStartTime(assignStartTime);
                            assign.setAssignTimeout(TimeDefaultType.FALSE.getValue());

                            serAssignRepository.save(assign);


                            manualModel.setWorkStatus(DeviceWorkStatusType.BUSY.getValue());

                            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.manual.info"),
                                    CryptUtil.encrypt(objectRedisMapper.writeValueAsString(manualDeviceModelList)),
                                    CommonConstant.EXPIRE_TIME.getValue());

                            internalData.setAssignId(assign.getAssignId());
                            redisUtil.set(BackgroundServiceUtil.getConfig(
                                    "redisKey.sys.manual.assign.guid.info") + manualDevice.getDeviceId(),
                                    CryptUtil.encrypt(objectRedisMapper.writeValueAsString(internalData)), CommonConstant.EXPIRE_TIME.getValue());

                            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.manual.assign.task.info") + taskInfoVO.getTaskNumber(),
                                    CryptUtil.encrypt(objectRedisMapper.writeValueAsString(internalData)), CommonConstant.EXPIRE_TIME.getValue());

                            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.manual.assign.task.status.info") + taskInfoVO.getTaskNumber(),
                                    DeviceDefaultType.TRUE.getValue(), CommonConstant.EXPIRE_TIME.getValue());
                            break loop;
                        }
                    }
                }

                redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.manual.assign.task.status.info") + taskInfoVO.getTaskNumber(),
                        DeviceDefaultType.FALSE.getValue(), CommonConstant.EXPIRE_TIME.getValue());
//                redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.manual.process.running.info") + identityKey,
//                        taskInfoVO.getTaskNumber());

                //Thread.sleep(1000);
            }

            log.info("任务" + taskInfoVO.getTaskNumber() + "是分配手检站");
//            redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.manual.process.running.info") + identityKey,
//                    taskInfoVO.getTaskNumber());
        } catch (Exception e) {
            log.error("未能分配手检端");
            log.error(e.getMessage());
            e.printStackTrace();
            ret.setRecheckNumber(null);
            ret.setLocalRecheck(null);
            internalData.setLocalRecheck(null);
            internalData.setRecheckNumber(null);
        }

        return ret;
    }

    /**
     * @param gender: 登录用户性别
     * @param taskInfoVO: 检查的用户信息
     * @return boolean : is able to check
     */
    public Boolean genderCheck(String gender, TaskInfoVO taskInfoVO, SysDeviceConfig deviceConfig) {
        String checkerGender = gender;
        String configGender = GenderType.MALE_AND_FEMALE.getValue();
        String customerGender = "";

        // 得到性别
        if (DeviceGenderType.MALE.getValue().equals(taskInfoVO.getCustomerGender())) {
            customerGender = GenderType.MALE.getValue();
        } else if (DeviceGenderType.FEMALE.getValue().equals(taskInfoVO.getCustomerGender())) {
            customerGender = GenderType.FEMALE.getValue();
        }
        Boolean ret;
        if (GenderType.MALE.getValue().equals(checkerGender)) {
            configGender = deviceConfig.getManManualGender();
        } else if (GenderType.FEMALE.getValue().equals(checkerGender)) {
            configGender = deviceConfig.getWomanManualGender();
        }

        if (GenderType.MALE_AND_FEMALE.getValue().equals(configGender))
            ret = true;
        else {
            if (configGender.equals(customerGender))
                ret = true;
            else
                ret = false;
        }
        return ret;
    }

    @Override
    public boolean genderCheckSecurity(String gender, String guid) {
        try {
            SysDevice securityDeviceModel = SysDevice.builder().guid(guid).build();
            Example<SysDevice> securityDeviceEx = Example.of(securityDeviceModel);
            SysDevice securityDevice = sysDeviceRepository.findOne(securityDeviceEx);

            SysDeviceConfig securityDeviceConfigModel = SysDeviceConfig.builder().sysDevice(securityDevice).build();
            Example<SysDeviceConfig> securityDeviceConfigEx = Example.of(securityDeviceConfigModel);
            SysDeviceConfig securityDeviceConfig = sysDeviceConfigRepository.findOne(securityDeviceConfigEx);
            if (!(DeviceInfoDefaultType.TRUE.getValue().equals(securityDeviceConfig.getManualSwitch()))) {
                return false;
            }


            SerLoginInfo loginInfo = serLoginInfoRepository.findLatestLoginInfo(securityDevice.getDeviceId());
            SysUser loginSecurityUserModel = SysUser.builder().userId(loginInfo.getUserId()).build();
            Example<SysUser> loginSecurityUserEx = Example.of(loginSecurityUserModel);
            SysUser loginSecurityUser = sysUserRepository.findOne(loginSecurityUserEx);
            String possibleGender;
            if(GenderType.MALE.getValue().equals(loginSecurityUser.getGender())) {
                possibleGender = securityDeviceConfig.getManDeviceGender();
            } else {
                possibleGender = securityDeviceConfig.getWomanDeviceGender();
            }
            if(GenderType.MALE_AND_FEMALE.getValue().equals(possibleGender)) {
                return true;
            }

            if(GenderType.MALE.getValue().equals(possibleGender)) {
                if(DeviceGenderType.MALE.getValue().equals(gender)) {
                    return true;
                }
            }

            if(GenderType.FEMALE.getValue().equals(possibleGender)) {
                if(DeviceGenderType.FEMALE.getValue().equals(gender)) {
                    return true;
                }
            }
        } catch(Exception ex) {

        }
        return false;


    }


    /**
     * 检查分配是否存在
     * @param taskNumber
     * @return
     */
    @Override
    public DispatchManualDeviceInfoVO isManualDeviceDispatched(String taskNumber) {
        try {
            String assignInfoStr = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.manual.assign.task.info") + taskNumber);
            if(StringUtils.isNotBlank(assignInfoStr)) {
                ObjectMapper objectMapper = new ObjectMapper();
                DispatchManualDeviceInfoVO assignInfo = objectMapper.readValue(assignInfoStr, DispatchManualDeviceInfoVO.class);
                return assignInfo;
            }
            return null;
        } catch(Exception ex) {
            return null;
        }
    }

    /**
     *
     * 注册设备
     * @param sysDevice
     * @param sysRegisterModel
     * @return booelan
     */
    @Override
    public boolean register(SysDevice sysDevice, SysRegisterModel sysRegisterModel) {
        try {
            if(!(DeviceStatusType.UNREGISTER.getValue().equals(sysDevice.getCurrentStatus()))) {
                return false;
            }
            // sys_device
            sysDevice.setCurrentStatus(DeviceStatusType.REGISTER.getValue());
            sysDevice.setDeviceIP(sysRegisterModel.getIp());
            sysDevice.setSoftwareVersion(sysRegisterModel.getSoftwareVersion());
            sysDevice.setAlgorithmVersion(sysRegisterModel.getAlgorithmVersion());
            sysDeviceRepository.save(sysDevice);

            //ser_platform_check
            savePlatformCheck();

            // ser_device_register
            SerDeviceRegister serDeviceRegister = new SerDeviceRegister();
            serDeviceRegister.setDeviceId(sysDevice.getDeviceId());
            serDeviceRegister.setDeviceId(sysDevice.getDeviceId());
            serDeviceRegister.setUnRegisterTime(null);
            serDeviceRegister.setRegisterTime(DateUtil.stringDateToDate(sysRegisterModel.getTime()));
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
            serDeviceStatus.setAccount(null);
            serDeviceStatus.setLoginTime(null);
            serDeviceStatus.setDeviceLoginTime(DateUtil.stringDateToDate(sysRegisterModel.getTime()));
            serDeviceStatus.setCurrentWorkflow(DeviceCurrentFlowNameType.LIFE_LOGIN_WAIT.getValue());
            serDeviceStatus.setCurrentStatus(DeviceCurrentFlowStatusType.WAIT_TO_START.getValue());
            serDeviceStatus.setDeviceOnline(DeviceOnlineType.ONLINE.getValue());
            serDeviceStatusRepository.save(serDeviceStatus);

            if (DeviceType.JUDGE.getValue().equals(sysDevice.getDeviceType())) {
                SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                sysJudgeDevice.setJudgeDeviceId(sysDevice.getDeviceId());
                sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                if (sysJudgeDevice != null) {
                    sysJudgeDevice.setDeviceStatus(DeviceStatusType.REGISTER.getValue());
                    sysJudgeDeviceRepository.save(sysJudgeDevice);
                }
            }

            if (DeviceType.MANUAL.getValue().equals(sysDevice.getDeviceType())) {
                SysManualDevice sysManualDevice = new SysManualDevice();
                sysManualDevice.setManualDeviceId(sysDevice.getDeviceId());
                sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                if (sysManualDevice != null) {
                    sysManualDevice.setDeviceStatus(DeviceStatusType.REGISTER.getValue());
                    sysManualDeviceRepository.save(sysManualDevice);
                }
            }
            redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.device.current.status") + sysDevice.getGuid());

            return true;
        } catch (Exception e) {
            log.error("无法注册设备");
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
            if(DeviceStatusType.UNREGISTER.getValue().equals(sysDevice.getCurrentStatus())) {
                return false;
            }
            sysDevice.setCurrentStatus(DeviceStatusType.UNREGISTER.getValue());
            sysDeviceRepository.save(sysDevice);

            if (DeviceType.JUDGE.getValue().equals(sysDevice.getDeviceType())) {
                SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                sysJudgeDevice.setJudgeDeviceId(sysDevice.getDeviceId());
                sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                if (sysJudgeDevice != null) {
                    sysJudgeDevice.setDeviceStatus(DeviceStatusType.UNREGISTER.getValue());
                    sysJudgeDevice.setDeviceCheckGender(null);
                    sysJudgeDeviceRepository.save(sysJudgeDevice);
                }
            }

            if (DeviceType.MANUAL.getValue().equals(sysDevice.getDeviceType())) {
                SysManualDevice sysManualDevice = new SysManualDevice();
                sysManualDevice.setManualDeviceId(sysDevice.getDeviceId());
                sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                if (sysManualDevice != null) {
                    sysManualDevice.setDeviceStatus(DeviceStatusType.UNREGISTER.getValue());
                    sysManualDevice.setDeviceCheckGender(null);
                    sysManualDeviceRepository.save(sysManualDevice);
                }
            }



            SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
            serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
            serDeviceStatus = serDeviceStatusRepository.findOne(Example.of(serDeviceStatus));
            if (serDeviceStatus == null) {
                return false;
            }

            SerDeviceRegister serDeviceRegister = serDeviceRegisterRepository.findLatestRegisterInfo(sysDevice.getDeviceId());
            serDeviceRegister.setUnRegisterTime(DateUtil.stringDateToDate(sysUnregisterModel.getTime()));
            serDeviceRegisterRepository.save(serDeviceRegister);
            serDeviceStatus.setAccount(null);
            serDeviceStatus.setDeviceLoginTime(null);
            serDeviceStatus.setLoginTime(null);
            serDeviceStatus.setCurrentWorkflow(DeviceCurrentFlowNameType.LIFE_EXIT.getValue());
            serDeviceStatus.setCurrentStatus(DeviceCurrentFlowStatusType.WAIT_TO_START.getValue());
            serDeviceStatusRepository.save(serDeviceStatus);
            redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.device.current.status") + sysDevice.getGuid());
            return true;
        } catch (Exception e) {
            log.error("设备登录错误");
            log.error(e.getMessage());
        }
        return false;
    }

    private void savePlatformCheck() {
        SerPlatformCheckParams serPlatformCheckParams = serPlatformCheckParamsService.getLastCheckParams();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String serPlatformCheckParamsStr = objectMapper.writeValueAsString(serPlatformCheckParams);
            serPlatformCheckParamsStr = CryptUtil.encrypt(serPlatformCheckParamsStr);
            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.setting.platform.check"),
                    serPlatformCheckParamsStr, CommonConstant.EXPIRE_TIME.getValue());
        } catch(Exception ex) {
            log.error("未能转换为字符串");
            ex.printStackTrace();
        }
    }

    /**
     * login
     *
     * @param sysDevice
     * @param sysLoginModel
     * @return boolean
     */
    @Override
    public int login(SysDevice sysDevice, SysLoginModel sysLoginModel) {
        try {
            SysUser sysUser = new SysUser();
            sysUser.setUserAccount(sysLoginModel.getUserName());

            sysUser = sysUserRepository.findOne(Example.of(sysUser));
            if (sysUser == null) {
                return 0;
            }
            String password = sysUser.getPassword();
            if(password.equals(BackgroundServiceUtil.getConfig("default.password"))) {
                password = serPlatformOtherParamsService.getLastOtherParams().getInitialPassword();
            }
            if(!(password.equals(sysLoginModel.getPassword()))) {
                return 1;
            }
            if(sysUser.getStatus().equals(UserStatusType.BLOCKED.getValue())) {
                return 2;
            }
            Long resourceId = null;
            if (DeviceType.SECURITY.getValue().equals(sysDevice.getDeviceType())) {
                resourceId = Long.valueOf(UserDeviceManageResourceType.DEVICE.getValue());
            } else if (DeviceType.MANUAL.getValue().equals(sysDevice.getDeviceType())) {
                resourceId = Long.valueOf(UserDeviceManageResourceType.MANUAL.getValue());
            } else if (DeviceType.JUDGE.getValue().equals(sysDevice.getDeviceType())) {
                resourceId = Long.valueOf(UserDeviceManageResourceType.JUDGE.getValue());
            }

            SysRoleResource sysRoleResource = SysRoleResource.builder().resourceId(resourceId).build();
            Example<SysRoleResource> ex = Example.of(sysRoleResource);
            List<SysRoleResource> sysRoleResourceList = sysRoleResourceRepository.findAll(ex);
            List<Long> roleIds = sysRoleResourceList.stream().map(x -> x.getRoleId()).collect(Collectors.toList());
            List<SysRoleUser> roleUsers = sysRoleUserRepository.getRoleUserListByUserId(sysUser.getUserId(), roleIds);
            if(roleUsers.size() == 0)  {
                List<SysUserGroupRole> roleUserGroups = sysUserGroupRoleRepository.getRoleUserGroupList(roleIds);
                List<Long> userGroupIds = roleUserGroups.stream().map(x -> x.getUserGroupId()).collect(Collectors.toList());
                if(sysUserGroupUserRepository.getGroupUserListByUserId(sysUser.getUserId(), userGroupIds).size() == 0) {
                    return 0;
                }
            }


            if(checkDeviceLogin(sysDevice) == 3) {
                SerLoginInfo serLoginInfo = serLoginInfoRepository.findLatestLoginInfo(sysDevice.getDeviceId());
                if(serLoginInfo != null) {
                    if(serLoginInfo.getUserId() != null && !(serLoginInfo.getUserId().equals(sysUser.getUserId()))) {
                        return 0;
                    }
                    return 3;
                }
            }

            //ser_platform_check
            savePlatformCheck();

            // sys_device
            sysDevice.setCurrentStatus(DeviceStatusType.LOGIN.getValue());
            sysDevice.setWorkStatus(DeviceWorkStatusType.FREE.getValue());
            sysDevice.setEditedTime(new Date());
            sysDeviceRepository.save(sysDevice);

            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.judge.process.info") + sysDevice.getGuid(), null);

            // ser_login_info
            SerLoginInfo serLoginInfo = new SerLoginInfo();
            serLoginInfo.setUserId(sysUser.getUserId());
            serLoginInfo.setDeviceId(sysDevice.getDeviceId());
            serLoginInfo.setLoginCategory(LogType.LOGIN.getValue());
            serLoginInfo.setTime(DateUtil.stringDateToDate(sysLoginModel.getLoginTime()));
            serLoginInfo.setLogoutTime(null);
            serLoginInfoRepository.save(serLoginInfo);

            // ser_device_status
            SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
            serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
            serDeviceStatus = serDeviceStatusRepository.findOne(Example.of(serDeviceStatus));
            if (serDeviceStatus == null) {
                return 0;
            }
            serDeviceStatus.setLoginTime(DateUtil.stringDateToDate(sysLoginModel.getLoginTime()));
            serDeviceStatus.setAccount(sysUser.getUserAccount());
            serDeviceStatus.setCurrentWorkflow(DeviceCurrentFlowNameType.DEVICE_INIT.getValue());
            serDeviceStatus.setCurrentStatus(DeviceCurrentFlowStatusType.PREPARING.getValue());
            serDeviceStatus.setDeviceOnline(DeviceOnlineType.ONLINE.getValue());

            serDeviceStatusRepository.save(serDeviceStatus);

            if (DeviceType.JUDGE.getValue().equals(sysDevice.getDeviceType())) {
                SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                sysJudgeDevice.setJudgeDeviceId(sysDevice.getDeviceId());
                sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                if (sysJudgeDevice != null) {
                    sysJudgeDevice.setDeviceCheckGender(sysUser.getGender());
                    sysJudgeDeviceRepository.save(sysJudgeDevice);
                }
            }

            if (DeviceType.MANUAL.getValue().equals(sysDevice.getDeviceType())) {
                SysManualDevice sysManualDevice = new SysManualDevice();
                sysManualDevice.setManualDeviceId(sysDevice.getDeviceId());
                sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                if (sysManualDevice != null) {
                    sysManualDevice.setDeviceCheckGender(sysUser.getGender());
                    sysManualDeviceRepository.save(sysManualDevice);
                }
            }
            redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.device.current.status") + sysDevice.getGuid());
            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.device.overtime.last") + sysDevice.getDeviceId(), null);
            return 3;
        } catch (Exception e) {
            log.error("无法登录设备");
            log.error(e.getMessage());
        }
        return 0;
    }

    /**
     * logout
     *
     * @param deviceId
     * @return boolean
     */
    @Override
    public int logout(Long deviceId) {
        try {

            SysDevice deviceModel = new SysDevice();
            deviceModel.setDeviceId(deviceId);
            Example<SysDevice> serDeviceEx = Example.of(deviceModel);
            SysDevice sysDevice = sysDeviceRepository.findOne(serDeviceEx);

            if((DeviceStatusType.UNREGISTER.getValue().equals(sysDevice.getCurrentStatus()))) {
                return 0;
            }

            // sys_device
            sysDevice.setCurrentStatus(DeviceStatusType.UNREGISTER.getValue());
            sysDeviceRepository.save(sysDevice);
            Date cur_date = new Date();

            // ser_login_info
            SerLoginInfo serLoginInfo = serLoginInfoRepository.findLatestLoginInfo(sysDevice.getDeviceId());
            serLoginInfo.setLoginCategory(LogType.LOGOUT.getValue());
            serLoginInfo.setLogoutTime(cur_date);
            serLoginInfoRepository.save(serLoginInfo);

            SerDeviceRegister serDeviceRegister = serDeviceRegisterRepository.findLatestRegisterInfo(sysDevice.getDeviceId());
            serDeviceRegister.setUnRegisterTime(cur_date);
            serDeviceRegisterRepository.save(serDeviceRegister);

            if (DeviceType.JUDGE.getValue().equals(sysDevice.getDeviceType())) {
                SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                sysJudgeDevice.setJudgeDeviceId(sysDevice.getDeviceId());
                sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                if (sysJudgeDevice != null) {
                    sysJudgeDevice.setDeviceStatus(DeviceStatusType.UNREGISTER.getValue());
                    sysJudgeDevice.setDeviceCheckGender(null);
                    sysJudgeDeviceRepository.save(sysJudgeDevice);
                }
            }

            if (DeviceType.MANUAL.getValue().equals(sysDevice.getDeviceType())) {
                SysManualDevice sysManualDevice = new SysManualDevice();
                sysManualDevice.setManualDeviceId(sysDevice.getDeviceId());
                sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                if (sysManualDevice != null) {
                    sysManualDevice.setDeviceStatus(DeviceStatusType.UNREGISTER.getValue());
                    sysManualDevice.setDeviceCheckGender(null);
                    sysManualDeviceRepository.save(sysManualDevice);
                }
            }


            // ser_device_status
            SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
            serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
            serDeviceStatus = serDeviceStatusRepository.findOne(Example.of(serDeviceStatus));
            if (serDeviceStatus == null) {
                return 1;
            }

            serDeviceStatus.setCurrentWorkflow(DeviceCurrentFlowNameType.LIFE_EXIT.getValue());
            serDeviceStatus.setCurrentStatus(DeviceCurrentFlowStatusType.WAIT_TO_START.getValue());
            serDeviceStatus.setAccount(null);
            serDeviceStatus.setLoginTime(null);
            serDeviceStatus.setDeviceOnline(DeviceOnlineType.OFFLINE.getValue());
            serDeviceStatusRepository.save(serDeviceStatus);
            redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.device.current.status") + sysDevice.getGuid());
            return 2;
        } catch (Exception e) {
            log.error("无法注销设备");
            log.error(e.getMessage());
            return 1;
        }
    }

    /**
     * logout
     *
     * @param sysDevice
     * @param sysLogoutModel
     * @return boolean
     */
    @Override
    public int logout(SysDevice sysDevice, SysLogoutModel sysLogoutModel) {
        try {
            SysUser sysUser = new SysUser();
            sysUser.setUserAccount(sysLogoutModel.getUserName());
            sysUser = sysUserRepository.findOne(Example.of(sysUser));

            if (sysUser == null) {
                return 0;
            }
            if(checkDeviceLogin(sysDevice) != 3) {
                return 1;
            }

            SerLoginInfo serLoginInfo = serLoginInfoRepository.findLatestLoginInfo(sysDevice.getDeviceId());
            if(serLoginInfo == null || !(sysUser.getUserId().equals(serLoginInfo.getUserId()))) {
                return 1;
            }

            // sys_device
            sysDevice.setCurrentStatus(DeviceStatusType.LOGOUT.getValue());
            sysDeviceRepository.save(sysDevice);

            // ser_login_info
            serLoginInfo.setLoginCategory(LogType.LOGOUT.getValue());
            serLoginInfo.setLogoutTime(DateUtil.stringDateToDate(sysLogoutModel.getLogoutTime()));
            serLoginInfoRepository.save(serLoginInfo);

            if (DeviceType.JUDGE.getValue().equals(sysDevice.getDeviceType())) {
                SysJudgeDevice sysJudgeDevice = new SysJudgeDevice();
                sysJudgeDevice.setJudgeDeviceId(sysDevice.getDeviceId());
                sysJudgeDevice = sysJudgeDeviceRepository.findOne(Example.of(sysJudgeDevice));
                if (sysJudgeDevice != null) {
                    sysJudgeDevice.setDeviceStatus(DeviceStatusType.LOGOUT.getValue());
                    sysJudgeDevice.setDeviceCheckGender(null);
                    sysJudgeDeviceRepository.save(sysJudgeDevice);
                }
            }

            if (DeviceType.MANUAL.getValue().equals(sysDevice.getDeviceType())) {
                SysManualDevice sysManualDevice = new SysManualDevice();
                sysManualDevice.setManualDeviceId(sysDevice.getDeviceId());
                sysManualDevice = sysManualDeviceRepository.findOne(Example.of(sysManualDevice));
                if (sysManualDevice != null) {
                    sysManualDevice.setDeviceStatus(DeviceStatusType.LOGOUT.getValue());
                    sysManualDevice.setDeviceCheckGender(null);
                    sysManualDeviceRepository.save(sysManualDevice);
                }
            }


            // ser_device_status
            SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
            serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
            serDeviceStatus = serDeviceStatusRepository.findOne(Example.of(serDeviceStatus));
            if (serDeviceStatus == null) {
                return 1;
            }
            serDeviceStatus.setCurrentWorkflow(DeviceCurrentFlowNameType.LIFE_LOGOUT.getValue());
            serDeviceStatus.setCurrentStatus(DeviceCurrentFlowStatusType.CANCELLED.getValue());
            serDeviceStatus.setAccount(null);
            serDeviceStatus.setLoginTime(null);
            serDeviceStatusRepository.save(serDeviceStatus);
            redisUtil.releasePessimisticLockWithTimeout(BackgroundServiceUtil.getConfig("redisKey.sys.device.current.status") + sysDevice.getGuid());
            return 2;
        } catch (Exception e) {
            log.error("无法注销设备");
            log.error(e.getMessage());
            return 1;
        }
    }

    /**
     * 获取所有设备状态
     * findMonitoringInfoList
     *
     * @return List<SysMonitoringDeviceStatusInfo>
     */
    @Override
    public List<SysMonitoringDeviceStatusInfoVO> findMonitoringInfoList() {
        Date startTime = new Date();
        List<SysMonitoringDeviceStatusInfoVO> result = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
        List<SysDeviceDetail> sysDeviceList = sysDeviceDetailRepository.findAll();
        for (SysDeviceDetail sysDevice : sysDeviceList) {
            SysMonitoringDeviceStatusInfoVO info = new SysMonitoringDeviceStatusInfoVO();

            SerLoginInfoDetail serLoginInfo = sysDevice.getLoginInfo();
            if (serLoginInfo != null) {
                if (serLoginInfo.getLoginCategory() != null && LogType.LOGIN.getValue().equals(serLoginInfo.getLoginCategory())) {
                    SysUser sysUser = serLoginInfo.getSysUser();

                    if (sysUser != null) {
                        info.setLoginUser(sysUser);

                        SerDeviceStatus serDeviceStatus = new SerDeviceStatus();
                        serDeviceStatus.setDeviceId(sysDevice.getDeviceId());
                        serDeviceStatus = sysDevice.getDeviceStatus();
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
        Date endTime = new Date();
        long calTime = endTime.getTime() - startTime.getTime();
        return result;
    }

    @Override
    public boolean checkSecurityHandDevice(String guid) {
        SysDevice deviceModel = new SysDevice();
        deviceModel.setGuid(guid);
        Example<SysDevice> serDeviceEx = Example.of(deviceModel);
        SysDevice securityDevice = sysDeviceRepository.findOne(serDeviceEx);
        if(securityDevice == null) {
            return false;
        }
        SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findLatestConfig(securityDevice.getDeviceId());
        if(sysDeviceConfig == null) {
            return false;
        }
        if(WorkModeType.SECURITY.getValue().equals(sysDeviceConfig.getSysWorkMode().getModeName())) {
            return true;
        }
        return false;
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
                    if (DeviceStatusType.LOGIN.getValue().equals(item.getCurrentStatus())
                            || DeviceStatusType.START.getValue().equals(item.getCurrentStatus())) {
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
                if (DeviceStatusType.LOGIN.getValue().equals(sysDevice.getCurrentStatus())
                        || DeviceStatusType.START.getValue().equals(sysDevice.getCurrentStatus())) {
                    isAvailable = true;
                }
            }
        }
        return isAvailable;
    }

    /**
     * 检查设备运行情况。
     * @param sysDevice
     * @return
     */
    @Override
    public int checkDeviceLogin(SysDevice sysDevice) {
        if(sysDevice == null) {
            return 0;
        }

        if(DeviceStatusType.UNREGISTER.getValue().equals(sysDevice.getCurrentStatus())) {
            return 0;
        } else if(DeviceStatusType.REGISTER.getValue().equals(sysDevice.getCurrentStatus())) {
            return 1;
        } else if(DeviceStatusType.LOGOUT.getValue().equals(sysDevice.getCurrentStatus())) {
            return 2;
        }
        return 3;
    }

}
