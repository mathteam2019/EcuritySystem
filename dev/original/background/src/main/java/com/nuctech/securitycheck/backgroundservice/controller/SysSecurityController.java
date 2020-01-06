package com.nuctech.securitycheck.backgroundservice.controller;

import com.nuctech.securitycheck.backgroundservice.common.entity.*;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.enums.UserDeviceManageRoleType;
import com.nuctech.securitycheck.backgroundservice.common.models.*;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.TaskInfoVO;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.repositories.SysRoleUserRepository;
import com.nuctech.securitycheck.backgroundservice.repositories.SysUserRepository;
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
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.dev.config");

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
                SysDeviceConfig sysDeviceConfig = new SysDeviceConfig();
                sysDeviceConfig = sysDeviceConfigService.findLastConfig(deviceId);

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
        String exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.dev");
        String routingKey = BackgroundServiceUtil.getConfig("routingKey.dev.userlist");
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        List<SysUserModel> userModelList = new ArrayList<SysUserModel>();
        SysUserReplyModel content = new SysUserReplyModel();
        content.setGuid(guid);
        try {
            SysDevice sysDevice = SysDevice.builder().guid(guid).build();
            sysDevice = sysDeviceService.find(sysDevice);

            if (sysDevice == null) {
                resultMessageVO.setKey(routingKey);
                resultMessageVO.setContent(userModelList);
                content.setUsers(userModelList);

                resultMessageVO.setContent(content);
                messageSender.sendDevUserList(resultMessageVO, exchangeName, routingKey);
            } else {
                Long roldId = null;
                if (sysDevice.getDeviceType().equals(DeviceType.SECURITY.getValue())) {
                    roldId = Long.valueOf(UserDeviceManageRoleType.DEVICE.getValue());
                } else if (sysDevice.getDeviceType().equals(DeviceType.MANUAL.getValue())) {
                    roldId = Long.valueOf(UserDeviceManageRoleType.MANUAL.getValue());
                } else if (sysDevice.getDeviceType().equals(DeviceType.JUDGE.getValue())) {
                    roldId = Long.valueOf(UserDeviceManageRoleType.JUDGE.getValue());
                }
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
            log.error(e.getMessage());
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(userModelList);
            content.setUsers(userModelList);
            resultMessageVO.setContent(content);
            messageSender.sendDevUserList(resultMessageVO, exchangeName, routingKey);
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
    public void sendDictData(@ApiParam("设备guid") @RequestParam String guid, @ApiParam("设备类型") @RequestParam String deviceType) {
        String exchangeName = null;
        String routingKey = null;
        if (deviceType.equals(DeviceType.SECURITY.getValue())) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.dev");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.dev.dictionary");
        } else if (deviceType.equals(DeviceType.JUDGE.getValue())) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.rem");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.rem.dictionary");
        } else if (deviceType.equals(DeviceType.MANUAL.getValue())) {
            exchangeName = BackgroundServiceUtil.getConfig("topic.inter.sys.man");
            routingKey = BackgroundServiceUtil.getConfig("routingKey.man.dictionary");
        }

        try {
            SysDictionaryData checkEntityTemp = SysDictionaryData.builder().dictionaryId(16L).build();
            List<SysDictionaryData> checkList = sysDictionaryDataService.findAll(checkEntityTemp);

            SysDictionaryData imageJudgeTemp = SysDictionaryData.builder().dictionaryId(18L).build();
            List<SysDictionaryData> imageJudgeList = sysDictionaryDataService.findAll(imageJudgeTemp);

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
            DictDataModel dictDataModel = new DictDataModel();
            dictDataModel.setGuid(guid);
            dictDataModel.setChecklist(StringUtils.stripEnd(checklist, ","));
            dictDataModel.setImageJudge(StringUtils.stripEnd(imageJudge, ","));
            ResultMessageVO resultMessageVO = new ResultMessageVO();
            resultMessageVO.setKey(routingKey);
            resultMessageVO.setContent(dictDataModel);

            messageSender.sendDevDictionary(resultMessageVO, exchangeName, routingKey);
        } catch (Exception e) {
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
     * 后台服务向安检仪推送判图结论
     *
     * @param taskNumber
     * @param guid
     */
    @Async
    @ApiOperation("4.3.1.15 后台服务向安检仪推送判图结论")
    @PostMapping("send-judge-result-to-security-device")
    public void sendJudgeResultToSecurityDevice(@RequestBody @ApiParam("请求报文定义") String taskNumber, String guid) {
        log.debug("4.3.1.15 后台服务向安检仪推送判图结论 service started at" + System.currentTimeMillis() + "params:" +
                "taskNumber:" + taskNumber + "guid:" + guid);
        SerDevJudgeGraphResultModel serDevJudgeGraphResultModel = securityImageInfoService.sendJudgeResultToSecurity(taskNumber);
        serDevJudgeGraphResultModel.setGuid(guid);
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.dev.result"));
        resultMessageVO.setContent(serDevJudgeGraphResultModel);
        messageSender.sendJudgeInfoToSecurity(resultMessageVO);
        log.debug("4.3.1.15 后台服务向安检仪推送判图结论 service finished at" + System.currentTimeMillis() + "params:" +
                "taskNumber:" + taskNumber + "guid:" + guid);
    }

    /**
     * 后台服务向安检仪推送调度的手检站信息
     *
     * @param guid
     * @param taskInfoVO
     * @return
     * @throws Exception
     */
    @Async
    @ApiOperation("4.3.1.16 后台服务向安检仪推送调度的手检站信息")
    @PostMapping("dispatch-manual")
    public ResultMessageVO dispatchManual(String guid, TaskInfoVO taskInfoVO) throws Exception {
        return sysDeviceService.dispatchManual(guid, taskInfoVO);
    }

}
