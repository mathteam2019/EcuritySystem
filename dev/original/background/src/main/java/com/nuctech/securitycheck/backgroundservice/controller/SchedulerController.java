package com.nuctech.securitycheck.backgroundservice.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerPlatformCheckParams;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceType;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.CryptUtil;
import com.nuctech.securitycheck.backgroundservice.common.utils.RedisUtil;
import com.nuctech.securitycheck.backgroundservice.common.vo.MonitoringVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.ResultMessageVO;
import com.nuctech.securitycheck.backgroundservice.common.vo.SysMonitoringDeviceStatusInfoVO;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.service.ISerPlatformCheckParamsService;
import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * SchedulerController
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-11-29
 */
@Api(tags = "SchedulerController")
@RestController
@Slf4j
@RequestMapping(value = "api/schedule/")
public class SchedulerController {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISerPlatformCheckParamsService iSerPlatformCheckParamsService;

    private ZabbixApi zabbixApi;

    /**
     * 后台服务向安检仪推送工作超时提醒
     */
    @Scheduled(cron = "0 * * * * ?")
    @ApiOperation("4.3.1.17 后台服务向安检仪推送工作超时提醒")
    @PostMapping("monitor-security-overtime")
    public void monitorSecurityOvertime() {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.dev.overtime"));

        MonitoringVO result = new MonitoringVO();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            SerPlatformCheckParams checkParamsTest = iSerPlatformCheckParamsService.getLastCheckParams();
            redisUtil.set(BackgroundServiceUtil.getConfig("sys.setting.checkparams"),
                    CryptUtil.encrypt(objectMapper.writeValueAsString(checkParamsTest)));

            String dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("sys.setting.checkparams")));
            SerPlatformCheckParams checkParams = objectMapper.readValue(dataStr, SerPlatformCheckParams.class);

            dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info")));
            JSONArray dataContent = JSONArray.parseArray(dataStr);
            List<SysMonitoringDeviceStatusInfoVO> monitorList;
            monitorList = dataContent.toJavaList(SysMonitoringDeviceStatusInfoVO.class);

            List<SysMonitoringDeviceStatusInfoVO> deviceListToRest = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
            for (SysMonitoringDeviceStatusInfoVO item : monitorList) {
                if (item.getDeviceType().equals(DeviceType.SECURITY.getValue()) && item.getLoginTime() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime loginTime = LocalDateTime.fromDateFields(item.getLoginTime());
                    if (checkParams != null) {
                        Long overTime = checkParams.getScanOverTime();
                        if (loginTime.plusMinutes(overTime.intValue()).isBefore(now)) {
                            deviceListToRest.add(item);
                        }
                    }
                }
            }
            if (deviceListToRest.size() > 0) {
                result.setMsgContent(BackgroundServiceUtil.getConfig("notify.message.overtime"));
                result.setDeviceListToRest(deviceListToRest);
                resultMessageVO.setContent(result);
                messageSender.cronJobSecurityOvertime(CryptUtil.encrypt(objectMapper.writeValueAsString(result)));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 后台服务向判图站推送工作超时提醒
     */
    @Scheduled(cron = "0 * * * * ?")
    @ApiOperation("4.3.2.12 后台服务向判图站推送工作超时提醒")
    @PostMapping("monitor-judge-overtime")
    public void monitorJudgeOvertime() {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.rem.overtime"));

        MonitoringVO result = new MonitoringVO();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("sys.setting.checkparams")));

            SerPlatformCheckParams checkParamsTest = iSerPlatformCheckParamsService.getLastCheckParams();
            redisUtil.set(BackgroundServiceUtil.getConfig("sys.setting.checkparams"),
                    CryptUtil.encrypt(objectMapper.writeValueAsString(checkParamsTest)));

            SerPlatformCheckParams checkParams = objectMapper.readValue(dataStr, SerPlatformCheckParams.class);
            List<SysMonitoringDeviceStatusInfoVO> monitorList;

            dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info")));
            JSONArray dataContent = JSONArray.parseArray(dataStr);
            monitorList = dataContent.toJavaList(SysMonitoringDeviceStatusInfoVO.class);

            List<SysMonitoringDeviceStatusInfoVO> deviceListToRest = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
            for (SysMonitoringDeviceStatusInfoVO item : monitorList) {
                if (item.getDeviceType().equals(DeviceType.JUDGE.getValue()) && item.getLoginTime() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime loginTime = LocalDateTime.fromDateFields(item.getLoginTime());
                    if (checkParams != null) {
                        Long overTime = checkParams.getJudgeScanOvertime();
                        if (loginTime.plusMinutes(overTime.intValue()).isBefore(now)) {
                            deviceListToRest.add(item);
                        }
                    }
                }
            }
            if (deviceListToRest.size() > 0) {
                result.setMsgContent(BackgroundServiceUtil.getConfig("notify.message.overtime"));
                result.setDeviceListToRest(deviceListToRest);
                resultMessageVO.setContent(result);
                messageSender.cronJobJudgeOvertime(CryptUtil.encrypt(objectMapper.writeValueAsString(result)));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 后台服务向手检端推送工作超时提醒
     */
    @Scheduled(cron = "0 * * * * ?")
    @ApiOperation("4.3.3.13 后台服务向手检端推送工作超时提醒")
    @PostMapping("monitor-manual-overtime")
    public void monitorManualOvertime() {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.sys.man.overtime"));

        MonitoringVO result = new MonitoringVO();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<SysMonitoringDeviceStatusInfoVO> monitorList;

            SerPlatformCheckParams checkParamsTest = iSerPlatformCheckParamsService.getLastCheckParams();
            redisUtil.set(BackgroundServiceUtil.getConfig("sys.setting.checkparams"),
                    CryptUtil.encrypt(objectMapper.writeValueAsString(checkParamsTest)));

            String dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("sys.setting.checkparams")));
            SerPlatformCheckParams checkParams = objectMapper.readValue(dataStr, SerPlatformCheckParams.class);

            dataStr = CryptUtil.decrypt(redisUtil.get(BackgroundServiceUtil.getConfig("redisKey.sys.monitoring.device.status.info")));
            JSONArray dataContent = JSONArray.parseArray(dataStr);
            monitorList = dataContent.toJavaList(SysMonitoringDeviceStatusInfoVO.class);

            List<SysMonitoringDeviceStatusInfoVO> deviceListToRest = new ArrayList<SysMonitoringDeviceStatusInfoVO>();
            for (SysMonitoringDeviceStatusInfoVO item : monitorList) {
                if (item.getDeviceType().equals(DeviceType.MANUAL.getValue()) && item.getLoginTime() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime loginTime = LocalDateTime.fromDateFields(item.getLoginTime());
                    if (checkParams != null) {
                        Long overTime = checkParams.getHandOverTime();
                        if (loginTime.plusMinutes(overTime.intValue()).isBefore(now)) {
                            deviceListToRest.add(item);
                        }
                    }
                }
            }
            if (deviceListToRest.size() > 0) {
                result.setMsgContent(BackgroundServiceUtil.getConfig("notify.message.overtime"));
                result.setDeviceListToRest(deviceListToRest);
                resultMessageVO.setContent(result);
                messageSender.cronJobHandOvertime(CryptUtil.encrypt(objectMapper.writeValueAsString(result)));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Monitor Server Free Disk Space from Zabbix
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void monitorServerFreeSpace() {
        ResultMessageVO resultMessageVO = new ResultMessageVO();
        resultMessageVO.setKey(BackgroundServiceUtil.getConfig("routingKey.zabbix"));

        String msg = "";
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String url = BackgroundServiceUtil.getConfig("zabbix.server.url");

            zabbixApi = new DefaultZabbixApi(url);
            zabbixApi.init();

            boolean login = zabbixApi.login(BackgroundServiceUtil.getConfig("zabbix.server.username"),
                    BackgroundServiceUtil.getConfig("zabbix.server.password"));

            String host = BackgroundServiceUtil.getConfig("zabbix.server.hostname");
            JSONObject filter = new JSONObject();
            filter.put("host", new String[]{host});
            Request getRequest = RequestBuilder.newBuilder().method("host.get")
                    .paramEntry("filter", filter).build();
            JSONObject getResponse = zabbixApi.call(getRequest);
            String hostid = getResponse.getJSONArray("result").getJSONObject(0).getString("hostid");


            JSONObject itemFilter = new JSONObject();
            itemFilter.put("hostid", hostid);
            itemFilter.put("key_", "vfs.fs.size[/,pfree]");

            Request getRequestItem = RequestBuilder.newBuilder().method("item.get").paramEntry("filter", itemFilter).build();
            JSONObject getResponseItem = zabbixApi.call(getRequestItem);
            JSONArray result = getResponseItem.getJSONArray("result");
            JSONObject obj = result.getJSONObject(0);

            Double curFreeSpacePercentage = NumberUtils.createDouble(obj.get("lastvalue").toString());
            Double setting = NumberUtils.createDouble(BackgroundServiceUtil.getConfig("server.storage.limitSpace"));
            if (setting.compareTo(curFreeSpacePercentage) > 0) {
                msg = BackgroundServiceUtil.getConfig("notify.message.lowdiskspace");
                resultMessageVO.setContent(msg);
                messageSender.cronJobLowDiskSpace(CryptUtil.encrypt(objectMapper.writeValueAsString(resultMessageVO)));
            }
            zabbixApi.destroy();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
