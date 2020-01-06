package com.nuctech.securitycheck.backgroundservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.enums.UserDeviceManageRoleType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.*;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.repositories.SerDeviceStatusRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SysDeviceRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SysRoleUserRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SysUserRepository;
import com.nuctech.securitycheck.backgroundservice.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    /**
     * 后台服务向安检仪下发配置信息
     *
     * @param guid 设备guid
     */
    @Async
    @ApiOperation("4.3.1.5 后台服务向安检仪下发配置信息")
    @PostMapping("send-dev-config")
    public void sendDeviceConfig(@ApiParam("设备guid") @RequestParam("guid") String guid) {
        String exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.dev");
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.dev.config");

        try {
            SysDevice sysDevice = SysDevice.builder().guid(guid).build();
            sysDevice = sysDeviceService.find(sysDevice);

            if (sysDevice == null) {
                SerDeviceConfigModel serDeviceConfigModel = new SerDeviceConfigModel();
                serDeviceConfigModel.setGuid(guid);
                ResultMessageVO resultMsg = new ResultMessageVO();
                resultMsg.setKey(routingKey);
                SerDeviceConfigModel model = new SerDeviceConfigModel();
                model.setGuid(guid);
                resultMsg.setContent(model);
                messageSender.sendDeviceConfigMessage(resultMsg, exchangeName, routingKey);
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
                serDeviceConfigModel.setMode(sysDeviceConfig.getSysWorkMode().getModeId());
                serDeviceConfigModel.setATRColor(serPlatformCheckParams.getScanRecogniseColour());
                serDeviceConfigModel.setManualColor(serPlatformCheckParams.getHandRecogniseColour());
                serDeviceConfigModel.setDeleteColor(serPlatformCheckParams.getDisplayDeleteSuspicionColour());
                serDeviceConfigModel.setParams(serScanParamModelList);
                ResultMessageVO resultMessageVO = new ResultMessageVO();
                resultMessageVO.setKey(routingKey);
                resultMessageVO.setContent(serDeviceConfigModel);
                messageSender.sendDeviceConfigMessage(resultMessageVO, exchangeName, routingKey);
            }
        } catch (Exception e) {
            log.error("无法发送设备配置");
            log.error(e.getMessage());
            ResultMessageVO resultMsg = new ResultMessageVO();
            resultMsg.setKey(routingKey);
            SerDeviceConfigModel model = new SerDeviceConfigModel();
            model.setGuid(guid);
            resultMsg.setContent(model);
            messageSender.sendDeviceConfigMessage(resultMsg, exchangeName, routingKey);
        }
    }

    /**
     * 后台服务向安检仪下发用户列表
     *
     * @param guid 设备guid
     */
    @Async
    @ApiOperation("4.3.1.6 后台服务向安检仪下发用户列表")
    @PostMapping("send-userlist")
    public void sendUserList(@ApiParam("设备guid") @RequestParam String guid) {

        //设置Rabbitmq的主题密钥和路由密钥
        String exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.dev");
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.dev.userlist");
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
            } else {

                //检查谁可以访问当前设备的角色(role id to access to current device)
                Long roldId = null;
                if (sysDevice.getDeviceType().equals(DeviceType.SECURITY.getValue())) {
                    roldId = Long.valueOf(UserDeviceManageRoleType.DEVICE.getValue());
                } else if (sysDevice.getDeviceType().equals(DeviceType.MANUAL.getValue())) {
                    roldId = Long.valueOf(UserDeviceManageRoleType.MANUAL.getValue());
                } else if (sysDevice.getDeviceType().equals(DeviceType.JUDGE.getValue())) {
                    roldId = Long.valueOf(UserDeviceManageRoleType.JUDGE.getValue());
                }

                // 获得与上述角色相关的用户
                SysRoleUser sysRoleUser = SysRoleUser.builder().roleId(roldId).build();
                Example<SysRoleUser> ex = Example.of(sysRoleUser);
                List<SysRoleUser> roleUserList = sysRoleUserRepository.findAll(ex);
                List<Long> userIds = roleUserList.stream().map(x -> x.getUserId()).collect(Collectors.toList());

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
            }
        } catch (Exception e) {
            log.error("无法发送用户列表");
            log.error(e.getMessage());
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(userModelList);
            content.setUsers(userModelList);
            resultMessageVO.setContent(content);
            messageSender.sendDevUserList(resultMessageVO, exchangeName, routingKey);
        }
    }


    /**
     * 将字典数据上传到rabbitmq
     * @param guid
     * @param deviceType
     */
    public void sendDictionaryData(String guid, String deviceType) {
        String exchangeName = null;
        String routingKey = null;

        if (deviceType.equals(DeviceType.SECURITY.getValue())) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.dev");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.dev.dictionary");
        } else if (deviceType.equals(DeviceType.JUDGE.getValue())) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.rem");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.rem.dictionary");
        } else if (deviceType.equals(DeviceType.MANUAL.getValue())) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.man");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.reply.man.dictionary");
        }

        try {

            //获取查获物和评价判图列表的设备词典数据
            SysDictionaryData checkEntityTemp = SysDictionaryData.builder().dictionaryId(16L).build();
            List<SysDictionaryData> checkList = sysDictionaryDataService.findAll(checkEntityTemp);

            SysDictionaryData imageJudgeTemp = SysDictionaryData.builder().dictionaryId(18L).build();
            List<SysDictionaryData> imageJudgeList = sysDictionaryDataService.findAll(imageJudgeTemp);

            // 从每个字典数据中获取数据代码
            List<Long> checkStrList = checkList.stream().map(x -> x.getDataCode()).collect(Collectors.toList());
            List<Long> imageJudgeStrList = imageJudgeList.stream().map(x -> x.getDataCode()).collect(Collectors.toList());
            String checklist = "";
            for (Long checkDataCode : checkStrList) {
                checklist += String.valueOf(checkDataCode) + ",";
            }

            String imageJudge = "";
            for (Long imageDataCode : imageJudgeStrList) {
                imageJudge += String.valueOf(imageDataCode) + ",";
            }

            // 制作 DictDataModel
            DictDataModel dictDataModel = new DictDataModel();
            dictDataModel.setGuid(guid);
            dictDataModel.setChecklist(StringUtils.stripEnd(checklist, ","));
            dictDataModel.setImageJudge(StringUtils.stripEnd(imageJudge, ","));
            ResultMessageVO resultMessageVO = new ResultMessageVO();
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(dictDataModel);

            messageSender.sendDevDictionary(resultMessageVO, exchangeName, routingKey);
        } catch (Exception e) {
            log.error("无法发送字典数据");
            log.error(e.getMessage());
            List<SysUserModel> userModelList = new ArrayList<SysUserModel>();
            DictDataModel dictDataModel = new DictDataModel();
            dictDataModel.setGuid(guid);
            ResultMessageVO resultMessageVO = new ResultMessageVO();
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(dictDataModel);
            messageSender.sendDevDictionary(resultMessageVO, exchangeName, routingKey);
        }
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
    public void sendDictData(@ApiParam("设备guid") @RequestParam String guid) {
        sendDictionaryData(guid, DeviceType.SECURITY.getValue());
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
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.dev.result"));
        SendMessageModel sendMessageModel = SendMessageModel.builder()
                .guid(serDevJudgeGraphResultModel.getGuid())
                .imageGuid(serDevJudgeGraphResultModel.getImageResult().getImageGuid())
                .result(CommonConstant.RESULT_SUCCESS.getValue())
                .build();
        resultMessageVO.setContent(sendMessageModel);
        messageSender.sendJudgeInfoToSecurity(resultMessageVO);
        log.debug("4.3.1.15 后台服务向安检仪推送判图结论 service finished at" + System.currentTimeMillis() + "params:" +
                "taskNumber:" + serDevJudgeGraphResultModel.getImageResult().getImageGuid() + "guid:" + serDevJudgeGraphResultModel.getGuid());
    }

    /**
     * 后台服务向安检仪推送调度的手检站信息
     *
     * @param
     * @param guid
     * @param taskInfoVo
     * @return
     * @throws Exception
     */
    @Async
    @ApiOperation("4.3.1.16 后台服务向安检仪推送调度的手检站信息")
    @PostMapping("dispatch-manual")
    public ResultMessageVO dispatchManual(String guid, TaskInfoVO taskInfoVO) throws Exception {
        DispatchManualDeviceInfoVO ret = sysDeviceService.dispatchManual(guid, taskInfoVO);
        ObjectMapper objectMapper = new ObjectMapper();
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.dev.dispatch.manual"));
        SendMessageModel sendMessageModel = SendMessageModel.builder()
                .guid(ret.getGuid())
                .imageGuid(ret.getImageGuid())
                .result(CommonConstant.RESULT_SUCCESS.getValue())
                .build();
        resultMessageVO.setContent(sendMessageModel);
        messageSender.sendDispatchManual(CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
        return resultMessageVO;
    }

    @ApiOperation("4.3.1.17 后台服务向安检仪推送工作超时提醒")
    @PostMapping("monitor-security-overtime")
    public void monitorSecurityOvertime(DeviceOvertimeModel checkOvertimeModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.dev.overtime"));

        CommonResultVO result = new CommonResultVO();
        ObjectMapper objectMapper = new ObjectMapper();
        result.setGuid(checkOvertimeModel.getGuid());
        if(checkOvertimeModel.getRemind() == true) {
            result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
        } else {
            result.setResult(CommonConstant.RESULT_FAIL.getValue());
        }
        try {
            resultMessageVO.setContent(result);
            messageSender.cronJobSecurityOvertime(CryptUtil.encrypt(objectMapper.writeValueAsString(result)));
        } catch (Exception e) {
            log.error("随着时间的推移未能监控安全性");
            log.error(e.getMessage());
        }
    }

    @ApiOperation("4.3.1.18 后台服务向设备发送控制信息")
    @PostMapping("monitor-security-remote")
    public void monitorSecurityRemote(SecurityRemoteModel securityRemoteModel) {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.reply.dev.command"));

        CommonResultVO result = new CommonResultVO();
        result.setGuid(securityRemoteModel.getGuid());
        result.setResult(CommonConstant.RESULT_SUCCESS.getValue());
        resultMessageVO.setContent(result);
        messageSender.sendJudgeInfoToSecurity(resultMessageVO);
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PlatformCheckrModifyRequestBody {

        String scanRecogniseColour; //scan Recognize Colour
        Long scanOverTime; //scan OverTime
        Long judgeAssignTime; //judge AssignTime
        Long judgeProcessingTime; //judge Processing Time
        Long judgeScanOvertime; //judge ScanOvertime
        String judgeRecogniseColour; //judge Recognise Colour
        Long handOverTime; //hand OverTime
        String handRecogniseColour; //hand Recognise Colour
        List<String> historyDataStorageList; //history Data Storage List
        List<String> historyDataExportList; //history Data Export List
        Long displayDeleteSuspicion; //display Delete Suspicion
        String displayDeleteSuspicionColour; //display Delete Suspicion Colour

        SerPlatformCheckParams convert2SerPlatformCheckParams() { //create new object from input parameters

            return SerPlatformCheckParams
                    .builder()
                    .scanRecogniseColour(this.getScanRecogniseColour())
                    .scanOverTime(this.getScanOverTime())
                    .judgeAssignTime(this.getJudgeAssignTime())
                    .judgeProcessingTime(this.getJudgeProcessingTime())
                    .judgeScanOvertime(this.getJudgeScanOvertime())
                    .judgeRecogniseColour(this.getJudgeRecogniseColour())
                    .handOverTime(this.getHandOverTime())
                    .handRecogniseColour(this.getHandRecogniseColour())
                    .displayDeleteSuspicion(this.getDisplayDeleteSuspicion())
                    .displayDeleteSuspicionColour(this.getDisplayDeleteSuspicionColour())
                    .build();
        }
    }

    @ApiOperation("保存人员查验")
    @PostMapping("save-checkparam")
    public void changePlatformCheck(@RequestBody @ApiParam("参数") PlatformCheckrModifyRequestBody requestBody){
        SerPlatformCheckParams checkParams = requestBody.convert2SerPlatformCheckParams();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String serPlatformCheckParamsStr = objectMapper.writeValueAsString(checkParams);
            serPlatformCheckParamsStr = CryptUtil.encrypt(serPlatformCheckParamsStr);
            redisUtil.set(BackgroundServiceUtil.getConfig("redisKey.sys.setting.platform.check"),
                    serPlatformCheckParamsStr, CommonConstant.EXPIRE_TIME.getValue());
        } catch(Exception ex) {
            log.error("未能转换为对象");
            ex.printStackTrace();
        }
    }

}
