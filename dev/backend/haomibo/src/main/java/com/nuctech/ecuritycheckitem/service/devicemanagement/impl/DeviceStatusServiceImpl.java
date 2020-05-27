/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceStatusServiceImpl）
 * 文件名：	DeviceStatusServiceImpl.java
 * 描述：	DeviceStatusService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.models.redis.HardwareStatusModel;
import com.nuctech.ecuritycheckitem.models.redis.SerDeviceStatusModel;
import com.nuctech.ecuritycheckitem.models.redis.SysMonitoringDeviceStatusInfoVO;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.models.simplifieddb.QSerScanSimple;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerScanParamSimple;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerScanSimple;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceStatusService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.RedisUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
@Slf4j
public class DeviceStatusServiceImpl implements DeviceStatusService {

    @Autowired
    SerScanSimpleRepository serScanSimpleRepository;

    @Autowired
    SerDeviceStatusRepository serDeviceStatusRepository;

    @Autowired
    SerPlatformOtherParamRepository serPlatformOtherParamRepository;

    @Autowired
    SerScanParamRepository serScanParamRepository;

    @Autowired
    AuthService authService;

    @Autowired
    RedisUtil redisUtil;

    public static String defaultStatusSort = "device.deviceSerial";

    /**
     * get record list
     * @param deviceStatus
     * @param deviceTrafficSetting
     * @return
     */
    private SerDeviceStatus.MonitorRecord getRecordList(SerDeviceStatus deviceStatus, int deviceTrafficSetting) {
        Date curDate = new Date();
        long times = curDate.getTime();
        long unitMiliSecond = deviceTrafficSetting * 60 * 1000;
        long lastDateTime = (times / unitMiliSecond + 1) * unitMiliSecond;
        long startDateTime = lastDateTime - unitMiliSecond * 10;
        Date[] rangeDate = new Date[20];
        int[] countArray = new int[20];
        for (int i = 0; i <= 10; i++) {
            rangeDate[i] = new Date(startDateTime + unitMiliSecond * i);
            countArray[i] = 0;
        }
        List<SerScanSimple> scanDataList = deviceStatus.getScanList();


        if (scanDataList != null) {
            for (int i = 0; i < scanDataList.size(); i++) {
                SerScanSimple scan = scanDataList.get(i);
                Date scanStartTime = scan.getScanStartTime();
                for (int j = 0; j < 10; j++) {
                    if ((scanStartTime.after(rangeDate[j]) || scanStartTime.equals(rangeDate[j])) && scanStartTime.before(rangeDate[j + 1])) {
                        countArray[j]++;
                    }
                }
            }
        }

        SerDeviceStatus.MonitorRecord result = new SerDeviceStatus.MonitorRecord();
        List<String> timeList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String strTime = formatter.format(rangeDate[i + 1]);
            timeList.add(strTime);
            countList.add(countArray[i] * 10);
        }
        result.setTimeList(timeList);
        result.setCountList(countList);
        return result;
    }

    /**
     * get paginated and filtered device status
     * @param fieldId
     * @param deviceName
     * @param categoryId
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SerDeviceStatus> getDeviceStatusByFilter(Long fieldId, String deviceName, Long categoryId, int currentPage, int perPage) {


        int deviceTrafficSetting = 10;
        int deviceTrafficMiddle = 30;
        int deviceTrafficHigh = 80;
        int storageAlarm = 0;
        List<SerPlatformOtherParams> paramList = serPlatformOtherParamRepository.findAll();
        if (paramList != null && paramList.size() > 0) {
            deviceTrafficMiddle = paramList.get(0).getDeviceTrafficMiddle();
            deviceTrafficHigh = paramList.get(0).getDeviceTrafficHigh();
            deviceTrafficSetting = paramList.get(0).getDeviceTrafficSettings();
        }

        Date curDate = new Date();
        long startDateTime = curDate.getTime() - deviceTrafficSetting * 60 * 1000 * 10;
        Date startDate = new Date(startDateTime);


        QSerDeviceStatus builder = QSerDeviceStatus.serDeviceStatus;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (fieldId != null) {
            predicate.and(builder.device.fieldId.eq(fieldId));
        }
        if (categoryId != null) {
            predicate.and(builder.device.categoryId.eq(categoryId));
        }
        if (!StringUtils.isEmpty(deviceName)) {
            predicate.and(builder.device.deviceName.contains(deviceName));
        }
        predicate.and(builder.device.deviceId.isNotNull());

        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            predicate.and(builder.device.createdBy.in(categoryUser.getUserIdList()).or(builder.device.editedBy.in(categoryUser.getUserIdList())));
        }




        PageRequest pageRequest = PageRequest.of(currentPage, perPage, Sort.by(defaultStatusSort).ascending());
        long total = serDeviceStatusRepository.count(predicate);
        List<SerDeviceStatus> data = serDeviceStatusRepository.findAll(predicate, pageRequest).getContent();



        for (int i = 0; i < data.size(); i++) {
            SerDeviceStatus deviceStatus = data.get(i);
            deviceStatus.setDeviceTrafficHigh(deviceTrafficHigh);
            deviceStatus.setDeviceTrafficMiddle(deviceTrafficMiddle);
            //deviceStatus.setRecord(getRecordList(deviceStatus, deviceTrafficSetting));

            SerScanParamSimple serScanParam = deviceStatus.getSerScanParam();
            if (serScanParam != null) {
                try {
                    String[] splitDiskSpace = deviceStatus.getDiskSpace().split("/");
                    int currentSpace = Integer.parseInt(splitDiskSpace[0]);
                    int totalSpace = Integer.parseInt(splitDiskSpace[1]);
                    Integer deviceStorageAlarm = serScanParam.getDeviceStorageAlarm();
                    Integer deviceStorageAlarmPercent = serScanParam.getDeviceStorageAlarmPercent();
                    if (deviceStorageAlarm != null && currentSpace > deviceStorageAlarm) {
                        storageAlarm = 1;
                    } else if (deviceStorageAlarmPercent != null && currentSpace * 100 > totalSpace * deviceStorageAlarmPercent) {
                        storageAlarm = 2;
                    }
                } catch (Exception ex) {

                }

                deviceStatus.setDeviceStorageAlarm(storageAlarm);
            }
        }
        return new PageResult<>(total, data);
    }

    @Override
    public SerDeviceStatus getDeviceStatusById(Long statusId) {
        QSerDeviceStatus builder = QSerDeviceStatus.serDeviceStatus;




        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());
        predicate.and(builder.statusId.eq(statusId));
        SerDeviceStatus deviceStatus = serDeviceStatusRepository.findOne(predicate).get();
        Date curDate = new Date();

        int deviceTrafficSetting = 10;
        int deviceTrafficMiddle = 30;
        int deviceTrafficHigh = 80;
        int storageAlarm = 0;
        List<SerPlatformOtherParams> paramList = serPlatformOtherParamRepository.findAll();
        if (paramList != null && paramList.size() > 0) {
            deviceTrafficSetting = paramList.get(0).getDeviceTrafficSettings();
            deviceTrafficMiddle = paramList.get(0).getDeviceTrafficMiddle();
            deviceTrafficHigh = paramList.get(0).getDeviceTrafficHigh();
        }
        long times = curDate.getTime();
        long unitMiliSecond = deviceTrafficSetting * 60 * 1000;
        long lastDateTime = (times / unitMiliSecond + 1) * unitMiliSecond;
        long startDateTime = lastDateTime - unitMiliSecond * 10;
        Date startDate = new Date(startDateTime);
        List<SerScanSimple> scanList = StreamSupport
                .stream(serScanSimpleRepository.findAll(QSerScanSimple.serScanSimple.scanDeviceId.eq(deviceStatus.getDeviceId()).and(QSerScanSimple.serScanSimple.scanStartTime.after(startDate))).spliterator(), false)
                .collect(Collectors.toList());
        deviceStatus.setScanList(scanList);

        deviceStatus.setDeviceTrafficHigh(deviceTrafficHigh);
        deviceStatus.setDeviceTrafficMiddle(deviceTrafficMiddle);
        deviceStatus.setRecord(getRecordList(deviceStatus, deviceTrafficSetting));

        SerScanParamSimple serScanParam = deviceStatus.getSerScanParam();
        if (serScanParam != null ) {
            try {
                String[] splitDiskSpace = deviceStatus.getDiskSpace().split("/");
                int currentSpace = Integer.parseInt(splitDiskSpace[0]);
                int totalSpace = Integer.parseInt(splitDiskSpace[1]);
                Integer deviceStorageAlarm = serScanParam.getDeviceStorageAlarm();
                Integer deviceStorageAlarmPercent = serScanParam.getDeviceStorageAlarmPercent();
                if (deviceStorageAlarm != null && currentSpace > deviceStorageAlarm) {
                    storageAlarm = 1;
                } else if (deviceStorageAlarmPercent != null && currentSpace * 100 > totalSpace * deviceStorageAlarmPercent) {
                    storageAlarm = 2;
                }
            } catch (Exception ex) {

            }
            deviceStatus.setDeviceStorageAlarm(storageAlarm);
        }
        return deviceStatus;
    }

    @Override
    public List<SerDeviceStatus> getDeviceDetailByGuidList(String guidListStr) {
        List<SerDeviceStatus> statusList = new ArrayList<>();
        List<HardwareStatusModel> hardwareList = new ArrayList<>();
        Date startDate = new Date();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String redisKey = "sys.device.hardware.info.first";
            Map<Object, Object> hardwareHash = redisUtil.getEntities(redisKey);
            if(hardwareHash != null) {
                hardwareHash.forEach((key, value) -> {
                    try {
                        HardwareStatusModel hardwareStatusModel = objectMapper.readValue(value.toString(), HardwareStatusModel.class);
                        hardwareList.add(hardwareStatusModel);
                    } catch (Exception ex) {}

                });
            }
        }catch (Exception ex) {}
        Date endDate = new Date();
        long difHardware = endDate.getTime() - startDate.getTime();
        //log.error("Redis hardware time is " + difHardware);
        List<SerDeviceStatusModel> statusModelList = new ArrayList<>();
        try {
            String redisKey = "sys.device.current.info.first";
            Map<Object, Object> currentHash = redisUtil.getEntities(redisKey);
            if(currentHash != null) {
                currentHash.forEach((key, value) -> {
                    try {
                        SerDeviceStatusModel deviceStatusModel = objectMapper.readValue(value.toString(), SerDeviceStatusModel.class);
                        statusModelList.add(deviceStatusModel);
                    } catch (Exception ex) {}

                });
            }
        } catch (Exception ex) {}
        endDate = new Date();
        long difCurrent = endDate.getTime() - startDate.getTime();
        //log.error("Redis current time is " + difCurrent);
        List<SysMonitoringDeviceStatusInfoVO> monitorList = new ArrayList<>();
        try {
            String dataStr = redisUtil.get(("sys.monitoring.device.status.info"));
            JSONArray dataContent = JSONArray.parseArray(dataStr);
            monitorList = dataContent.toJavaList(SysMonitoringDeviceStatusInfoVO.class);
        } catch (Exception ex) {}
        endDate = new Date();
        long difMonitoring = endDate.getTime() - startDate.getTime();
        //log.error("Redis monitoring time is " + difMonitoring);
        try {
            String[] guidList = guidListStr.split(",");
            for(String guid: guidList) {
                SerDeviceStatus serDeviceStatus = SerDeviceStatus.builder().build();
                serDeviceStatus.setGuid(guid);
                for(HardwareStatusModel hardwareStatusModel: hardwareList) {
                    if(hardwareStatusModel.getGuid().equals(guid)) {
                        serDeviceStatus.setPlcStatus(hardwareStatusModel.getPLC());
                        serDeviceStatus.setMasterCardStatus(hardwareStatusModel.getCaptureCardMainStatus());
                        serDeviceStatus.setSlaveCardStatus(hardwareStatusModel.getCaptureCardSecondStatus());
                        serDeviceStatus.setServo(hardwareStatusModel.getServoStatus());
                        serDeviceStatus.setEmergencyStop(hardwareStatusModel.getEmergencyStop());
                        serDeviceStatus.setFootWarning(hardwareStatusModel.getFootAlarmOnLine());
                    }
                }
                for(SerDeviceStatusModel statusModel: statusModelList) {
                    if(statusModel.getGuid().equals(guid)) {
                        serDeviceStatus.setCurrentWorkFlow(statusModel.getFlowName());
                        serDeviceStatus.setCurrentStatus(statusModel.getFlowStatus());
                        serDeviceStatus.setDiskSpace(statusModel.getDiskSpace());
                    }
                }

                for(SysMonitoringDeviceStatusInfoVO monitor: monitorList) {
                    if(monitor.getGuid().equals(guid)) {
                        serDeviceStatus.setIpAddress(monitor.getIpAddress());
                        serDeviceStatus.setLoginTime(monitor.getLoginTime());
                        serDeviceStatus.setDeviceLoginTime(monitor.getDeviceLoginTime());
                        if(monitor.getLoginUser() != null) {
                            serDeviceStatus.setAccount(monitor.getLoginUser().getUserAccount());
                        }
                        SysDevice device = SysDevice.builder().build();
                        device.setCurrentStatus(monitor.getCurrentStatus());
                        serDeviceStatus.setDevice(device);
                    }
                }
                statusList.add(serDeviceStatus);
            }
        } catch (Exception ex) {

        }
        endDate = new Date();
        long difComplte = endDate.getTime() - startDate.getTime();
        //log.error("Complete device status time is " + difComplte);
        return statusList;
    }
}
