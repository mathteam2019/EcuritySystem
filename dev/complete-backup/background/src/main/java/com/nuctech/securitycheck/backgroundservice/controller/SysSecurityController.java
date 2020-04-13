package com.nuctech.securitycheck.backgroundservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceDefaultType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.enums.UserDeviceManageResourceType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.repositories.*;
import com.nuctech.securitycheck.backgroundservice.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SysSecurityController
 *
 * @author benja
 * @version v1.0
 * @since 2019-11-29
 */
@EnableAsync
@Api(tags = "SysSecurityController")
@RestController
@Slf4j
@RequestMapping(value = "api/sys-security/")
public class SysSecurityController {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ISysDeviceService sysDeviceService;

    @Autowired
    private ISysDeviceConfigService sysDeviceConfigService;

    @Autowired
    private ISerScanParamsService serScanParamsService;

    @Autowired
    private ISerPlatformCheckParamsService serPlatformCheckParamsService;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private ISysDictionaryDataService sysDictionaryDataService;

    @Autowired
    private ISecurityImageInfoService securityImageInfoService;

    @Autowired
    private SysRoleUserRepository sysRoleUserRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISerPlatformCheckParamsService iSerPlatformCheckParamsService;

    @Autowired
    private SysDeviceRepository sysDeviceRepository;

    @Autowired
    private SerDeviceStatusRepository serDeviceStatusRepository;

    @Autowired
    private ISerMqMessageService serMqMessageService;

    @Autowired
    private SysRoleResourceRepository sysRoleResourceRepository;

    @Autowired
    private SysUserGroupRoleRepository sysUserGroupRoleRepository;

    @Autowired
    private SysUserGroupUserRepository sysUserGroupUserRepository;

    /**
     * 后台服务向安检仪下发配置信息
     *
     * @param guid 设备guid
     */
    @Async
    @ApiOperation("4.3.1.5 后台服务向安检仪下发配置信息")
    @PostMapping("send-dev-config")
    public ResultMessageVO sendDeviceConfig(@ApiParam("设备guid") @RequestParam("guid") String guid) {
        String exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.dev");
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.dev.config");
        ResultMessageVO resultMsg = new ResultMessageVO();
        try {
            SysDevice sysDevice = SysDevice.builder().guid(guid).build();
            sysDevice = sysDeviceService.find(sysDevice);

            if (sysDevice == null) {
                SerDeviceConfigModel serDeviceConfigModel = new SerDeviceConfigModel();
                serDeviceConfigModel.setGuid(guid);
                resultMsg.setKey(routingKey);
                SerDeviceConfigModel model = new SerDeviceConfigModel();
                model.setGuid(guid);
                resultMsg.setContent(model);
                messageSender.sendDeviceConfigMessage(resultMsg, exchangeName, routingKey);
                serMqMessageService.save(resultMsg, 1, model.getGuid(), null,
                        CommonConstant.RESULT_INVALID_DEVICE.getValue().toString());
            } else {
                Long deviceId = sysDevice.getDeviceId();
                SysDeviceConfig sysDeviceConfig = sysDeviceConfigService.findLastConfig(deviceId);

                //获取扫描参数列表
                SerScanParam serScanParam = SerScanParam.builder().deviceId(deviceId).build();
                List<SerScanParam> serScanParamList = serScanParamsService.findAll(serScanParam);
                List<SerScanParamModel> serScanParamModelList = new ArrayList<SerScanParamModel>();
                for (SerScanParam temp : serScanParamList) {
                    SerScanParamModel serScanParamModel = SerScanParamModel.builder()
                            .AirCaliWarnTime(temp.getAirCaliWarnTime())
                            .StandbyTime(temp.getStandByTime())
                            .AlarmSound(temp.getAlarmSound())
                            .PassSound(temp.getPassSound())
                            .PoserrorSound(temp.getPosErrorSound())
                            .StandSound(temp.getStandSound())
                            .ScanSound(temp.getScanSound())
                            .ScanOverUseSound(temp.getScanOverUseSound())
                            .AutoRecognise(temp.getAutoRecognise())
                            .RecognitionRate(temp.getRecognitionRate())
                            .SaveScanData(temp.getSaveScanData())
                            .SaveSuspectData(temp.getSaveSuspectData())
                            .FacialBlurring(temp.getFacialBlurring())
                            .ChestBlurring(temp.getChestBlurring())
                            .HipBlurring(temp.getHipBlurring())
                            .GroinBlurring(temp.getGroinBlurring())
                            .build();
                    serScanParamModelList.add(serScanParamModel);
                }
                SerPlatformCheckParams serPlatformCheckParams = new SerPlatformCheckParams();
                serPlatformCheckParams = serPlatformCheckParamsService.getLastCheckParams();

                SerDeviceConfigModel serDeviceConfigModel = new SerDeviceConfigModel();
                serDeviceConfigModel.setGuid(guid);
                serDeviceConfigModel.setDeviceNumber(sysDevice.getDeviceSerial());
                serDeviceConfigModel.setATRColor(serPlatformCheckParams.getScanRecogniseColour());
                serDeviceConfigModel.setManualColor(serPlatformCheckParams.getJudgeRecogniseColour());
                serDeviceConfigModel.setDeleteColor(serPlatformCheckParams.getDisplayDeleteSuspicionColour());
                serDeviceConfigModel.setParams(serScanParamModelList);
                resultMsg.setKey(routingKey);
                resultMsg.setContent(serDeviceConfigModel);
                messageSender.sendDeviceConfigMessage(resultMsg, exchangeName, routingKey);
                serMqMessageService.save(resultMsg, 1, serDeviceConfigModel.getGuid(), null,
                        CommonConstant.RESULT_SUCCESS.getValue().toString());
            }
        } catch (Exception e) {
            log.error("无法发送设备配置");
            log.error(e.getMessage());

            resultMsg.setKey(routingKey);
            SerDeviceConfigModel model = new SerDeviceConfigModel();
            model.setGuid(guid);
            resultMsg.setContent(model);
            messageSender.sendDeviceConfigMessage(resultMsg, exchangeName, routingKey);
            serMqMessageService.save(resultMsg, 1, model.getGuid(), null,
                    CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue().toString());
            return resultMsg;
        }
        return resultMsg;
    }

    /**
     * 后台服务向安检仪下发用户列表
     *
     * @param guid 设备guid
     */
    @ApiOperation("4.3.1.6 后台服务向安检仪下发用户列表")
    @PostMapping("send-userlist")
    @Async
    public ResultMessageVO sendUserList(@ApiParam("设备guid") @RequestParam String guid) {

        //设置Rabbitmq的主题密钥和路由密钥
        String exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.dev");
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.dev.userlist");
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        List<SysUserModel> userModelList = new ArrayList<SysUserModel>();
        SysUserReplyModel content = new SysUserReplyModel();
        content.setGuid(guid);
        try {
            // 从数据库获取设备(get device data from database)
            SysDevice sysDevice = SysDevice.builder().guid(guid).build();
            sysDevice = sysDeviceService.find(sysDevice);

            if (sysDevice == null) {
                resultMessageVO.setKey(routingKey);
                resultMessageVO.setContent(userModelList);
                content.setUsers(userModelList);

                resultMessageVO.setContent(content);
                messageSender.sendDevUserList(resultMessageVO, exchangeName, routingKey);
                serMqMessageService.save(resultMessageVO, 1, guid, null,
                        CommonConstant.RESULT_INVALID_DEVICE.getValue().toString());
            } else {

                //检查谁可以访问当前设备的角色(role id to access to current device)
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
                List<SysRoleUser> roleUserList = sysRoleUserRepository.getRoleUserList(roleIds);
                List<SysUserGroupRole> userGroupRoleList = sysUserGroupRoleRepository.getRoleUserGroupList(roleIds);
                List<Long> userGroupIds = userGroupRoleList.stream().map(x -> x.getUserGroupId()).collect(Collectors.toList());
                List<SysUserGroupUser> userGroupList = sysUserGroupUserRepository.getUserIdList(userGroupIds);
                List<Long> userIds = roleUserList.stream().map(x -> x.getUserId()).collect(Collectors.toList());
                for(int i = 0; i < userGroupList.size(); i ++) {
                    Long userId = userGroupList.get(i).getUserId();
                    boolean isExist = false;
                    for(int j = 0; j < userIds.size(); j ++) {
                        if(userIds.get(j).equals(userId)) {
                            isExist = true;
                        }
                    }
                    if(isExist == false) {
                        userIds.add(userId);
                    }
                }

                List<SysUser> userList = sysUserRepository.findAll(userIds);
                for (SysUser temp : userList) {
                    SysUserModel entity = new SysUserModel();
                    entity.setLoginName(temp.getUserAccount());
                    entity.setPassword(temp.getPassword());
                    userModelList.add(entity);
                }
                resultMessageVO.setKey(routingKey);
                content.setUsers(userModelList);
                resultMessageVO.setContent(content);
                messageSender.sendDevUserList(resultMessageVO, exchangeName, routingKey);
                serMqMessageService.save(resultMessageVO, 1, guid, null,
                        CommonConstant.RESULT_SUCCESS.getValue().toString());
            }
        } catch (Exception e) {
            log.error("无法发送用户列表");
            log.error(e.getMessage());
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(userModelList);
            content.setUsers(userModelList);
            resultMessageVO.setContent(content);
            messageSender.sendDevUserList(resultMessageVO, exchangeName, routingKey);
            serMqMessageService.save(resultMessageVO, 1, guid, null,
                    CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue().toString());
        }
        return resultMessageVO;
    }


    /**
     * 将字典数据上传到rabbitmq
     * @param guid
     * @param deviceType
     */
    public ResultMessageVO sendDictionaryData(String guid, String deviceType) {
        String exchangeName = null;
        String routingKey = null;
        ResultMessageVO resultMessageVO = new ResultMessageVO();

        if (DeviceType.SECURITY.getValue().equals(deviceType)) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.dev");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.dev.dictionary");
        } else if (DeviceType.JUDGE.getValue().equals(deviceType)) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.rem");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.rem.dictionary");
        } else if (DeviceType.MANUAL.getValue().equals(deviceType)) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.man");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.man.dictionary");
        }

        try {

            //获取查获物和评价判图列表的设备词典数据
            SysDictionaryData checkEntityTemp = SysDictionaryData.builder().dictionaryId(16L).build();
            List<SysDictionaryData> checkList = sysDictionaryDataService.findAll(checkEntityTemp);

            SysDictionaryData imageJudgeTemp = SysDictionaryData.builder().dictionaryId(18L).build();
            List<SysDictionaryData> imageJudgeList = sysDictionaryDataService.findAll(imageJudgeTemp);

            // 从每个字典数据中获取数据代码
            List<SeizedGoodModel> checkDataList = new ArrayList<>();
            for(int i = 0; i < checkList.size(); i ++) {
                SeizedGoodModel goodModel = new SeizedGoodModel();
                goodModel.setGoodCode(checkList.get(i).getDataCode());
                goodModel.setGoodName(checkList.get(i).getDataValue());
                checkDataList.add(goodModel);
            }
            List<Long> imageJudgeStrList = imageJudgeList.stream().map(x -> x.getDataCode()).collect(Collectors.toList());


            String imageJudge = "";
            for (Long imageDataCode : imageJudgeStrList) {
                imageJudge += (imageDataCode) + ",";
            }

            // 制作 DictDataModel
            DictDataModel dictDataModel = new DictDataModel();
            dictDataModel.setGuid(guid);
            dictDataModel.setChecklist(checkDataList);
            dictDataModel.setImageJudge(StringUtils.stripEnd(imageJudge, ","));
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(dictDataModel);

            messageSender.sendDevDictionary(resultMessageVO, exchangeName, routingKey);
            serMqMessageService.save(resultMessageVO, 1, guid, null,
                    CommonConstant.RESULT_SUCCESS.getValue().toString());
        } catch (Exception e) {
            log.error("无法发送字典数据");
            log.error(e.getMessage());
            List<SysUserModel> userModelList = new ArrayList<SysUserModel>();
            DictDataModel dictDataModel = new DictDataModel();
            dictDataModel.setGuid(guid);

            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(dictDataModel);
            messageSender.sendDevDictionary(resultMessageVO, exchangeName, routingKey);
            serMqMessageService.save(resultMessageVO, 1, guid, null,
                    CommonConstant.RESULT_INVALID_LOGIC_DATA.getValue().toString());
        }
        return resultMessageVO;
    }

    /**
     * 后台服务向安检仪下发字典
     *
     * @param guid 设备guid
     * @Param deviceType 设备类型
     */
    @Async
    @ApiOperation("4.3.1.7 后台服务向安检仪下发字典")
    @PostMapping("send-dict-data")
    public ResultMessageVO sendDictData(@ApiParam("设备guid") @RequestParam String guid) {
        return sendDictionaryData(guid, DeviceType.SECURITY.getValue());
    }



    /**
     * 后台服务向安检仪推送判图结论
     *
     * @param serDevJudgeGraphResultModel
     */
    @Async
    @ApiOperation("4.3.1.15 后台服务向安检仪推送判图结论")
    @PostMapping("send-judge-result-to-security-device")
    public void sendJudgeResultToSecurityDevice(@RequestBody @ApiParam("请求报文定义") SerDevJudgeGraphResultModel serDevJudgeGraphResultModel) {
        log.debug("4.3.1.15 后台服务向安检仪推送判图结论 service started at" + System.currentTimeMillis() + "params:" +
                "taskNumber:" + serDevJudgeGraphResultModel.getImageResult().getImageGuid() + "guid:" + serDevJudgeGraphResultModel.getGuid());
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.dev.result"));

        resultMessageVO.setContent(serDevJudgeGraphResultModel);
        messageSender.sendJudgeInfoToSecurity(resultMessageVO);
        serMqMessageService.save(resultMessageVO, 1, serDevJudgeGraphResultModel.getGuid(), serDevJudgeGraphResultModel.getImageResult().getImageGuid(),
                CommonConstant.RESULT_SUCCESS.getValue().toString());
        log.debug("4.3.1.15 后台服务向安检仪推送判图结论 service finished at" + System.currentTimeMillis() + "params:" +
                "taskNumber:" + serDevJudgeGraphResultModel.getImageResult().getImageGuid() + "guid:" + serDevJudgeGraphResultModel.getGuid());
    }

    /**
     * 后台服务向安检仪推送调度的手检站信息
     *
     * @param
     * @param guid
     * @param taskInfoVO
     * @return
     * @throws Exception
     */
    @Async
    @ApiOperation("4.3.1.16 后台服务向安检仪推送调度的手检站信息")
    @PostMapping("dispatch-manual")
    public ResultMessageVO dispatchManual(String guid, TaskInfoVO taskInfoVO) throws Exception {
        DispatchManualDeviceInfoVO ret = sysDeviceService.dispatchManual(guid, taskInfoVO);
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.dev.dispatch.manual"));
        resultMessageVO.setContent(ret);
        messageSender.sendDispatchManual(resultMessageVO);
        serMqMessageService.save(resultMessageVO, 1, guid, taskInfoVO.getTaskNumber(),
                CommonConstant.RESULT_SUCCESS.getValue().toString());
        return resultMessageVO;
    }



    @ApiOperation("4.3.1.18 后台服务向设备发送控制信息")
    @PostMapping("monitor-security-remote")
    public void monitorSecurityRemote(SecurityRemoteModel securityRemoteModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.dev.command"));

        resultMessageVO.setContent(securityRemoteModel);
        messageSender.sendJudgeInfoToSecurity(resultMessageVO);
        serMqMessageService.save(resultMessageVO, 1, securityRemoteModel.getGuid(), null,
                CommonConstant.RESULT_SUCCESS.getValue().toString());
    }

    /**
     * 后台服务向安检仪推送手检结论
     *
     * @param sendSimpleMessageModel
     * @return resultMessage
     */
    @ApiOperation("4.3.1.20 后台服务向安检仪推送手检结论")
    @PostMapping("assign-security-device")
    public ResultMessageVO assignSecurityDevice(@RequestBody @ApiParam("请求报文定义") SendSimpleMessageModel sendSimpleMessageModel) {
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
        resultMessageVO.setContent(sendSimpleMessageModel);
        messageSender.sendSysDevMessage(resultMessageVO, routingKey);
        serMqMessageService.save(resultMessageVO, 1, sendSimpleMessageModel.getGuid(), sendSimpleMessageModel.getImageGuid(),
                CommonConstant.RESULT_SUCCESS.getValue().toString());
        return resultMessageVO;
    }

    @Async
    public void checkHandDevice(String taskNumber) {
        String key = "dev.service.image.info" + taskNumber;
        String devSerImageInfoStr = redisUtil.get(key);
        devSerImageInfoStr = CryptUtil.decrypt(devSerImageInfoStr);
        ObjectMapper objectMapper = new ObjectMapper();
        String guid = "";
        try {
            DevSerImageInfoModel devSerImageInfoModel = objectMapper.readValue(devSerImageInfoStr, DevSerImageInfoModel.class);
            guid = devSerImageInfoModel.getGuid();
            boolean checkGender = sysDeviceService.genderCheckSecurity(devSerImageInfoModel.getImageData().getImageGender(), guid);
            if(checkGender == false) {
                return ;
            }
        } catch(Exception ex) {
            log.error("无法保存判断结果");
            return ;
        }


        String resultAssign = "";

        try {
            while(true) {
                resultAssign = redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.manual.assign.task.status.info") + taskNumber);
                if(StringUtils.isBlank(resultAssign) == true) {
                    //Thread.sleep(100);
                } else {
                    break;
                }
            }
        } catch(Exception ex) {
            log.error("后台服务向安检仪推送调度的手检站信息 报错：" + ex.getMessage());
        }


        if(!(DeviceDefaultType.TRUE.getValue().equals(resultAssign))) {
            //4.3.1.20 后台服务向安检仪推送手检结论
            SendSimpleMessageModel sendSimpleMessageModel = new SendSimpleMessageModel();
            sendSimpleMessageModel.setGuid(guid);
            sendSimpleMessageModel.setImageGuid(taskNumber);
            assignSecurityDevice(sendSimpleMessageModel);
        }
    }




    @ApiOperation("测试注册sys-register")
    @PostMapping("all-sys-register")
    public void allSysRegister(SysRegisterModel SysRegisterModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.register"));

        //CommonResultVO result = new CommonResultVO();
        //result.setGuid(SysRegisterModel.getGuid());
        //result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
        resultMessageVO.setContent(SysRegisterModel);
        messageSender.allSysRegister(resultMessageVO);
    }


}
